package  m.mcoupledate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.mcoupledate.classes.DropDownMenu.ConstellationAdapter;
import m.mcoupledate.classes.DropDownMenu.DropDownMenu;
import m.mcoupledate.classes.DropDownMenu.ListDropDownAdapter;
import m.mcoupledate.classes.adapters.SiteListAdapter;
import m.mcoupledate.funcs.PinkErrorHandler;


public class SearchSites extends Fragment
{
    public static final int SITETYPE_ATTRACTION = 0, SITETYPE_RESTAURANT = 1;
    public static final int SEARCHTYPE_BROWSE = 0, SEARCHTYPE_MYLIKES = 1;

    private int searchType, siteType;

    private View rootView;

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    RequestQueue mQueue;
    String pinkCon = "http://140.117.71.216/pinkCon/";


    private ListView siteListView;
    private SiteListAdapter siteListAdapter;
    private JSONArray sites;


    public String select_city = "";
    public String select_area = "";

    private ArrayList<String> headers = new ArrayList<String>();
//    String[] city, area;
    List<String[]> tabOptions = new ArrayList<String[]>();


    List<HashMap<String, Object>> menuOption = null;


    DropDownMenu mDropDownMenu;

    private final int SITECLASS_CITY = 1, SITECLASS_AREA = 2;

    public static SearchSites newInstance(int searchType, int siteType)
    {
        SearchSites newInstance = new SearchSites();

        Bundle settings = new Bundle();
        settings.putInt("searchType", searchType);
        settings.putInt("siteType", siteType);

        newInstance.setArguments(settings);

        return newInstance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        searchType = getArguments().getInt("searchType");
        siteType = getArguments().getInt("siteType");

        if (siteType==SITETYPE_ATTRACTION)
            headers.addAll(Arrays.asList(new String[]{"大行政區", "小行政區"}));
        else if  (siteType==SITETYPE_RESTAURANT)
            headers.addAll(Arrays.asList(new String[]{"大行政區", "小行政區", "時段", "種類", "口味"}));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.frament_search_site, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        pref = this.getActivity().getSharedPreferences("pinkpink", 0);
        prefEditor = pref.edit();

        mQueue = Volley.newRequestQueue(this.getActivity());

        mDropDownMenu= (DropDownMenu) getView().findViewById( R.id.dropDownMenu);
        initDropDownMenu();
    }

    private void initDropDownMenu()
    {
        final View contentView = getActivity().getLayoutInflater().inflate(R.layout.dropdownmenu_contentview, null);

        sites = new JSONArray();
        siteListAdapter = new SiteListAdapter(SearchSites.this.getActivity(), sites);
        siteListView = (ListView) contentView.findViewById(R.id.siteListView);
        siteListView.setAdapter(siteListAdapter);

        initTabOption(contentView);

        //该监听回调只监听默认类型，如果是自定义view请自行设置，参照demo
        mDropDownMenu.addMenuSelectListener(new m.mcoupledate.classes.DropDownMenu.DropDownMenu.OnDefultMenuSelectListener() {
            @Override
            public void onSelectDefaultMenu(int index, int pos, String clickstr) {
//                String end = null;
                //index:点击的tab索引，pos：单项菜单中点击的位置索引，clickstr：点击位置的字符串
                switch( index )  /*status 只能為整數、長整數或字元變數.*/
                {
                    case 0:
                        if (clickstr.compareTo(select_city)==0)
                            break;

                        select_city = clickstr;
                        refreshTabOption(clickstr);
                        break;
//                    case 1:
//                        select_area = clickstr;
//                        break;
                }
//                Toast.makeText(getBaseContext(), clickstr, Toast.LENGTH_SHORT).show();
            }
        });

        mDropDownMenu.setListRefresher(
                new DropDownMenu.ListRefresher() {
                    @Override
                    public void refresh(JSONArray jArr)
                    {
                        siteListAdapter.changeData(jArr);
                    }
                }

        );
        mDropDownMenu.setDynamicSearch(this.getActivity(), searchType, siteType);
    }


