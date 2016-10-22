package m.mcoupledate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MemberData extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //連結MariaDB
    private String conAPI = "http://140.117.71.216/pinkCon/";
    RequestQueue mQueue;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private String id = MainActivity.getUserId();
    //name editview
    private EditText editText;
    //傳送些改的button
    private Button button;

    SQLiteDatabase db = null;
    private EditText b_year;
    private EditText b_month;
    private EditText b_day;
    private EditText r_year;
    private EditText r_month;
    private EditText r_day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarM);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_home_page);
        TextView userId = (TextView) header.findViewById(R.id.userId);
        userId.setText(id);
        TextView name = (TextView) header.findViewById(R.id.userName);
        name.setText("名字");

        //name editview
        editText = (EditText)findViewById(R.id.username);
        b_year = (EditText) findViewById(R.id.b_year);
        b_year.toString();
        b_month = (EditText)findViewById(R.id.b_month);
        b_month.toString();
        b_day = (EditText)findViewById(R.id.b_day);
        b_day.toString();
        r_year = (EditText)findViewById(R.id.r_year);
        r_year.toString();
        r_month = (EditText)findViewById(R.id.r_month);
        r_month.toString();
        r_day = (EditText)findViewById(R.id.r_day);
        r_day.toString();
        //傳送些改的button
        button = (Button)findViewById(R.id.button);


        DateFormat stringFormatter = new SimpleDateFormat("yyyy-MM-dd");//要轉成String的
        Date now = Calendar.getInstance().getTime();//取得現在時間
        String today = stringFormatter.format(now);//將取得的時間轉成String

        //從SQLite取資料印在頁面上
        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);//打開資料庫
        Cursor cursor = db.rawQuery("SELECT name, gender, birthday, relationship_date from member", null);
        cursor.moveToFirst();
        do{
            editText.setText(cursor.getString(0));

            if(cursor.getString(2) != null){//不為空值才設定起始年月日 => 生日
                b_day.setText(cursor.getString(2).substring(8, 10));
                b_month.setText(cursor.getString(2).substring(5, 7));
                b_year.setText(cursor.getString(2).substring(0, 4));
            }
            if(cursor.getString(3) != null){//不為空值才設定起始年月日 => 交往日
                r_year.setText(cursor.getString(3).substring(0, 4));
                r_month.setText(cursor.getString(3).substring(5, 7));
                r_day.setText(cursor.getString(3).substring(8, 10));
            }
        }while(cursor.moveToNext());
        db.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.member_data, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_check:
                    String nameStr = editText.getText().toString();

                    //設定初始值為空
                    String yearB="";
                    String monthB="";
                    String dayB="";
                    String yearM="";
                    String monthM="";
                    String dayM="";
                    int tag=0;

                    //如果為空 予空值  若非則提取值
                    if("".equals(b_year.getText().toString().trim())) {
                        yearB = "";
                        tag = 1;
                    }
                    else yearB = b_year.getText().toString();

                    if("".equals(b_month.getText().toString().trim())) {
                        monthB = "";
                        tag = 2;
                    }
                    else monthB = b_month.getText().toString();

                    if("".equals(b_day.getText().toString().trim())) {
                        dayB = "";
                        tag = 3;
                    }
                    else dayB = b_day.getText().toString();

                    if("".equals(r_year.getText().toString().trim())) {
                        yearM ="";
                        tag=4;
                    }
                    else yearM = r_year.getText().toString();

                    if("".equals(r_month.getText().toString().trim())) {
                        monthM ="";
                        tag=5;
                    }
                    else monthM = r_month.getText().toString();

                    if("".equals(r_day.getText().toString().trim())) {
                        dayM ="";
                        tag=6;
                    }
                    else dayM = r_day.getText().toString();


                    //空值提醒
                    if(tag==1) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
                    else if(tag==2) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
                    else if(tag==3) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
                    else if(tag==4) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
                    else if(tag==5) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
                    else if(tag==6) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();

                    else {
                        //正確輸入範圍判 else {斷

                        //測試用
                        //Toast.makeText(MemberData.this, "YES", Toast.LENGTH_SHORT).show();

                        int b_year = Integer.parseInt(yearB);
                        int b_month = Integer.parseInt(monthB);
                        int b_day = Integer.parseInt(dayB);
                        int m_year = Integer.parseInt(yearM);
                        int m_month = Integer.parseInt(monthM);
                        int m_day = Integer.parseInt(dayM);

                        if (b_year < 1900 || b_year > 2016 || yearB == "")
                            Toast.makeText(MemberData.this, "請輸入正確生日年份(西元)", Toast.LENGTH_SHORT).show();
                        else if (b_month > 12)
                            Toast.makeText(MemberData.this, "請輸入正確生日月份", Toast.LENGTH_SHORT).show();
                        else if (b_day > 31)
                            Toast.makeText(MemberData.this, "請輸入正確生日日期", Toast.LENGTH_SHORT).show();

                        else if (m_year < 1900 || m_year > 2016 || yearM == "")
                            Toast.makeText(MemberData.this, "請輸入正確紀念日年份(西元)", Toast.LENGTH_SHORT).show();
                        else if (m_month > 12)
                            Toast.makeText(MemberData.this, "請輸入正確紀念日月份", Toast.LENGTH_SHORT).show();
                        else if (m_day > 31)
                            Toast.makeText(MemberData.this, "請輸入正確紀念日日期", Toast.LENGTH_SHORT).show();

                        else {
                            String b_yearS = Integer.toString(b_year);
                            String b_monthS = Integer.toString(b_month);
                            String b_dayS = Integer.toString(b_day);
                            String m_yearS = Integer.toString(m_year);
                            String m_monthS = Integer.toString(m_month);
                            String m_dayS = Integer.toString(m_day);

                            String birthdayStr = "";
                            if(b_month < 10 && b_day< 10)
                                birthdayStr = b_yearS + "-0" + b_monthS + "-0" +b_dayS;
                            else if(b_month < 10 && b_day >= 10)
                                birthdayStr =b_yearS + "-0" + b_monthS + "-" + b_dayS;
                            else if(b_month >= 10 && b_day < 10)
                                birthdayStr = b_yearS + "-" + b_monthS + "-0" + b_dayS;
                            else
                                birthdayStr = b_yearS + "-" + b_monthS + "-" + b_dayS;

                            //彈出是視窗顯示birthdayStr
                            Toast.makeText(MemberData.this, "生日"+ birthdayStr, Toast.LENGTH_SHORT).show();

                            String relationshipstr = "";
                            if(m_month < 10 && m_day < 10)
                                relationshipstr = m_yearS + "-0" + m_monthS.toString() + "-0" + m_dayS;
                            else if(m_month < 10 && m_day >= 10)
                                relationshipstr = m_yearS + "-0" + m_monthS + "-" + m_dayS;
                            else if(m_month >= 10 && m_day < 10)
                                relationshipstr = m_yearS + "-" + m_monthS + "-0" + m_dayS;
                            else
                                relationshipstr =m_yearS + "-" + m_monthS + "-" + m_dayS;

                            //彈出是視窗顯示relationshipstr
                            Toast.makeText(MemberData.this, "紀念日"+ relationshipstr, Toast.LENGTH_SHORT).show();

                            //傳資料給SQLite MariaDB
                            int g=0;
                            db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);//打開SQLite資料庫
                            db.execSQL("UPDATE member SET name = '"+nameStr+"', gender = '"+g+"', birthday = '"+birthdayStr+"', relationship_date = '"+relationshipstr+"' WHERE _id = '"+id+"'");
                            db.close();
                            insertIntoMariaDB(nameStr,g , birthdayStr, relationshipstr);//MariaDB
                            //跳回首頁
                            Intent intent = new Intent(MemberData.this, HomePageActivity.class);
                            startActivity(intent);
                        }
                    }
            }

            if(!msg.equals("")) {
                Toast.makeText(MemberData.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation_topbar view item clicks here.
        int id = item.getItemId();
        //123
        if (id == R.id.nav_memberData) {
            // 設定從這個活動跳至 home 的活動
            Intent intent = new Intent(MemberData.this, MemberData.class);
            // 開始跳頁
            startActivity(intent);
        } else if (id == R.id.nav_memorialDay) {

        }else if (id == R.id.nav_myTravle) {

        } else if (id == R.id.nav_travleEdit) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 將修改的資料放入MariaDB
     */
    public void insertIntoMariaDB(final String name, final int gender, final String birthday, final String relationship)
    {
        mContext = this;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mStringRequest = new StringRequest(Request.Method.POST, conAPI+"updateMemberData.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

                map.put("User", id);
                map.put("Name", name);
                map.put("Gender", String.valueOf(gender));
                map.put("Birthday", birthday);
                map.put("Relationship", relationship);
                return map;
            }
        };
        mRequestQueue.add(mStringRequest);
    }


}
//第一次登入從直接跳來這裡
