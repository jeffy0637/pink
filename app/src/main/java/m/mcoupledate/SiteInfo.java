package m.mcoupledate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SiteInfo extends AppCompatActivity {

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
    private TextView site_phone;
    private TextView site_website;
    private TextView site_transportation;
    private TextView site_activity;
    private TextView site_note;

    private Button call_button;

    private String sId = "7";//測試用
    private String Id = "";
    private Double love = 0.0;
    Button add_site, add_travel, scoreB;
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
        site_phone = (TextView) findViewById(R.id.attraction_phone);
        site_website = (TextView) findViewById(R.id.attraction_website) ;
        site_website.setAutoLinkMask(Linkify.ALL);
        site_transportation = (TextView) findViewById(R.id.site_transportation);
        site_activity = (TextView) findViewById(R.id.site_activity);
        site_note = (TextView) findViewById(R.id.site_note);
        call_button = (Button) findViewById(R.id.call);
        //在這裡判斷是哪一種site

        //從資料庫抓景點資料出來
        getAttractionInformationFromMariDB();

        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:" + site_phone.getText().toString()));
                //phoneIntent.setData(Uri.parse("tel:0981916023"));
                try {
                    startActivity(phoneIntent);
                    finish();
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SiteInfo.this,
                            "Call faild, please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        add_site = (Button)findViewById(R.id.collect_site);
        add_travel = (Button)findViewById(R.id.join_trip);
        edit = (TextView)findViewById(R.id.want_edit);
        scoreB = (Button)findViewById(R.id.score);

        add_site.setOnClickListener(addSiteOnClickListener);
        add_travel.setOnClickListener(addTravelOnClickListener);
        edit.setOnClickListener(editOnClickListener);
        scoreB.setOnClickListener(score);
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
                            String city_area = o.getJSONArray("site").getJSONObject(0).optString("city") + o.getJSONArray("site").getJSONObject(0).optString("area");
                            site_area.setText(city_area);
                            site_address.setText(o.getJSONArray("site").getJSONObject(0).optString("address"));
                            site_description.setText(o.getJSONArray("site").getJSONObject(0).optString("description"));
                            site_phone.setText(o.getJSONArray("site").getJSONObject(0).optString("phone"));
                            //site_website.setText(o.getJSONArray("site").getJSONObject(0).optString("website"));
                            String website = o.getJSONArray("site").getJSONObject(0).optString("website");
                            if(website=="null") website = "無";
                            site_website.setText(website);
                            site_transportation.setText(o.getJSONArray("site").getJSONObject(0).optString("transportation"));
                            site_activity.setText(o.getJSONArray("site").getJSONObject(0).optString("activity"));
                            site_note.setText(o.getJSONArray("site").getJSONObject(0).optString("note"));

                            love = o.getJSONArray("three_sites").getJSONObject(0).optDouble("love");

                            Id = o.getJSONArray("three_sites").getJSONObject(0).optString("ID");
                            //印景點圖片
                            printPicture();

                                for(int i = 0 ; i < o.getJSONArray("business_hours").length() ; i++){

                                    int day = Integer.valueOf(o.getJSONArray("business_hours").getJSONObject(i).optString("day"));

                                    switch (day){
                                        case 1:
                                            site_time1.setText(o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                            timeL.addView(site_time1);
                                            break;
                                        case 2:
                                            site_time2.setText(o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                            timeL.addView(site_time1);
                                            break;
                                        case 3:
                                            site_time3.setText(o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                            timeL.addView(site_time1);
                                            break;
                                        case 4:
                                            site_time4.setText(o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                            timeL.addView(site_time1);
                                            break;
                                        case 5:
                                            site_time5.setText(o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                            timeL.addView(site_time1);
                                            break;
                                        case 6:
                                            site_time6.setText(o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                            timeL.addView(site_time1);
                                            break;
                                        case 7:
                                            site_time7.setText(o.getJSONArray("business_hours").getJSONObject(day-1).optString("start_time") + "-" + o.getJSONArray("business_hours").getJSONObject(day-1).optString("end_time"));
                                            timeL.addView(site_time1);
                                            break;
                                        default:
                                            Log.v("error","營業時間有錯");
                                            break;
                                    }
                                }
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
     */
    private void printPicture(){
        Glide.with(this)
                .load(conAPI + "images/sitePic/" + Id + "a.jpg")
                .asBitmap()
                .error(R.drawable.defualt_img)
                .into(picture1);
        Glide.with(this)
                .load(conAPI + "images/sitePic/" + Id + "b.jpg")
                .asBitmap()
                .error(R.drawable.defualt_img)
                .into(picture2);
        Glide.with(this)
                .load(conAPI + "images/sitePic/" + Id + "c.jpg")
                .asBitmap()
                .error(R.drawable.defualt_img)
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

    private Button.OnClickListener score
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            //評分
            LayoutInflater inflater = LayoutInflater.from(SiteInfo.this);
            final View v = inflater.inflate(R.layout.dialog_score, null);

            new AlertDialog.Builder(SiteInfo.this)
                    .setTitle("請輸入你的id")
                    .setView(v)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "謝謝您的評分", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();


        }};



}

//加入我的收藏時是SQLite 跟 MariaDB一起新增
//所以在mainactivity要建SQLite表格