    private void initTabOption(final View contentView)
    {
        String url = pinkCon+"searchSites_initTabOptions.php?opt="+siteType;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            menuOption = new ArrayList<>();
                            JSONObject tabs = new JSONObject(response);


                            menuOption.add(createOneTabOption(tabs.optJSONArray("city"), DropDownMenu.TYPE_LIST_SIMPLE));
                            menuOption.add(createOneTabOption(null, DropDownMenu.TYPE_GRID));  //  area

                            if (siteType==SITETYPE_RESTAURANT)
                            {
                                menuOption.add(createOneTabOption(tabs.optJSONArray("time"), DropDownMenu.TYPE_LIST_SIMPLE));
                                menuOption.add(createOneTabOption(tabs.optJSONArray("food_kind"), DropDownMenu.TYPE_GRID));
                                menuOption.add(createOneTabOption(tabs.optJSONArray("country"), DropDownMenu.TYPE_GRID));
                            }

                            mDropDownMenu.setDropDownMenu(headers, menuOption, contentView);

                        } catch (Exception e) {
//                            Log.d("HF2345", e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        PinkErrorHandler.retryConnect(rootView, new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                initTabOption(contentView);
//                                startActivity(SiteInfo.this.getIntent());
                            }
                        });
                    }
                });

        mQueue.add(stringRequest);
    }

    private HashMap<String, Object> createOneTabOption(JSONArray aTabOptNames, int menuType)
    {
        if (aTabOptNames==null)
            aTabOptNames = new JSONArray();

        String[] aTabOpts = new String[aTabOptNames.length()+1];
        aTabOpts[0] = "不限";
        for (int a=1; a<(aTabOptNames.length()+1); ++a)
            aTabOpts[a] = aTabOptNames.optJSONObject(a-1).optString("optName");

        HashMap<String, Object> aTabOption = new HashMap<String, Object>();
        aTabOption.put(DropDownMenu.KEY, menuType);
        aTabOption.put(DropDownMenu.VALUE, aTabOpts);

        return aTabOption;
    }


    private void refreshTabOption(final String cityName)
    {
        String url = pinkCon+"searchSites_getAreaTabOptions.php?city="+cityName;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                        try {
                            JSONArray areaTabOptNames = new JSONArray(response);

                            String[] areaTabOpts = new String[areaTabOptNames.length()+1];
                            areaTabOpts[0] = "不限";
                            for (int a=1; a<(areaTabOptNames.length()+1); ++a)
                                areaTabOpts[a] = areaTabOptNames.optJSONObject(a-1).optString("optName");

                            ((ConstellationAdapter)mDropDownMenu.adapterMap.get(1)).changeData(Arrays.asList(areaTabOpts));
                            mDropDownMenu.setTabText(1, "小行政區");

                        } catch (JSONException e) {

                            Log.d("HF1", e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("HF2", error.getMessage());
                    }
                });

        mQueue.add(stringRequest);
    }



    public Boolean onBackPressedAct() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing())
        {
            mDropDownMenu.closeMenu();
            return true;
        }
        else
        {
            getFragmentManager().popBackStack();
            return false;
        }
    }

    public void searchByClasses()
    {
        mDropDownMenu.closeMenu();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, pinkCon+"searchSites_searchByClasses.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("HFSEARCHBYCLASS", response);
                        try
                        {
                            sites = new JSONArray(response);
                            siteListAdapter.changeData(sites);
                        }
                        catch (JSONException e)
                        {   }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 失敗後的動作
                    }
                }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> map = new HashMap<String, String>();

                map.put("siteType", String.valueOf(siteType));
                map.put("searchType", String.valueOf(searchType));
                map.put("city", select_city);
                map.put("area", ((ConstellationAdapter)mDropDownMenu.adapterMap.get(1)).getCheckedListJSONString());

                if (searchType==SEARCHTYPE_MYLIKES)
                    map.put("mId", pref.getString("mId", null));

                if (siteType==SITETYPE_RESTAURANT)
                {
                    map.put("time", ((ListDropDownAdapter)mDropDownMenu.adapterMap.get(2)).getCheckedItem());
                    map.put("food_kind", ((ConstellationAdapter)mDropDownMenu.adapterMap.get(3)).getCheckedListJSONString());
                    map.put("country", ((ConstellationAdapter)mDropDownMenu.adapterMap.get(4)).getCheckedListJSONString());

                    Log.d("HFCOUNTRYANS", ((ConstellationAdapter)mDropDownMenu.adapterMap.get(4)).getCheckedListJSONString());
                }

                return map;
            }
        };

        mQueue.add(stringRequest);
    }
}

