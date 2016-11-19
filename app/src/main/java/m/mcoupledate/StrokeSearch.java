package m.mcoupledate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import m.mcoupledate.classes.NavigationActivity;
import m.mcoupledate.classes.funcs.PinkCon;

public class StrokeSearch extends NavigationActivity {

    private Activity activity;

    private RequestQueue mQueue;

    private BottomBar mBottomBar;

    private ListView strokeListView;
    private StrokeSearchAdapter strokeSearchAdapter;

    final String tripType = "search";
    private SearchView searchView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stroke_search);

        this.activity = this;

        mQueue = Volley.newRequestQueue(activity);


        strokeSearchAdapter = new StrokeSearchAdapter(activity);
        strokeListView = (ListView) findViewById(R.id.strokeSearchListView);
        strokeListView.setAdapter(strokeSearchAdapter);
        strokeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(StrokeSearch.this, StrokeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tripId", strokeSearchAdapter.getItem(position).tId);
                bundle.putString("tripType", tripType);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        initStrokeList();


        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItems(R.menu.bottom_menu);
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                //单击事件 menuItemId 是 R.menu.bottombar_menu 中 item 的 id
                switch (menuItemId) {
                    case R.id.bb_menu_memorialday:

                        break;
                    case R.id.bb_menu_site:

                        break;
                    case R.id.bb_menu_trip:

                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                //重选事件，当前已经选择了这个，又点了这个tab。
                switch (menuItemId) {
                    case R.id.bb_menu_memorialday:
                        Intent go = new Intent(StrokeSearch.this, HomePageActivity.class);
                        startActivity(go);
                        break;
                    case R.id.bb_menu_site:
                        Intent go2 = new Intent(StrokeSearch.this, SiteSearchActivity.class);
                        startActivity(go2);
                        break;
                    case R.id.bb_menu_trip:

                        break;
                }

            }
        });

        // 当点击不同按钮的时候，设置不同的颜色
        // 可以用以下三种方式来设置颜色.
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(1, 0xFF5D4037);
        mBottomBar.mapColorForTab(2, "#7B1FA2");

    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }

    /*
    初始化数据
     */
    private void initStrokeList()
    {
        //  連接Firebase 印出行程

        final String firebaseUrl = "https://couple-project.firebaseio.com/travel";
        Firebase.setAndroidContext(this);

        new Firebase(firebaseUrl).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                JSONArray strokeJSONArray = new JSONArray();

                for(int i = 0 ; i < dataSnapshot.getChildrenCount() ; i++)
                {
                    JSONObject aTrip =new JSONObject();

                    DataSnapshot snapShotChild = dataSnapshot.child(String.valueOf(i));

                    String tName = String.valueOf(snapShotChild.child("tripName").getValue());
                    String tId = String.valueOf(snapShotChild.child("tId").getValue());
                    String startDate = String.valueOf(snapShotChild.child("start_date").getValue());
                    String endDate = String.valueOf(snapShotChild.child("end_date").getValue());

                    try
                    {
                        aTrip.put("tId", tId);
                        aTrip.put("tName", tName);
                        aTrip.put("startDate", startDate);
                        aTrip.put("endDate", endDate);
                    }
                    catch (JSONException e)
                    {   e.printStackTrace();    }

                    JSONArray tripDays = new JSONArray();
                    for(int j = 1 ; j <= snapShotChild.child("site").getChildrenCount() ; j++)
                    {
                        JSONArray tripDaySites = new JSONArray(); //  一天中的景點
                        for(int k = 0 ; k < snapShotChild.child("site").child("day" + j).getChildrenCount() ; k++)
                        {
                            String sId = String.valueOf(snapShotChild.child("site").child("day" + j).child("" + k).child("sId").getValue());
                            tripDaySites.put(sId);
                        }
                        tripDays.put(tripDaySites);
                    }

                    try
                    {
                        aTrip.put("sites", tripDays);
                    }
                    catch (JSONException e)
                    {   e.printStackTrace();    }

                    strokeJSONArray.put(aTrip);
                }

                sortStrokes(strokeJSONArray);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    private void sortStrokes(final JSONArray strokeJSONArray)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL+"strokeSearch_sortByRecommend.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            strokeSearchAdapter.addAll(new JSONArray(response));
                        }
                        catch (JSONException e)
                        {   e.printStackTrace();    }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {   }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("strokeJSONArray", strokeJSONArray.toString());
                map.put("mId", getPref().getString("mId", ""));

                return map;
            }
        };

        mQueue.add(stringRequest);
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.topbar_search, menu);

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.topbarSearch));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                onQueryTextChange(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
                passSearchQuery(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose()
            {
                return false;
            }
        });

        return true;
    }

    private void passSearchQuery(String query)
    {
        //
    }
}