package m.mcoupledate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
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
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import m.mcoupledate.classes.PinkClusterMapFragmentActivity;
import m.mcoupledate.classes.adapters.CommentAdapter;
import m.mcoupledate.classes.adapters.RecyclerAlbumAdapter;
import m.mcoupledate.classes.mapClasses.WorkaroundMapFragment;
import m.mcoupledate.classes.funcs.PinkCon;

import static m.mcoupledate.R.id.addLikeBtn;

public class SiteInfo extends PinkClusterMapFragmentActivity{

    SharedPreferences pref;

    private RequestQueue mQueue;
    private PinkCon.InitErrorBar initErrorBar;

    private String sId, mId, picId;
    private int siteType;
    private Boolean ifLiked;

    //印資訊
    private HashMap<String, TextView> siteCol;
    private RatingBar loveRateBar;


    private Button phoneCallBtn, commentBtn;
    private Boolean ifCommented = false;
    private View commentPage;
    private RatingBar commentRBar;
    private TextView commentText;
    private String from;


    Button editLikeBtn, addTravelBtn;
    TextView editSiteBtn;


    private ScrollView scrollView;

    private GoogleMap mMap;
    private WorkaroundMapFragment mapFragment;


    RecyclerView album;
    LinearLayoutManager mLayoutManager;
    RecyclerAlbumAdapter raAdapter;


    ListView allComments;
    CommentAdapter allCommentsAdapter;

    private String tId;
    private String column;
    private String count;
    private String transId;
    private Boolean fromTravel = false;

    final String url = "https://couple-project.firebaseio.com/travel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_info);

        pref = this.getSharedPreferences("pinkpink", 0);
        mId = pref.getString("mId", null);


        mQueue = Volley.newRequestQueue(this);
        initErrorBar = PinkCon.getInitErrorSnackBar(getRootView(), PinkCon.INIT_FAIL, this);

        sId = this.getIntent().getStringExtra("sId");
        from = this.getIntent().getStringExtra("from");
        tId = this.getIntent().getStringExtra("tId");
        column = this.getIntent().getStringExtra("column");
        count = this.getIntent().getStringExtra("count");
        fromTravel = this.getIntent().getBooleanExtra("fromTravel",false);
        transId = this.getIntent().getStringExtra("transId");

        //Toast.makeText(this, tId + " " + column + " " + count, Toast.LENGTH_SHORT).show();

        siteCol = new HashMap<String, TextView>();
        //先印圖片 aId+a 不是sId
        siteCol.put("sName", (TextView) findViewById(R.id.site_name));
        siteCol.put("area", (TextView) findViewById(R.id.site_area));
        siteCol.put("time", (TextView) findViewById(R.id.site_time));
        siteCol.put("address", (TextView) findViewById(R.id.site_address));
        siteCol.put("description", (TextView) findViewById(R.id.site_description));
        siteCol.put("phone", (TextView) findViewById(R.id.attraction_phone));
        siteCol.put("website", (TextView) findViewById(R.id.attraction_website));
        siteCol.put("transportation", (TextView) findViewById(R.id.site_transportation));
        siteCol.put("activity", (TextView) findViewById(R.id.site_activity));
        siteCol.put("note", (TextView) findViewById(R.id.site_note));

        siteCol.get("website").setAutoLinkMask(Linkify.ALL);

        loveRateBar = (RatingBar)findViewById(R.id.loveRateBar);
        LayerDrawable stars = (LayerDrawable) loveRateBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.pinkpink), PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.pinkpink), PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP); // for empty stars

        Button view = (Button)findViewById(R.id.addLikeBtn);
        Button view1 = (Button)findViewById(R.id.addTravelBtn);
        if (fromTravel)
            view1.setVisibility(View.VISIBLE);
        else {
            switch (from)  /*status 只能為整數、長整數或字元變數.*/ {
                case "search":

                    view.setVisibility(View.VISIBLE);
                    break;
                case "like":

                    break;

            }
        }

        //從資料庫抓景點資料出來 (函式移至onMapReady呼叫)
