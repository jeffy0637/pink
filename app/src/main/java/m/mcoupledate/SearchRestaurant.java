package  m.mcoupledate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.mcoupledate.classes.DropDownMenu.ConstellationAdapter;
import m.mcoupledate.classes.adapters.SiteListAdapter;


public class SearchRestaurant extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    RequestQueue mQueue;
    String pinkCon = "http://140.117.71.216/pinkCon/";


    private ListView siteListView;
    private SiteListAdapter siteListAdapter;
    private JSONArray sites;


    public String select_city = "";
    public String select_area = "";

    private String headers[] = {"大行政區", "小行政區"};
    String[] city, area;


    List<HashMap<String, Object>> menuOption = null;
    HashMap<String, Object> cityTabOption = null;
    HashMap<String, Object> areaTabOption = null;


    m.mcoupledate.classes.DropDownMenu.DropDownMenu mDropDownMenu;

    private final int SITECLASS_CITY = 1, SITECLASS_AREA = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.frament_search_site, container, false);
        return view;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pref = this.getActivity().getSharedPreferences("pinkpink", 0);
        prefEditor = pref.edit();

        mQueue = Volley.newRequestQueue(this.getActivity());

        mDropDownMenu= (m.mcoupledate.classes.DropDownMenu.DropDownMenu) getView().findViewById( R.id.dropDownMenu);
        initDropDownMenu();
    }

    private void initDropDownMenu() {
        final View contentView = getActivity().getLayoutInflater().inflate(R.layout.dropdownmenu_contentview, null);

        sites = new JSONArray();
        siteListAdapter = new SiteListAdapter(SearchRestaurant.this.getActivity(), sites);
        siteListView = (ListView) contentView.findViewById(R.id.siteListView);
        siteListView.setAdapter(siteListAdapter);

        setTabOption(contentView, null);

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
                        setTabOption(contentView, clickstr);
                        break;
//                    case 1:
//                        select_area = clickstr;
//                        break;
                }
//                Toast.makeText(getBaseContext(), clickstr, Toast.LENGTH_SHORT).show();
            }
        });

        mDropDownMenu.setListRefresher(
                new m.mcoupledate.classes.DropDownMenu.DropDownMenu.ListRefresher() {
                    @Override
                    public void refresh(JSONArray jArr)
                    {
                        siteListAdapter.changeData(jArr);
                    }
                }

        );
    }

    //設置篩選選單
//    private List<HashMap<String, Object>> setMenuOption() {
//
//
//
//        cityTabOption.put(DropDownMenu.KEY, DropDownMenu.TYPE_LIST_SIMPLE);
//        cityTabOption.put(DropDownMenu.VALUE, city.toArray());
//        menuOption.add(cityTabOption);
//
//        areaTabOption.put(DropDownMenu.KEY, DropDownMenu.TYPE_GRID);
//        areaTabOption.put(DropDownMenu.VALUE, area.toArray());
//        menuOption.add(areaTabOption);
//
//        return menuOption;
//    }

    private void setTabOption(final View contentView, final String reFreshParam)
    {
        final String act;
        if (menuOption==null)
            act = "initAttraction";
        else
            act = "refreshArea";

        String url;
        if (act.compareTo("initAttraction")==0)
            url = pinkCon+"getSiteClasses.php?opt="+SITECLASS_CITY;
        else
            url = pinkCon+"getSiteClasses.php?opt="+SITECLASS_AREA+"&city="+reFreshParam;

        Log.d("HFURL", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                        if (act.compareTo("initAttraction")==0)
                        {
                            try {
                                JSONArray jArr = new JSONArray(response);

                                city = new String[jArr.length()+1];
                                area = new String[1];

                                city[0] = "不限";
                                area[0] = "不限";

                                for (int a=1; a<(jArr.length()+1); ++a)
                                    city[a] = jArr.optJSONObject(a-1).optString("oName");


                                menuOption = new ArrayList<>();

                                cityTabOption = new HashMap<String, Object>();
                                areaTabOption = new HashMap<String, Object>();

                                cityTabOption.put(m.mcoupledate.classes.DropDownMenu.DropDownMenu.KEY, m.mcoupledate.classes.DropDownMenu.DropDownMenu.TYPE_LIST_SIMPLE);
                                cityTabOption.put(m.mcoupledate.classes.DropDownMenu.DropDownMenu.VALUE, city);
                                menuOption.add(cityTabOption);

                                areaTabOption.put(m.mcoupledate.classes.DropDownMenu.DropDownMenu.KEY, m.mcoupledate.classes.DropDownMenu.DropDownMenu.TYPE_GRID);
                                areaTabOption.put(m.mcoupledate.classes.DropDownMenu.DropDownMenu.VALUE, area);
                                menuOption.add(areaTabOption);

                                mDropDownMenu.setDropDownMenu(Arrays.asList(headers), menuOption, contentView);

                            } catch (Exception e) {
                                Log.d("HF2345", e.getMessage());
                            }
                        }
                        else
                        {
                            try {
                                JSONArray jArr = new JSONArray(response);

                                area = new String[jArr.length()+1];
                                area[0] = "不限";

                                for (int a=1; a<(jArr.length()+1); ++a)
                                    area[a] = jArr.optJSONObject(a-1).optString("oName");

                                ((ConstellationAdapter)mDropDownMenu.adapterMap.get(1)).changeData(Arrays.asList(area));
                                mDropDownMenu.setTabText(1, "小行政區");

                            } catch (JSONException e) {

                                Log.d("HF1", e.getMessage());
                            }

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


    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    //設置搜尋按鈕
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.topbar_search, menu);
//        ((Toolbar)findViewById(R.id.action_check)).;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId()==R.id.topbarSearch) {

            mDropDownMenu.closeMenu();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, pinkCon+"getMyLikeSites.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("HF", response);
                            try {
                                sites = new JSONArray(response);

                                siteListAdapter.changeData(sites);

                            } catch (JSONException e) {


                            }

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

                    map.put("opt", "attraction");
                    map.put("mId", pref.getString("mId", null));
                    map.put("city", select_city);
                    map.put("area", ((ConstellationAdapter)mDropDownMenu.adapterMap.get(1)).getCheckedListJSONString());

                    Log.d("HF", ((ConstellationAdapter)mDropDownMenu.adapterMap.get(1)).getCheckedListJSONString());


                    return map;
                }
            };

            mQueue.add(stringRequest);


        }

        return super.onOptionsItemSelected(item);
    }
}

