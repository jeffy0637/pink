package m.mcoupledate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import m.mcoupledate.classes.ClusterSite;
import m.mcoupledate.classes.ClusterSiteRenderer;
import m.mcoupledate.classes.WorkaroundMapFragment;
import m.mcoupledate.funcs.AuthChecker6;

public class AddNewSiteActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        ClusterManager.OnClusterClickListener<ClusterSite>,
        ClusterManager.OnClusterInfoWindowClickListener<ClusterSite>,
        ClusterManager.OnClusterItemClickListener<ClusterSite>,
        ClusterManager.OnClusterItemInfoWindowClickListener<ClusterSite>,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private ScrollView scrollView;

    private GoogleMap mMap;
    private ClusterManager<ClusterSite> mClusterManager;

    private final int REQ_INIT_MYLOCATION = 235;
    private final int REQ_GET_MYPOSITION = 236;

    RequestQueue mQueue;
    String pinkCon = "http://140.117.71.216/pinkCon/";

    Intent intent;

    private AuthChecker6 mapChecker;    //  用來檢查android 6權限和定位功能是否開啟

    private TextView title;
    private ImageButton searchStart, searchClean, submit;
    private ListView searchSuggestion;  //  搜尋建議

    private Map<String, EditText> input;    //  輸入欄位的map


    private final int USER_TYPING = 0, FROM_SUGGESTIONLIST = 1;
    private int searchSuggestionStatus = USER_TYPING;

    ArrayAdapter<String> adapter;
    ArrayList<String> suggestions;
    private Marker suggestMarker = null;
    private String suggestAddress = "";     //  利用input查詢到的地址存於此，submit時將此送出為地址
    private LatLng newSiteLatLng = null;    //  查到的地點的LatLng ，submit時將此送出為Py, Px

    private final int TOTAG__PLACE = 1, DEFAULT_GESTURE = 0;
    private int cameraMoveType = DEFAULT_GESTURE;   //  移動地圖camera時，記錄是程式移動或使用者移動，判斷該否先清空cluster

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_site);

        //  從SESSION取得使用者mId
        pref = this.getSharedPreferences("pinkpink", 0);
        prefEditor = pref.edit();

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        mapFragment.getMapAsync(this);

        mQueue = Volley.newRequestQueue(this);

        intent = this.getIntent();


        title = (TextView) findViewById(R.id.title);
        title.setText(intent.getStringExtra("TYPE"));


        input = new HashMap<String, EditText>();

        input.put("sName", (EditText)findViewById(R.id.sName));

        searchStart = (ImageButton)findViewById(R.id.searchStart);
        searchStart.setOnClickListener(this);
        searchClean = (ImageButton)findViewById(R.id.searchClean);
        searchClean.setOnClickListener(this);
        submit = (ImageButton)findViewById(R.id.submit);
        submit.setOnClickListener(this);

        input.put("search", (EditText)findViewById(R.id.search));
        input.get("search").addTextChangedListener(new TextWatcher()
        {
            //  設定當地址欄內文字改變時，跟google place api要求建議字句
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (searchSuggestionStatus==USER_TYPING) // 但若是由程式改變的話則不要求
                {
                    String query = input.get("search").getText().toString().replace(" ", "+");
                    Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + query + "&language=zh-TW&components=country:tw&key=AIzaSyBn1wKXTrwBl2qZRVY9feOZC3aeklAnZXg");

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        JSONObject o = new JSONObject(response);

                                        if (o.optString("status").compareTo("OK") == 0)
                                        {
                                            JSONArray jArr = o.getJSONArray("predictions");
                                            suggestions.clear();

                                            for (int a = 0; a < jArr.length(); ++a)
                                                suggestions.add(String.valueOf(jArr.getJSONObject(a).optString("description")));

                                            adapter.notifyDataSetChanged();
                                        }
                                        else if (input.get("search").getText().toString().compareTo("")==0)
                                        {
                                            suggestions.clear();
                                            adapter.notifyDataSetChanged();
                                        }
                                        else
                                        {
                                            suggestions.clear();
                                            suggestions.add("查無結果");
                                            adapter.notifyDataSetChanged();
                                        }

                                    } catch (JSONException e) { e.printStackTrace(); }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(AddNewSiteActivity.this, error.getMessage() + "-----" + error.toString() + "--vvv", Toast.LENGTH_LONG).show();
                                }
                            });
                    mQueue.add(stringRequest);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable)
            {   searchSuggestionStatus = USER_TYPING;   }
        });

        suggestions = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this , android.R.layout.simple_list_item_1 ,suggestions);

        searchSuggestion = (ListView)findViewById(R.id.searchSuggestion);
        searchSuggestion.setAdapter(adapter);
        searchSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3)
            {
                ListView suggestionList = (ListView) arg0;

                if (suggestionList.getItemAtPosition(arg2).toString().compareTo("查無結果")==0)
                    return ;

                //  當使用者點擊listItem時，將建議填入搜尋框，設status為0，避免填入時被onTextChanged重新要求
                searchSuggestionStatus = FROM_SUGGESTIONLIST;
                input.get("search").setText(suggestionList.getItemAtPosition(arg2).toString());
                searchPlaceLocate(suggestionList.getItemAtPosition(arg2).toString());
            }
        });

    }

    //  當map載入完成後，相關功能設定並檢查是否可定位
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        setUpClusterer();

        //  當camera移動時就呼叫 markClusterSites 印出週遭景點
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (mMap.getCameraPosition().zoom>=12)
                    markClusterSites(mMap.getCameraPosition().target);
            }
        });

        LatLng y = new LatLng(23.9036873,121.0793705);  // 預設台灣
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(y, 6));


        //  檢查是否有存取目前定位的權限，確認後載入附近景點
        mapChecker = new AuthChecker6(AddNewSiteActivity.this){
            @Override
            public void onResult(Object result)
            {
//                Location location = (Location) result;
//                markClusterSites(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        };
        mapChecker.checkMapMyLocation(mMap);
    }


    @Override
    public void onMapLongClick(LatLng latLng)
    {
        if (suggestMarker!=null)
            suggestMarker.remove();

        cameraMoveType = TOTAG__PLACE;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (mMap.getMaxZoomLevel()-8)));
        suggestMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("✓ 點擊確認新增"));
        suggestMarker.showInfoWindow();

        suggestAddress = "";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://maps.googleapis.com/maps/api/geocode/json?latlng="+String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude)+"&result_type=street_address&language=zh-TW&key=AIzaSyBn1wKXTrwBl2qZRVY9feOZC3aeklAnZXg",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONArray jArr = new JSONObject(response).getJSONArray("results");

                            suggestAddress = jArr.getJSONObject(0).optString("formatted_address");

                        } catch (JSONException e) {
                            Toast.makeText(AddNewSiteActivity.this, e.getMessage()+" - "+e.toString(), Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddNewSiteActivity.this, error.getMessage()+"-----"+error.toString()+"--vvv", Toast.LENGTH_LONG).show();
                    }
                });

        mQueue.add(stringRequest);

    }

    private void setUpClusterer()
    {
        mClusterManager = new ClusterManager<ClusterSite>(this, mMap);

        mClusterManager.setRenderer(new ClusterSiteRenderer(AddNewSiteActivity.this, mMap, mClusterManager));

        mMap.setOnMarkerClickListener(mClusterManager);
        //mMap.setOnInfoWindowClickListener(mClusterManager); //  當點擊資訊視窗時引發事件

        // 當點擊群集時引發事件
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

    }


    public void markClusterSites(LatLng latlng)
    {
        //  以 cluster 印出 latlng 附近的景點

        if (cameraMoveType== TOTAG__PLACE)  // 若為程式呼叫非使用者移動則不清空cluster
        {
            mClusterManager.clearItems();
            mClusterManager.cluster();
        }

        cameraMoveType = DEFAULT_GESTURE;  //   判斷完後先改回預設


        StringRequest stringRequest = new StringRequest(Request.Method.GET, pinkCon+"getAroundSites.php?lat="+latlng.latitude+"&lng="+latlng.longitude,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jArr = new JSONArray(response);
                            JSONObject o;

                            for (int a=0; a<jArr.length(); ++a)
                            {
                                o = jArr.getJSONObject(a);

                                if (a%10==9 || a==(jArr.length()-1))
                                    setOneMarker(o, 1);
                                else
                                    setOneMarker(o, 0);

                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(AddNewSiteActivity.this, e.getMessage()+"-----"+e.toString()+"--vvv", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddNewSiteActivity.this, error.getMessage()+"-----"+error.toString()+"--vvv", Toast.LENGTH_LONG).show();
                    }
                });

        mQueue.add(stringRequest);


    }

    private void setOneMarker(final JSONObject o, final int ifNeedCluster)
    {

        Glide.with(AddNewSiteActivity.this)
                .load(pinkCon + "images/sitePic/" + o.optString("picId") + "a.jpg")
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(50, 50)
                      {
                          @Override
                          public void onResourceReady(Bitmap bitmap, GlideAnimation anim)
                          {
                              mClusterManager.addItem(new ClusterSite(new LatLng(o.optDouble("Py"), o.optDouble("Px")), o.optString("sName"), bitmap));

                              if (ifNeedCluster==1)
                                  mClusterManager.cluster();
                          }
                          @Override
                          public void onLoadFailed(Exception e, Drawable errorDrawable)
                          {
                              //                            mClusterManager.addItem(new ClusterSite(new LatLng(o.optDouble("Py"), o.optDouble("Px")), o.optString("sName"), AddNewSiteActivity.this));
                          }

                      }
                );
    }

    @Override
    public boolean onClusterClick(Cluster<ClusterSite> cluster)
    {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this,  firstName+"和"+(cluster.getSize()-1)+"個景點", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<ClusterSite> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(ClusterSite item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterSite item) {
        try
        {
            Toast.makeText(AddNewSiteActivity.this, item.name, Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            Toast.makeText(AddNewSiteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQ_INIT_MYLOCATION:
            case REQ_GET_MYPOSITION:
                mapChecker.checkMapMyLocation(mMap);
                break;
        }

    }


    @Override
    public void onClick(View view){

        switch (view.getId())
        {
            case R.id.searchStart:
                searchPlaceLocate(suggestions.get(0));
                break;
            case R.id.searchClean:
                input.get("search").setText("");
                break;
            case R.id.submit:
                submit();
                break;
        }

    }



    //  使用者點擊搜尋按鈕時，將其位置定位至mMap
    //  或選擇建議時，同時導至該建議的點
    private void searchPlaceLocate(String query)
    {
        Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/place/textsearch/json?input=" + query + "&language=zh-TW&components=country:tw&key=AIzaSyBn1wKXTrwBl2qZRVY9feOZC3aeklAnZXg");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject o = new JSONObject(response);

                            if (o.optString("status").compareTo("OK") == 0)
                            {
                                JSONObject place = o.getJSONArray("results").getJSONObject(0);

                                suggestAddress = place.optString("formatted_address");
                                JSONObject placeLocation = place.getJSONObject("geometry").getJSONObject("location");

                                LatLng placeLatLng = new LatLng(placeLocation.optDouble("lat"), placeLocation.optDouble("lng"));

                                if (suggestMarker!=null)
                                    suggestMarker.remove();

                                suggestMarker = mMap.addMarker(new MarkerOptions().position(placeLatLng).title("✓ 點擊確認新增").snippet(place.optString("name")));

                                suggestMarker.showInfoWindow();
                                cameraMoveType = TOTAG__PLACE;
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, (mMap.getMaxZoomLevel()-8)));

                            }
                            else
                            {
                                Toast.makeText(AddNewSiteActivity.this, "查無結果", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddNewSiteActivity.this, error.getMessage() + "-----" + error.toString() + "--vvv", Toast.LENGTH_LONG).show();
                    }
                });

        mQueue.add(stringRequest);

    }


    private void submit()
    {

        input.put("description", (EditText) findViewById(R.id.description));
        input.put("phone", (EditText)findViewById(R.id.phone));
        input.put("transportation", (EditText) findViewById(R.id.transportation));
        input.put("email", (EditText) findViewById(R.id.email));
        input.put("website", (EditText) findViewById(R.id.website));
        input.put("activity", (EditText) findViewById(R.id.activity));
        input.put("note", (EditText) findViewById(R.id.note));


        if (checkForm(input)==false)
            return ;

        final String Py = String.valueOf(newSiteLatLng.latitude);
        final String Px = String.valueOf(newSiteLatLng.longitude);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, pinkCon+"addNewSite.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(AddNewSiteActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(AddNewSiteActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("sName", input.get("sName").getText().toString());
                map.put("description", input.get("description").getText().toString());
                map.put("address", suggestAddress);
                map.put("phone", input.get("phone").getText().toString());
                map.put("transportation", input.get("transportation").getText().toString());
                map.put("email", input.get("email").getText().toString());
                map.put("website", input.get("website").getText().toString());
                map.put("Py", Py);
                map.put("Px", Px);
                map.put("activity", input.get("activity").getText().toString());
                map.put("creator", pref.getString("mId", null));
                map.put("note", input.get("note").getText().toString());
                map.put("siteType", intent.getStringExtra("TYPE"));

                return map;
            }
        };

        mQueue.add(stringRequest);

    }


    @Override
    public void onInfoWindowClick(Marker marker)
    {
        //  若點擊到marker是suggestMarker則點擊是為確認地點
        if (marker.getId().compareTo(suggestMarker.getId())==0)
        {
            newSiteLatLng = suggestMarker.getPosition();
            marker.hideInfoWindow();
            marker.setTitle(null);

            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_beenhere_black_48dp));

            input.get("sName").setText(marker.getSnippet());
        }
    }


    private Boolean checkForm(Map<String, EditText> input)
    {
        Boolean status = true;
        String[] notNullFields = {"sName", "description"};
        String msg = "請填寫以下欄位";

        for (String field : notNullFields)
        {
            if (input.get(field).getText().toString().length()==0)
            {
                status = false;
                msg += "\n"+input.get(field).getHint();
                input.get(field).setBackgroundColor(Color.parseColor("#F78181"));
            }
        }

        if (newSiteLatLng == null)
        {
            status = false;
            msg += "\n並在地圖上選擇地點";
        }

        if (status==false)
            Toast.makeText(AddNewSiteActivity.this, msg, Toast.LENGTH_SHORT).show();

        return status;

    }


}
