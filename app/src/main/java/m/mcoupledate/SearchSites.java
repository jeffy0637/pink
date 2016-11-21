package  m.mcoupledate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.mcoupledate.classes.DropDownMenu.ConstellationAdapter;
import m.mcoupledate.classes.DropDownMenu.DropDownMenu;
import m.mcoupledate.classes.DropDownMenu.ListDropDownAdapter;
import m.mcoupledate.classes.adapters.SiteListAdapter;
import m.mcoupledate.classes.funcs.PinkCon;


public class SearchSites extends Fragment
{
    public static final int SITETYPE_ATTRACTION = 8341, SITETYPE_RESTAURANT = 8342;
    public static final int SEARCHTYPE_BROWSE = 8341, SEARCHTYPE_MYLIKES = 8342;

    private int searchType, siteType;

    SharedPreferences pref;

    RequestQueue mQueue;
    private PinkCon.InitErrorBar initErrorBar;


    private ListView siteListView;
    private SiteListAdapter siteListAdapter;
    private JSONArray sites;
    private JSONArray initSiteList = null;


    public String select_city = "";

    private ArrayList<String> headers = new ArrayList<String>();


    List<HashMap<String, Object>> menuOption = null;


    DropDownMenu mDropDownMenu;
    private View contentView;

    private View rootView;

    private FloatingActionButton addNewSiteFAB;

    private Boolean ifInitShow;

    private String siteFrom;


    public static SearchSites newInstance(int searchType, int siteType, Boolean ifFirstPage)
    {
        SearchSites newInstance = new SearchSites();

        Bundle settings = new Bundle();
        settings.putInt("searchType", searchType);
        settings.putInt("siteType", siteType);
        settings.putBoolean("ifInitShow", ifFirstPage);

        newInstance.setArguments(settings);


        return newInstance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        searchType = getArguments().getInt("searchType");
        siteType = getArguments().getInt("siteType");
        siteFrom = getArguments().getString("siteFrom");

        if (siteType==SITETYPE_ATTRACTION)
            headers.addAll(Arrays.asList(new String[]{"大行政區", "小行政區"}));
        else if  (siteType==SITETYPE_RESTAURANT)
            headers.addAll(Arrays.asList(new String[]{"大行政區", "小行政區", "時段", "種類", "口味"}));


        ifInitShow = getArguments().getBoolean("ifInitShow");
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

        mQueue = Volley.newRequestQueue(this.getActivity());
        initErrorBar = PinkCon.getInitErrorSnackBar(rootView, PinkCon.INIT_FAIL, this.getActivity());

        mDropDownMenu= (DropDownMenu) getView().findViewById( R.id.dropDownMenu);
        initDropDownMenu();

        Toast.makeText(this.getActivity(),searchType+"+"+siteFrom , Toast.LENGTH_SHORT).show();

        if (searchType==SEARCHTYPE_BROWSE)
        {
            addNewSiteFAB = (FloatingActionButton) getView().findViewById(R.id.addNewSiteFAB);
            addNewSiteFAB.setVisibility(View.VISIBLE);
            addNewSiteFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();

                    if (searchType == SearchSites.SEARCHTYPE_BROWSE)
                        intent.setClass(SearchSites.this.getActivity(), EditSite.class);
                    else
                        intent.setClass(SearchSites.this.getActivity(), SiteSearchActivity.class);

                    intent.putExtra("siteType", siteType);
                    startActivity(intent);
                }
            });
        }
        /*
        switch(searchType) {
            case 8341:
                addNewSiteFAB.setVisibility(View.VISIBLE);
                break;
        }*/
    }

    private void initDropDownMenu()
    {
        contentView = getActivity().getLayoutInflater().inflate(R.layout.dropdownmenu_contentview, null);

        sites = new JSONArray();
        siteListAdapter = new SiteListAdapter(SearchSites.this.getActivity(), sites);
        siteListView = (ListView) contentView.findViewById(R.id.siteListView);
        siteListView.setAdapter(siteListAdapter);
        siteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(SearchSites.this.getActivity(), SiteInfo.class);
                intent.putExtra("sId", siteListAdapter.getSiteId(position));
                intent.putExtra("from","search");
                startActivity(intent);
            }
        });
        setInitSiteList();



        initTabOption();

        //该监听回调只监听默认类型，如果是自定义view请自行设置，参照demo
        mDropDownMenu.addMenuSelectListener(new DropDownMenu.OnDefultMenuSelectListener() {
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

        mDropDownMenu.setClassSearcher(new DropDownMenu.ClassSearcher() {
            @Override
            public void search() {
                searchByClasses();
            }
        });

//        mDropDownMenu.setListRefresher(
//                new DropDownMenu.ListRefresher() {
//                    @Override
//                    public void refresh(JSONArray jArr) throws JSONException {
//                        siteListAdapter.changeData(jArr);
//                    }
//                }
//        );
//        mDropDownMenu.setDynamicSearcher(getDynamicSearcher());
    }


    public void initTabOption()
    {
        String url = PinkCon.URL +"searchSites_initTabOptions.php?opt="+siteType;

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

                        } catch (Exception e)
                        {    initErrorBar.show();   }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {    initErrorBar.show();   }
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
        String url = PinkCon.URL +"searchSites_getAreaTabOptions.php?city="+cityName;

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
                        PinkCon.retryConnect(rootView, PinkCon.CONNECT_FAIL, initErrorBar,
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {   refreshTabOption(cityName);  }
                            });
                    }
                });

        mQueue.add(stringRequest);
    }

    public void setInitSiteList()
    {
        if (initSiteList!=null)
        {
            try
            {
                siteListAdapter.changeData(initSiteList);
            }
            catch (JSONException e)
            {   e.printStackTrace();    }
        }
        else
        {
            String url;
            if (searchType == SEARCHTYPE_BROWSE)
                url = PinkCon.URL + "searchSites_initSiteList_recommended.php?siteType=" + siteType + "&mId=" + pref.getString("mId", null);
            else
                url = PinkCon.URL + "searchSites_initSiteList_allMyLike.php?siteType=" + siteType + "&mId=" + pref.getString("mId", null);


            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                    Log.d("HFresponse", response);
                            try {
                                initSiteList = new JSONArray(response);
                                siteListAdapter.changeData(initSiteList);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                    PinkCon.retryConnect(rootView, PinkCon.SEARCH_FAIL, initErrorBar,
//                            new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    searchByClasses();
//                                }
//                            });
                        }
                    });

            mQueue.add(stringRequest);
        }
    }



    public Boolean onBackPressedAct() {
        //退出activity前关闭菜单
        if (mDropDownMenu!=null && mDropDownMenu.isShowing())
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
//        mDropDownMenu.closeMenu();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL +"searchSites_searchByClasses.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("HFsearchByClass", response);
                        try
                        {
                            sites = new JSONArray(response);
                            siteListAdapter.changeData(sites);
                        }
                        catch (JSONException e)
                        {
                            PinkCon.retryConnect(rootView, PinkCon.SEARCH_FAIL, initErrorBar,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        searchByClasses();
                                    }
                                });
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        PinkCon.retryConnect(rootView, PinkCon.SEARCH_FAIL, initErrorBar,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    searchByClasses();
                                }
                            });
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
                }

                return map;
            }
        };

        mQueue.add(stringRequest);
    }

