package m.mcoupledate;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.mcoupledate.classes.ClusterSite;
import m.mcoupledate.classes.ClusterSiteRenderer;
import m.mcoupledate.classes.DropDownMenu.ConstellationAdapter;
import m.mcoupledate.classes.GridAlbumAdapter;
import m.mcoupledate.classes.LockableScrollView;
import m.mcoupledate.classes.ResponsiveGridView;
import m.mcoupledate.classes.WorkaroundMapFragment;
import m.mcoupledate.funcs.AuthChecker6;

public class AddNewSiteActivity extends FragmentActivity implements
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

    private LockableScrollView scrollView;

    private GoogleMap mMap;
    private ClusterManager<ClusterSite> mClusterManager;

    private final int REQ_INIT_MYLOCATION = 235;
    private final int REQ_GET_MYPOSITION = 236;

    RequestQueue mQueue;
    String pinkCon = "http://140.117.71.216/pinkCon/";

    Intent intent;

    private AuthChecker6 mapChecker;    //  用來檢查android 6權限和定位功能是否開啟

    private TextView title;
    private ImageButton submit;


    private Map<String, EditText> input;    //  輸入欄位的map


    //  搜尋地圖
    private ListView searchMapSuggestion;
    private Button searchMap, searchMapMaskBack, searchMapQueryClean;
    private LinearLayout searchMapMask;
    private EditText searchMapQuery;

    ArrayAdapter<String> searchMapAdapter;
    ArrayList<String> suggestions;

    private Marker suggestMarker = null;
    private String suggestAddress = "";     //  利用input查詢到的地址存於此，submit時將此送出為地址
    private LatLng newSiteLatLng = null;    //  查到的地點的LatLng ，submit時將此送出為Py, Px

    private final int TOTAG__PLACE = 1, DEFAULT_GESTURE = 0;
    private int cameraMoveType = DEFAULT_GESTURE;   //  移動地圖camera時，記錄是程式移動或使用者移動，判斷該否先清空cluster


    private final int SITECLASS_CITY = 1, SITECLASS_AREA = 2, SITECLASS_TIME = 3, SITECLASS_FOODKIND = 4, SITECLASS_COUNTRY = 5;
    private SparseArray<HashMap<String, View>> siteClassViews = new SparseArray<HashMap<String, View>>();
    private HashMap<Integer, ConstellationAdapter> siteClassesAdapters = new HashMap<Integer, ConstellationAdapter>();


    private InputMethodManager keyboard;
    private IBinder windowToken;


    private GridView uploadPicsAlbum;
    private GridAlbumAdapter gaAdapter;
    private ImageButton uploadPic_album, uploadPic_camera;

    //-------------------------飛---------------------------

    //常數
    private final static int UPLOADPIC_CAMERA = 1, UPLOADPIC_ALBUM = 2;

    //照片路徑
    private String strImage;

    //php位置
    private String upLoadServerUri = pinkCon + "uploadPicture.php";
    private ProgressDialog dialog = null;
    private int serverResponseCode = 0;


    //存圖片uri 之後拿來將圖片設定給相簿使用
    private Uri uriMyImage;

    private String siteType = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_site);


        pref = this.getSharedPreferences("pinkpink", 0);
        prefEditor = pref.edit();

        scrollView = (LockableScrollView) findViewById(R.id.scrollView);

        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null)
        {
            mapFragment = new WorkaroundMapFragment(); // (WorkaroundMapFragment) WorkaroundMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }

        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });


        mapFragment.getMapAsync(this);

        mQueue = Volley.newRequestQueue(this);

        intent = this.getIntent();
        siteType = intent.getStringExtra("siteType");


        title = (TextView) findViewById(R.id.title);
        title.setText(siteType);


        input = new HashMap<String, EditText>();
        input.put("sName", (EditText)findViewById(R.id.sName));
        input.put("address", (EditText)findViewById(R.id.address));

        submit = (ImageButton)findViewById(R.id.submit);
        submit.setOnClickListener(this);



        /*       以下 -> 動態搜尋地圖       */

        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        windowToken = (new View(AddNewSiteActivity.this)).getWindowToken();

        final Animation searchMapMaskIn = AnimationUtils.loadAnimation(AddNewSiteActivity.this, R.anim.fade_in);
        final Animation searchMapMaskOut = AnimationUtils.loadAnimation(AddNewSiteActivity.this, R.anim.fade_out);
        searchMapMaskOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation)
            {
                searchMapMask.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        suggestions = new ArrayList<String>();
        searchMapAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_list_item_1 ,suggestions);


        searchMapSuggestion = (ListView)findViewById(R.id.searchMapSuggestion);
        searchMapSuggestion.setAdapter(searchMapAdapter);
        searchMapSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3)
            {
                ListView suggestionList = (ListView) arg0;

                if (suggestionList.getItemAtPosition(arg2).toString().compareTo("查無結果")==0)
                    return ;

                //  當使用者點擊listItem時，將建議填入搜尋框，設status為0，避免填入時被onTextChanged重新要求
//                searchSuggestionStatus = FROM_SUGGESTIONLIST;
//                searchMapQuery.setText(suggestionList.getItemAtPosition(arg2).toString());
                searchMapLocatePlace(suggestionList.getItemAtPosition(arg2).toString());

                searchMapMask.startAnimation(searchMapMaskOut);
                scrollView.unlock();
            }
        });

        searchMapQuery = (EditText)findViewById(R.id.searchMapQuery);
        searchMapQuery.addTextChangedListener(new TextWatcher()
        {
            //  設定當地址欄內文字改變時，跟google place api要求建議字句
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

//                if (searchSuggestionStatus==USER_TYPING) // 但若是由程式改變的話則不要求
//                {
                    String query = searchMapQuery.getText().toString().replace(" ", "+");
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

                                            searchMapAdapter.notifyDataSetChanged();
                                        }
                                        else if (searchMapQuery.getText().toString().compareTo("")==0)
                                        {
                                            suggestions.clear();
                                            searchMapAdapter.notifyDataSetChanged();
                                        }
                                        else
                                        {
                                            suggestions.clear();
                                            suggestions.add("查無結果");
                                            searchMapAdapter.notifyDataSetChanged();
                                        }

                                    } catch (JSONException e) {
                                        Log.d("HFDYNAMICERROR", e.getMessage());
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(AddNewSiteActivity.this, error.getMessage() + "-----" + error.toString() + "--vvv", Toast.LENGTH_LONG).show();
                                }
                            });
                    mQueue.add(stringRequest);