//        initSiteInfoFromMariDB();

        editLikeBtn = (Button)findViewById(addLikeBtn);
        editLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editLike();
            }
        });

        addTravelBtn = (Button)findViewById(R.id.addTravelBtn);
        addTravelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firebase.setAndroidContext(view.getContext());//this用mBoardView.getContext()取代
                new Firebase(url).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if((""+dataSnapshot.child("tId").getValue()).equals(tId)){
                            //在特定行程加入景點
                            Firebase siteRef = (dataSnapshot.child("site").child("day" + column).child("" + count).getRef());
                            Site site = new Site(Long.parseLong(count), "", Long.parseLong(sId) , 1);//剛開始journel應該是null
                            siteRef.setValue(site);
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
                //回傳&回去行程
                Intent intent = new Intent(SiteInfo.this, StrokeActivity.class);
                intent.putExtra("sId", sId);
                intent.putExtra("tripId", tId);
                intent.putExtra("tripType", "my");
//                intent.putExtra("column", "" + column);
//                intent.putExtra("count", "" + count);
                startActivity(intent);
            }
        });

        editSiteBtn = (TextView)findViewById(R.id.editSiteBtn);
        editSiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(SiteInfo.this, EditSite.class);
                i.putExtra("sId", sId);
                i.putExtra("siteType", siteType);
                i.putExtra("picId", picId);
                startActivity(i);
            }
        });



        scrollView = (ScrollView) findViewById(R.id.scrollView);


        mapFragment = getMapFragment(R.id.siteInfoMap);
        mapFragment.setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });



        album = (RecyclerView)findViewById(R.id.album);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        album.setLayoutManager(mLayoutManager);
        //album.setHasFixedSize(true);    //   如果可以确定每个item的高度是固定的，设置这个选项可以提高性能

        raAdapter = new RecyclerAlbumAdapter(this, album.getHeight());
        album.setAdapter(raAdapter);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap mMap)
    {
        super.onMapReady(mMap);

        this.mMap = mMap;

        initSiteInfoFromMariDB();

    }


    /**
     * 用volley取景點資料
     */
    private void initSiteInfoFromMariDB(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL +"siteInfo_initSiteInfo.php?sId="+sId+"&mId="+mId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("HFSITE", "siteInfo_initSiteInfo.php?sId="+sId+"&mId="+mId);
                        Log.d("HFSITE", response);

                        try {
                            final JSONObject o = new JSONObject(response);

                            final JSONObject site = o.getJSONArray("site").getJSONObject(0);

                            String[] siteColName = {"sName", "area", "address", "description", "phone", "website", "transportation", "activity", "note"};
                            for (String colName : siteColName) {
                                String content;
                                if (colName.compareTo("area") == 0)
                                    content = site.optString("city") + site.optString("area");//合併城市跟區域
                                else
                                    content = site.optString(colName);

                                if (content != null && content.compareTo("null") != 0)
                                    siteCol.get(colName).setText(content);
                            }


                            String time = "";
                            String[] weekDayNames = {"一", "二", "三", "四", "五", "六", "日"};
                            int nowDay = 0;

                            JSONArray businessHours = o.getJSONArray("business_hours");
                            for(int i = 0 ; i < businessHours.length() ; i++)
                            {
                                JSONObject aPeriod = businessHours.getJSONObject(i);

                                if (nowDay!=aPeriod.optInt("day"))
                                {
                                    time += "("+ weekDayNames[(aPeriod.optInt("day")-1)] +")  " + aPeriod.optString("start_time") + " ~ " + aPeriod.optString("end_time")+"\n";
                                    nowDay = aPeriod.optInt("day");
                                }
                                else
                                {
                                    time += " 　    " + aPeriod.optString("start_time") + " ~ " + aPeriod.optString("end_time")+"\n";
                                }
                            }
                            siteCol.get("time").setText(time);


                            loveRateBar.setRating((float)o.getJSONArray("diffSite").getJSONObject(0).optDouble("site_love"));

                            picId = o.getJSONArray("diffSite").getJSONObject(0).optString("picId");
                            siteType = o.getJSONArray("diffSite").getJSONObject(0).optInt("siteType");

                            phoneCallBtn = (Button) findViewById(R.id.call);
                            phoneCallBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                    phoneIntent.setData(Uri.parse("tel:" + site.optString("phone")));
                                    //phoneIntent.setData(Uri.parse("tel:0981916023"));
                                    try {
                                        startActivity(phoneIntent);
                                        finish();
                                    } catch (android.content.ActivityNotFoundException ex) {
                                        Toast.makeText(SiteInfo.this, "Call faild, please try again later.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });


                            commentPage = LayoutInflater.from(SiteInfo.this).inflate(R.layout.dialog_score, null);

                            commentRBar = ((RatingBar) commentPage.findViewById(R.id.rating));
                            LayerDrawable stars = (LayerDrawable) commentRBar.getProgressDrawable();
                            stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.pinkpink), PorterDuff.Mode.SRC_ATOP); // for filled stars
                            stars.getDrawable(1).setColorFilter(getResources().getColor(R.color.pinkpink), PorterDuff.Mode.SRC_ATOP); // for half filled stars
                            stars.getDrawable(0).setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP); // for empty stars

                            commentText = (TextView) commentPage.findViewById(R.id.text);

                            commentBtn = (Button) findViewById(R.id.score);
                            final AlertDialog commentDialog = new AlertDialog.Builder(SiteInfo.this)
                                    .setTitle("請輸入評論")
                                    .setView(commentPage)
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            if (ifCommented)
                                                submitComment(commentText.getText().toString(), commentRBar.getRating(), "edit");
                                            else
                                                submitComment(commentText.getText().toString(), commentRBar.getRating(), "new");
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {}
                                    }).create();

                            commentBtn.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View arg0) {
                                    //評分
                                    commentDialog.show();
                                }});


                            if (o.optJSONArray("myComment").length()==0)
                            {
                                ifCommented = false;
                            }
                            else
                            {
                                ifCommented = true;

                                JSONObject myComment = o.optJSONArray("myComment").optJSONObject(0);
                                setCommentContent((float) myComment.optDouble("person_love"), myComment.optString("text"));
                            }



                            ifLiked = o.optBoolean("ifLiked");
                            setEditLikeBtn();

                            //印景點圖片
                            printPicture(picId);

                            LatLng siteLatLng = new LatLng(site.optDouble("Py"), site.optDouble("Px"));