//    private DropDownMenu.DynamicSearcher getDynamicSearcher()
//    {
//        return new DropDownMenu.DynamicSearcher()
//        {
//            @Override
//            public TextWatcher getSearcher(final EditText searchBar, final DropDownMenu.ListRefresher listRefresher)
//            {
//                return new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count)
//                    {
//                        String query = searchBar.getText().toString();
//
//                        searchByText(query);
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {}
//                };
//            };
//
//        };
//    }


    public void searchByText(String query)
    {
        query = query.replace(" ", "%");

        if (query.matches("^[%]*$"))
            return ;

        String url = null;
        try {
            url = PinkCon.URL +"searchSites_searchByText.php?searchType="+searchType+"&siteType="+siteType+"&mId="+pref.getString("mId", "anyone")+"&query="+ URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return ;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                                        Log.d("HF~r", response);

                        try {
                            siteListAdapter.changeData(new JSONArray(response));
                        } catch (JSONException e) {
                            //  do nothing
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        PinkCon.retryConnect(rootView, PinkCon.SEARCH_FAIL, initErrorBar,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        searchByClasses();
                                    }
                                });
                    }
                });
        mQueue.add(stringRequest);


    }


    public void onScrolledIn()
    {
        if (addNewSiteFAB!=null)
            addNewSiteFAB.setVisibility(View.VISIBLE);
    }


    public void onScrollOut()
    {
        if (addNewSiteFAB!=null)
            addNewSiteFAB.setVisibility(View.GONE);

    }

}