//                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable)
            {
                //searchSuggestionStatus = USER_TYPING;
            }
        });



        searchMapMask = (LinearLayout) findViewById(R.id.searchMapMask);
        searchMap = (Button) findViewById(R.id.searchMap);
        searchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMapMask.setVisibility(View.VISIBLE);
                searchMapMask.startAnimation(searchMapMaskIn);
                scrollView.lock();
                searchMapQuery.requestFocus();
            }
        });

        searchMapMaskBack = (Button) findViewById(R.id.searchMapMaskBack);
        searchMapMaskBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboard.hideSoftInputFromWindow(windowToken, 0);
                searchMapMask.startAnimation(searchMapMaskOut);
                scrollView.unlock();
            }
        });

        searchMapQueryClean = (Button)findViewById(R.id.searchMapQueryClean);
        searchMapQueryClean.setOnClickListener(this);
        /*       以上 -> 動態搜尋地圖       */




        HashMap<String, View> siteClassCity = new HashMap<String, View>();
        siteClassCity.put("text", (TextView) findViewById(R.id.selectCity));
        siteClassCity.put("options", (ResponsiveGridView) findViewById(R.id.selectCityOptions));
        siteClassViews.put(SITECLASS_CITY, siteClassCity);
        setClassOptions(SITECLASS_CITY, ConstellationAdapter.RADIO, null);

        HashMap<String, View> siteClassArea = new HashMap<String, View>();
        siteClassArea.put("text", (TextView) findViewById(R.id.selectArea));
        siteClassArea.put("options", (ResponsiveGridView) findViewById(R.id.selectAreaOptions));
        siteClassViews.put(SITECLASS_AREA, siteClassArea);
        setClassOptions(SITECLASS_AREA, ConstellationAdapter.RADIO, "city=不限");

        if (siteType.compareTo("r")==0)
        {
            ((LinearLayout) findViewById(R.id.selectSiteClass_restaurantText)).setVisibility(View.VISIBLE);

            HashMap<String, View> siteClassTime = new HashMap<String, View>();
            siteClassTime.put("text", (TextView) findViewById(R.id.selectTime));
            siteClassTime.put("options", (ResponsiveGridView) findViewById(R.id.selectTimeOptions));
            siteClassViews.put(SITECLASS_TIME, siteClassTime);
            setClassOptions(SITECLASS_TIME, ConstellationAdapter.RADIO, null);

            HashMap<String, View> siteClassFoodKind = new HashMap<String, View>();
            siteClassFoodKind.put("text", (TextView) findViewById(R.id.selectFoodKind));
            siteClassFoodKind.put("options", (ResponsiveGridView) findViewById(R.id.selectFoodKindOptions));
            siteClassViews.put(SITECLASS_FOODKIND, siteClassFoodKind);
            setClassOptions(SITECLASS_FOODKIND, ConstellationAdapter.CHECK, null);

            HashMap<String, View> siteClassCountry = new HashMap<String, View>();
            siteClassCountry.put("text", (TextView) findViewById(R.id.selectCountry));
            siteClassCountry.put("options", (ResponsiveGridView) findViewById(R.id.selectCountryOptions));
            siteClassViews.put(SITECLASS_COUNTRY, siteClassCountry);
            setClassOptions(SITECLASS_COUNTRY, ConstellationAdapter.CHECK, null);
        }



        uploadPicsAlbum = (GridView) findViewById(R.id.uploadPics);
        gaAdapter = new GridAlbumAdapter(AddNewSiteActivity.this);
        uploadPicsAlbum.setAdapter(gaAdapter);


        //-------------------------------------飛-----------------------------------------
        uploadPic_album = (ImageButton) findViewById(R.id.uploadPic_album);

        //使用者選相簿
        uploadPic_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因為點選相片後返回程式呼叫onActivityResult
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, UPLOADPIC_ALBUM);
            }
        });

        //使用者選照相機
        uploadPic_camera = (ImageButton) findViewById(R.id.uploadPic_camera);
        uploadPic_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先判斷內置SD存不存在
                if(existSDCard()){
                    if(getSDFreeSize() > 25) {//看有沒有容量
                        //取得時間
                        Calendar c = Calendar.getInstance();

                        String path = getExtermalStoragePublicDir("Love%%GO").getPath();
                        //照片路徑
                        strImage = path + "/" + c.get(Calendar.YEAR) + (c.get(Calendar.MONTH) + 1) + c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) + ".jpg";
                        File myImage = new File(strImage);
                        uriMyImage = Uri.fromFile(myImage);

                        Log.v("path", strImage);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriMyImage);

                        startActivityForResult(intent, UPLOADPIC_CAMERA);
                    }
                    else
                        Toast.makeText(view.getContext(), "空間不足", Toast.LENGTH_LONG).show();
                }
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
                Location location = (Location) result;
                markClusterSites(new LatLng(location.getLatitude(), location.getLongitude()));
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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://maps.googleapis.com/maps/api/geocode/json?latlng="+String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude)+"&result_type=street_address&language=zh-TW&key=AIzaSyBn1wKXTrwBl2qZRVY9feOZC3aeklAnZXg",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONArray jArr = new JSONObject(response).getJSONArray("results");

                            input.get("address").setText(jArr.getJSONObject(0).optString("formatted_address"));

                        } catch (JSONException e) {
//                            Toast.makeText(AddNewSiteActivity.this, e.getMessage()+" - "+e.toString(), Toast.LENGTH_LONG).show();
                            input.get("address").setText("");
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
            /*       ↓       飛       ↓       */
            case UPLOADPIC_CAMERA:
                if (resultCode == RESULT_OK) {//避免點旁邊會閃退
                    galleryAddPic(uriMyImage);// 將圖片設定給相簿使用
                    Bitmap bmp = BitmapFactory.decodeFile(strImage);
                    //Log.v("path", strImage);
//                    accessPath = strImage;//儲存路徑

                    gaAdapter.add(bmp, strImage);

                }
                break;

            case UPLOADPIC_ALBUM:
                if (resultCode == RESULT_OK)
                {   //避免點旁邊會閃退
                    //取得照片路徑uri
                    Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    try
                    {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        String[] pojo = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(uri, pojo, null, null, null);
                        if (cursor != null) {
                            int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
//                            accessPath = cursor.getString(colunm_index);     //儲存路徑
                            gaAdapter.add(bitmap, cursor.getString(colunm_index));
                            //showPicturePath.setText(path);
                        }

//                        gaAdapter.add(bitmap);

                    }
                    catch (FileNotFoundException e) {}
                }
                break;
            /*       ↑       飛       ↑       */


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
            case R.id.searchMapQueryClean:
                searchMapQuery.setText("");
                break;
            case R.id.submit:
                submit();
                break;
        }

    }

    @Override
    public void onBackPressed()
    {
        if (searchMapMask.getVisibility()==View.VISIBLE)
            searchMapMask.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }



    //  使用者點擊搜尋按鈕時，將其位置定位至mMap
    //  或選擇建議時，同時導至該建議的點
    private void searchMapLocatePlace(String query)
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
                        Log.d("HFSUBMITRESPONSE", response);
                        Toast.makeText(AddNewSiteActivity.this, "新增成功", Toast.LENGTH_SHORT).show();

                        /*       ↓       飛       ↓       */
                        final String id = response;
                        Log.v("Id", id);                     //將資料送入資料庫
                        dialog = ProgressDialog.show(AddNewSiteActivity.this, "", "Uploading file...", true);
                        new Thread(new Runnable() {
                            public void run() {
                                int seq = 97;
                                for (HashMap<String, Object> pic : gaAdapter.album)
                                {
                                    uploadFile((String) pic.get("path"), (char)(seq++), id);
                                }
                                dialog.dismiss();
                                AddNewSiteActivity.this.finish();
//                                if(accessPath != null)
//                                    uploadFile(accessPath, 1, Id);
                            }
                        }).start();
                        /*       ↑       飛       ↑       */
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("HFSUBMITERROR", error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("sName", input.get("sName").getText().toString());
                map.put("description", input.get("description").getText().toString());
                map.put("address", input.get("address").toString());
                map.put("phone", input.get("phone").getText().toString());
                map.put("transportation", input.get("transportation").getText().toString());
                map.put("email", input.get("email").getText().toString());
                map.put("website", input.get("website").getText().toString());
                map.put("Py", Py);
                map.put("Px", Px);
                map.put("activity", input.get("activity").getText().toString());
                map.put("city", siteClassesAdapters.get(SITECLASS_CITY).getCheckedListJSONArray().optString(0));
                map.put("area", siteClassesAdapters.get(SITECLASS_AREA).getCheckedListJSONArray().optString(0));
                map.put("creator", pref.getString("mId", null));
                map.put("note", input.get("note").getText().toString());
                map.put("siteType", siteType);

                if (siteType.compareTo("r")==0)
                {
                    map.put("time", siteClassesAdapters.get(SITECLASS_TIME).getCheckedListJSONArray().optString(0));
                    map.put("country", siteClassesAdapters.get(SITECLASS_COUNTRY).getCheckedListJSONString());
                    map.put("food_kind", siteClassesAdapters.get(SITECLASS_FOODKIND).getCheckedListJSONString());
                }

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
            input.get("address").setText(suggestAddress);
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


    private void setClassOptions(final int siteClass, final int selectType, String param)
    {
        final String url = pinkCon+"getSiteClasses.php?opt="+siteClass+"&"+param;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try {

                            JSONArray jArr = new JSONArray(response);
                            List options = new ArrayList<String>();


                            for (int a=0; a<jArr.length(); ++a)
                                options.add(jArr.getJSONObject(a).optString("oName"));


                            ConstellationAdapter cAdapter = siteClassesAdapters.get(siteClass);

                            if (cAdapter==null)
                                initClassOptions(siteClass, selectType, options);
                            else
                                cAdapter.changeData(options);

                        } catch (JSONException e) {
                            Log.d("HFJSON", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        Log.d("HF1031", url);
//                        Log.d("HF2", error.getMessage());
                    }
                });

        mQueue.add(stringRequest);
    }


    private void initClassOptions(final int siteClass, int selectType, List options)
    {
        final ConstellationAdapter cAdapter = new ConstellationAdapter(this, options, selectType);
        siteClassesAdapters.put(siteClass, cAdapter);

        TextView selectClass = (TextView) siteClassViews.get(siteClass).get("text");
        final ResponsiveGridView selectClassOptions = (ResponsiveGridView) siteClassViews.get(siteClass).get("options");

        selectClassOptions.setAdapter(cAdapter);
        selectClassOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                cAdapter.setCheckItem(position);
                if (siteClass==SITECLASS_CITY)
                {
                    setClassOptions(SITECLASS_AREA, 0, "city="+cAdapter.getItem(position));

                }
            }
        });


        final LinearLayout optionTabsContainer = (LinearLayout) selectClassOptions.getParent();

        final Animation optionsIn = AnimationUtils.loadAnimation(AddNewSiteActivity.this, R.anim.fade_in);
        final Animation optionsOut = AnimationUtils.loadAnimation(AddNewSiteActivity.this, R.anim.fade_out);

        optionsOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation)
            {
                selectClassOptions.setVisibility(View.GONE);
                optionTabsContainer.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        selectClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("HFCONTAINERID", String.valueOf(optionTabsContainer.getId()));

                if (optionTabsContainer.getVisibility()!=View.VISIBLE)
                {
                    Log.d("HFCONTAINERACTION", "1");
                    optionTabsContainer.setVisibility(View.VISIBLE);
                    selectClassOptions.setVisibility(View.VISIBLE);
                    optionTabsContainer.startAnimation(optionsIn);
                }
                else if (selectClassOptions.getVisibility()!=View.VISIBLE)
                {
                    Log.d("HFCONTAINERACTION", "2");

                    int a = 0;
                    while (true)
                    {
                        if (optionTabsContainer.getChildAt(a)==null)
                            break;

                        optionTabsContainer.getChildAt(a).setVisibility(View.GONE);
                        ++a;
                    }

                    selectClassOptions.setVisibility(View.VISIBLE);
                    selectClassOptions.startAnimation(optionsIn);
                }
                else// if (selectClassOptions.getVisibility()==View.VISIBLE)
                {
                    Log.d("HFCONTAINERACTION", "3");
                    selectClassOptions.startAnimation(optionsOut);
                }
            }
        });
    }

