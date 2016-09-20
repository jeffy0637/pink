package m.mcoupledate;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SiteInfoActivity extends AppCompatActivity {

    private String conAPI = "http://140.117.71.216/pinkCon/";
    private Context mContext;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;


    //印site照片
    private ImageView picture1;
    private ImageView picture2;
    private ImageView picture3;

    //印資訊
    private TextView site_name;
    private TextView site_area;
    private TextView site_address;
    private TextView site_description;
    private TextView attraction_phone;
    private TextView attraction_website;
    private TextView site_transportation;
    private TextView site_activity;
    private TextView site_note;

    private String sId = "7";//測試用
    private String aId = "";
    private Double love = 0.0;

    Button add_site, add_travel;
    TextView edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_info);

        //先印圖片 aId+a 不是sId
        picture1 = (ImageView) findViewById(R.id.picture1);
        picture2 = (ImageView) findViewById(R.id.picture2);
        picture3 = (ImageView) findViewById(R.id.picture3);
        site_name = (TextView) findViewById(R.id.site_name);
        site_area = (TextView) findViewById(R.id.site_area);
        site_address = (TextView) findViewById(R.id.site_address);
        site_description = (TextView) findViewById(R.id.site_description);
        attraction_phone = (TextView) findViewById(R.id.attraction_phone);
        attraction_website = (TextView) findViewById(R.id.attraction_website) ;
        attraction_website.setAutoLinkMask(Linkify.ALL);
        site_transportation = (TextView) findViewById(R.id.site_transportation);
        site_activity = (TextView) findViewById(R.id.site_activity);
        site_note = (TextView) findViewById(R.id.site_note);
        //site_time = (TextView) findViewById(R.id.site_time);

        add_site = (Button)findViewById(R.id.collect_site);
        add_travel = (Button)findViewById(R.id.join_trip);
        edit = (TextView)findViewById(R.id.want_edit);

        add_site.setOnClickListener(addSiteOnClickListener);
        add_travel.setOnClickListener(addTravelOnClickListener);
        edit.setOnClickListener(editOnClickListener);
        //在這裡判斷是哪一種site

        //從資料庫抓景點資料出來
        getAttractionInformationFromMariDB();
    }

    /**
     * 用valley取景點資料
     */
    private void getAttractionInformationFromMariDB(){
        mContext = this;
        final TextView site_time1 = new TextView(this);
        final TextView site_time2 = new TextView(this);
        final TextView site_time3 = new TextView(this);
        final TextView site_time4 = new TextView(this);
        final TextView site_time5 = new TextView(this);
        final TextView site_time6 = new TextView(this);
        final TextView site_time7 = new TextView(this);
        mRequestQueue = Volley.newRequestQueue(mContext);
        mStringRequest = new StringRequest(Request.Method.POST, conAPI+"getSiteInformationFromMariDB.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject o = new JSONObject(response);
                            LinearLayout timeL = (LinearLayout) findViewById(R.id.time_L);
                            site_name.setText(o.getJSONArray("site").getJSONObject(0).optString("sName"));

                            //合併城市跟區域
                            String city_area = o.getJSONArray("attraction").getJSONObject(0).optString("city") + o.getJSONArray("attraction").getJSONObject(0).optString("area");
                            site_area.setText(city_area);
                            site_address.setText(o.getJSONArray("site").getJSONObject(0).optString("address"));
                            site_description.setText(o.getJSONArray("site").getJSONObject(0).optString("description"));
                            attraction_phone.setText(o.getJSONArray("site").getJSONObject(0).optString("phone"));
                            String website = o.getJSONArray("site").getJSONObject(0).optString("website");
                            if(website=="null") website = "無";
                            attraction_website.setText(website);

                            String transportation = o.getJSONArray("site").getJSONObject(0).optString("transportation");
                            if(transportation=="null") transportation = "";
                            site_transportation.setText(transportation);

                            site_activity.setText(o.getJSONArray("site").getJSONObject(0).optString("activity"));
                            site_note.setText(o.getJSONArray("site").getJSONObject(0).optString("note"));
                            love = o.getJSONArray("attraction").getJSONObject(0).optDouble("love");

                            for(int i = 0 ; i < o.getJSONArray("business_hours").length() ; i++){

                                int day = Integer.valueOf(o.getJSONArray("business_hours").getJSONObject(i).optString("day"));

                                switch (day){
                                    case 1:
                                        site_time1.setText("星期一 : "+o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                        timeL.addView(site_time1);
                                        break;
                                    case 2:
                                        site_time2.setText("星期二 : "+o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                        timeL.addView(site_time2);
                                        break;
                                    case 3:
                                        site_time3.setText("星期三 : "+o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                        timeL.addView(site_time3);
                                        break;
                                    case 4:
                                        site_time4.setText("星期四 : "+o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                        timeL.addView(site_time4);
                                        break;
                                    case 5:
                                        site_time5.setText("星期五 : "+o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                        timeL.addView(site_time5);
                                        break;
                                    case 6:
                                        site_time6.setText("星期六 : "+o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                        timeL.addView(site_time6);
                                        break;
                                    case 7:
                                        site_time7.setText("星期日 : "+o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                        timeL.addView(site_time7);
                                        break;
                                    default:
                                        Log.v("error","營業時間有錯");
                                        break;
                                }
                            }

                            aId = o.getJSONArray("attraction").getJSONObject(0).optString("aId");
                            //印景點圖片
                            printPicture(aId);
                        }
                        catch (Exception e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("sId", sId);
                return map;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    /**
     * 三種site的印圖片method
     * @param id attraction, restaurant, hotel的id
     */
    private void printPicture(String id){
        Glide.with(this)
                .load(conAPI + "images/sitePic/" + id + "a.jpg")
                .asBitmap()
                .fitCenter()
                .error(null)
                .into(picture1);
        Glide.with(this)
                .load(conAPI + "images/sitePic/" + id + "b.jpg")
                .asBitmap()
                .fitCenter()
                .error(null)
                .into(picture2);
        Glide.with(this)
                .load(conAPI + "images/sitePic/" + id + "c.jpg")
                .asBitmap()
                .fitCenter()
                .error(null)
                .into(picture3);
    }

    private Button.OnClickListener addSiteOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            //加入收藏
        }};

    private Button.OnClickListener addTravelOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            //加入行程
        }};

    private Button.OnClickListener editOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            //修改景點
        }};
}
