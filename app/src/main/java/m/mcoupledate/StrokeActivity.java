package m.mcoupledate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import m.mcoupledate.classes.InputDialogManager;
import m.mcoupledate.classes.NavigationActivity;
import m.mcoupledate.classes.adapters.DynamicSearchAdapter;
import m.mcoupledate.classes.customView.ResponsiveListView;
import m.mcoupledate.classes.funcs.PinkCon;

public class StrokeActivity extends NavigationActivity {

    private Context context;
    private RequestQueue mQueue;

    private static String tripType;
    Intent intent;
    String tripId;
    //String tripType;
    EditText share_dialog;

    final String url = "https://couple-project.firebaseio.com/travel";


    private InputDialogManager searchMemberManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stroke);

        context = this;
        mQueue = Volley.newRequestQueue(context);

        intent = this.getIntent();
        // String tripId = intent.getStringExtra("tripId");

        Bundle bundle = this.getIntent().getExtras();
        tripId = bundle.getString("tripId");
        tripType = bundle.getString("tripType");

        if (savedInstanceState == null) {
            showFragment(BoardFragment.newInstance());
        }

        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color)));

        searchMemberManager = getNewSearchMemberManager();
    }

    private void showFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("TI",tripId);
        bundle.putString("TT",tripType);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "fragment").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        //getMenuInflater().inflate(R.menu.menu_board_move, menu); 原版
        switch (tripType){
            case "my":
                getMenuInflater().inflate(R.menu.menu_board, menu);
                break;
            case "collection":
                getMenuInflater().inflate(R.menu.mytrip_like, menu);
                break;
            case "search":
                getMenuInflater().inflate(R.menu.travel_search, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean listFragment = getSupportFragmentManager().findFragmentByTag("fragment") instanceof ListFragment;
        //menu.findItem(R.id.action_lists).setVisible(!listFragment);
        //menu.findItem(R.id.action_board).setVisible(listFragment);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_lists:
//                showFragment(ListFragment.newInstance());
                return true;
            case R.id.action_board:
                showFragment(BoardFragment.newInstance());
                return true;
        }
        //各按鈕作用 首項為測試
        switch (tripType){
            case "my":
                switch (item.getItemId()) {
                    case  R.id.together :
                        searchMemberManager.dialog.show();
                }
                break;
            case "collection":
                break;
            case "search":
                switch (item.getItemId()) {
                    case  R.id.add_like :
                        addMyTravelCollection();
                        break;
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String getTripType()
    {
        String t = tripType;
        return t;
    }



    private InputDialogManager getNewSearchMemberManager()
    {
        return new InputDialogManager(context, R.layout.dialog_searchmember, "邀請夥伴", "加入", "取消")
        {
            @Override
            protected void initContent()
            {
                final SearchView searchView = (SearchView) dialogFindViewById(R.id.searchView);

                ResponsiveListView memberSuggestListView = (ResponsiveListView) dialogFindViewById(R.id.memberSuggestListView);
                final DynamicSearchAdapter memberSuggestListAdapter = new DynamicSearchAdapter(context);
                memberSuggestListView.setAdapter(memberSuggestListAdapter);

                memberSuggestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        DynamicSearchAdapter.AResult aResult = memberSuggestListAdapter.getItem(position);

                        searchView.setQuery(aResult.text, false);

                        vars.put("resultValue", aResult.value);
                    }
                });

                vars.put("searchView", searchView);


                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query)
                    {   return onQueryTextChange(query);    }
                    @Override
                    public boolean onQueryTextChange(String newText)
                    {
                        if (newText.compareTo("")==0)
                        {
                            memberSuggestListAdapter.clear();
                            return false;
                        }

                        passSearchQuery(newText);
                        return false;
                    }

                    private void passSearchQuery(final String query)
                    {
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, PinkCon.URL + "dynamicSearchMember.php?query=" + query + "&self=" + StrokeActivity.this.getSharedPreferences("pinkpink", 0).getString("mId", ""),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response)
                                    {
                                        try
                                        {
                                            memberSuggestListAdapter.refresh(new JSONArray(response));
                                        }
                                        catch (JSONException e)
                                        {   e.printStackTrace();    }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
//                        initErrorBar.show("HFsetClassesrror", error.getMessage());
                                    }
                                });

                        mQueue.add(stringRequest);
                    }
                });
            }

            @Override
            protected Boolean onConfirm()
            {
                final String mId = (String) vars.get("resultValue");
                Log.d("HFmId", mId);

                ((SearchView)vars.get("searchView")).setQuery("", false);
//                final String mId = "1150514441660964";

                Firebase.setAndroidContext(StrokeActivity.this);//this用mBoardView.getContext()取代
                new Firebase(url).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if((""+dataSnapshot.child("tId").getValue()).equals(tripId)){
                            Firebase addEditorRef = (dataSnapshot.child("editor")).getRef();
                            Map<String, Object> nameMap = new HashMap<String, Object>();
                            nameMap.put(""+dataSnapshot.child("editor").getChildrenCount(), mId);
                            addEditorRef.updateChildren(nameMap);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });



                return true;
            }

            @Override
            protected Boolean onCancel()
            {   return true;    }
        };
    }



    private void addMyTravelCollection()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL + "addMyTravelCollection.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
//                        PinkCon.retryConnect(getRootView(), PinkCon.SEARCH_FAIL, initErrorBar,
//                                new View.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(View view)
//                                    {   searchMapLocatePlace(query);  }
//                                });
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("mId", context.getSharedPreferences("pinkpink", 0).getString("mId", ""));
                map.put("tId", tripId);

                Log.d("HFaddCollection", map.toString());

                return map;
            }
        };

        mQueue.add(stringRequest);
    }
}