//    private interface OptionsSelectAction
//    {
//        public void act(String param);
//    }








    /*       ↓       飛       ↓       */
    /**
     * 將圖片設定給相簿使用
     * @param contentUri
     */
    private void galleryAddPic(Uri contentUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * 在公用資料夾創資料夾
     * @param albumName
     * @return
     */
    private File getExtermalStoragePublicDir(String albumName) {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File f = new File(file, albumName);
        if(!f.exists()){
            f.mkdir();
            return f;
        }
        else
            return new File(file, albumName);
    }
    /**
     * 判斷SD存不存在
     * @return
     */
    private boolean existSDCard(){

        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        else
            return false;
    }

    /**
     * SD卡剩餘空間
     * @return 傳回剩下的MB
     */
    public int getSDFreeSize(){
        //取得SD卡路徑
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //獲取單個數據大小
        long blockSize = sf.getBlockSize();
        //空閒數據塊數量
        long freeBlocks = sf.getAvailableBlocks();

        Log.v("剩餘空間",""+(freeBlocks * blockSize)/1024/1024);

        return (int)((freeBlocks * blockSize)/1024/1024);
    }
    /**
     * 上傳檔案
     * @param sourceFileUri
     * @return
     */
    public int uploadFile(String sourceFileUri, char seq, String id){
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        Log.d("HFFILENAME", siteType + id + seq+ ".jpg");

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            //Log.e("uploadFile", "Source File not exist :"+imagepath);
            Log.e("uploadFile", "Source File not exist :");

            runOnUiThread(new Runnable() {
                public void run() {
                    //messageText.setText("Source File not exist :"+ imagepath);
                }
            });
            return 0;
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                //宣告權限 android 6 以上的要這個
                //不知道6之下的版本需不需要這個 不需要可能要判斷版本再進來
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                //更改圖片在sever的檔名

                fileName =  siteType + id + seq+ ".jpg";


                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);


                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file your server. \n\n";
                            Toast.makeText(AddNewSiteActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(AddNewSiteActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(AddNewSiteActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload Exception", "Exception : "  + e.getMessage(), e);
            }
//            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

    /*       ↑       飛       ↑       */




}