//                            mMap..animateCamera(CameraUpdateFactory.newLatLngZoom(siteLatLng, (mMap.getMaxZoomLevel()-8)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(siteLatLng, (mMap.getMaxZoomLevel()-8)));

                            setTheSiteMarker(siteLatLng, site.optString("sName"));
//                            mMap.addMarker(new MarkerOptions().position(siteLatLng).title(site.optString("sName"))
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.thesite)));

                            allCommentsAdapter = new CommentAdapter(SiteInfo.this, o.optJSONArray("allComments"));
                            allComments = (ListView) findViewById(R.id.allComments);
                            allComments.setAdapter(allCommentsAdapter);



                        }
                        catch (Exception e)
                        {    initErrorBar.show("HFINITSITEA", e.getMessage());   }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {    initErrorBar.show("HFINITSITE", error.getMessage());   }
                });

        mQueue.add(stringRequest);


        loadPinkClusterSites(mQueue, null);
    }

    /**
     * 三種site的印圖片method
     */
    private void printPicture(String id)
    {
        int seq = 97;
        int picNum = getResources().getInteger(R.integer.sitePicMaxLimit);

        if (raAdapter.getItemCount()>0)
            raAdapter.clear(false);

        while (picNum>0)
        {
            String url = PinkCon.URL + "images/sitePic/" + id + (char)(seq) +".jpg";
//            Log.d("sitePicUrl", url);
            Glide.with(SiteInfo.this)
                    .load(url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(Resources.getSystem().getDisplayMetrics().widthPixels, 300)
                          {
                              @Override
                              public void onResourceReady(Bitmap bitmap, GlideAnimation anim)
                              {
                                  raAdapter.add(bitmap, null);
                              }
                              @Override
                              public void onLoadFailed(Exception e, Drawable errorDrawable)
                              {     }

                          }
                    );

            ++seq;
            --picNum;
        }
    }


    private void submitComment(final String text, final float personLove, final String editType)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL+"siteInfo_editComment.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        if (editType.compareTo("new")==0)
                        {
                            ifCommented = true;
                            setCommentContent(personLove, text);
                            allCommentsAdapter.add(mId, pref.getString("mName", ""), text, personLove);
                        }
                        else
                        {
                            allCommentsAdapter.update(mId, pref.getString("mName", ""), text, personLove);
                        }

                        loveRateBar.setRating(Float.valueOf(response));

                        Toast.makeText(getApplicationContext(), "謝謝您的評分", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        PinkCon.retryConnect(getRootView(), PinkCon.SUBMIT_FAIL, initErrorBar,
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {   submitComment(text, personLove, editType);  }
                            });
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("sId", sId);
                map.put("member", mId);
                map.put("text", text);
                map.put("person_love", String.valueOf(personLove));
                map.put("siteType", String.valueOf(siteType));
                map.put("editType", editType);

                return map;
            }
        };

        mQueue.add(stringRequest);

    }


    private void setCommentContent(float personLove, String text)
    {
        commentRBar.setRating(personLove);
        commentText.setText(text);
        commentBtn.setText("編輯評論");
    }



    private void editLike()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL+"siteInfo_editMyLike.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        if (ifLiked)
                        {
                            Toast.makeText(SiteInfo.this, "移除成功", Toast.LENGTH_SHORT).show();
                            ifLiked = false;
                        }
                        else
                        {
                            Toast.makeText(SiteInfo.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            ifLiked = true;
                        }

                        setEditLikeBtn();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        PinkCon.retryConnect(getRootView(), PinkCon.CONNECT_FAIL, initErrorBar,
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {   editLike();  }
                            });
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("sId", sId);
                map.put("mId", mId);

                if (ifLiked)
                    map.put("editType", "remove");
                else
                    map.put("editType", "add");

                return map;
            }
        };

        mQueue.add(stringRequest);
    }


    private void setEditLikeBtn()
    {
        if (ifLiked)
            editLikeBtn.setText("移除收藏");
        else
            editLikeBtn.setText("加入收藏");
    }





}



//加入我的收藏時是SQLite 跟 MariaDB一起新增
//所以在mainactivity要建SQLite表格
