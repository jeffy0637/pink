package m.mcoupledate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String conAPI = "http://140.117.71.216/pinkCon/";

   // private String id = MainActivity.getUserId();
    private String id = "1763438647274913";
    private SQLiteDatabase db = null;

    //此TextView是靜態新增的
    private TextView totalDaysDialog;

    private BottomBar mBottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

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
                        Intent go2 = new Intent(HomePageActivity.this, SiteSearchActivity.class);
                        startActivity(go2);
                        break;
                    case R.id.bb_menu_trip:

                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                //重选事件，当前已经选择了这个，又点了这个tab。
                Intent go2 = new Intent(HomePageActivity.this, SiteSearchActivity.class);
                startActivity(go2);
            }
        });

        // 当点击不同按钮的时候，设置不同的颜色
        // 可以用以下三种方式来设置颜色.
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(1, 0xFF5D4037);
        mBottomBar.mapColorForTab(2, "#7B1FA2");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //靜態
        totalDaysDialog = (TextView) findViewById(R.id.totalDaysDialog);
        totalDays();
        printMemorialDays();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //保存BottomBar的状态
        mBottomBar.onSaveInstanceState(outState);
    }

    //算總共交往多久(新版)
    public void totalDays() {
        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select relationship_date from member",null);
        if(cursor.getCount() != 0){
            cursor.moveToFirst();
            do{
                long margin = 0;
                String theDay = cursor.getString(0);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = null;
                try{
                    d1 = formatter.parse(theDay);
                }
                catch (ParseException e){
                    e.printStackTrace();
                }
                Date d2 = new Date();
                long diff = d2.getTime() - d1.getTime();
                String diffstr = "" + diff/(1000*60*60*24);
                totalDaysDialog.setText(diffstr);
            }while (cursor.moveToNext());
            db.close();
        }
    }
    //印出紀念日們(新版)
    public void printMemorialDays() {
        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("select * from memorialday",null);
        cursor.moveToFirst();
        do{
            if(cursor.getCount() != 0){
                String name = cursor.getString(1);
                String theDay = cursor.getString(2);
                DateFormat stringFormatter = new SimpleDateFormat("yyyy-MM-dd");//要轉成String的
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");//要轉成Date的

                Date now = Calendar.getInstance().getTime();//取得現在時間
                String today = stringFormatter.format(now);//將取得的時間轉成String

                //String to Date 紀念日時間
                Date d1 = null;
                try{
                    String d1_str = today.substring(0, 4) + "-" + theDay.substring(5, 7) + "-" + theDay.substring(8, 10);
                    d1 = dateFormatter.parse(d1_str);
                }
                catch (ParseException e){
                    e.printStackTrace();
                }
                //String to Date 現在時間
                Date d2 = null;
                try{
                    d2 = dateFormatter.parse(today);
                }
                catch (ParseException e){
                    e.printStackTrace();
                }
                //把過的忽略
                if(Integer.valueOf(theDay.substring(5, 7)) > Integer.valueOf(today.substring(5, 7))){//月大於 日就不用比了
                    long diff = d2.getTime() - d1.getTime();
                    String diffstr = "" + diff/(1000*60*60*24);
                    init(name, theDay, Math.abs(Integer.valueOf(diffstr)));
                }
                else if(Integer.valueOf(theDay.substring(5, 7)) == Integer.valueOf(today.substring(5, 7)) && Integer.valueOf(theDay.substring(8, 10)) >= Integer.valueOf(today.substring(8, 10))){//月等於 比日
                    long diff = d2.getTime() - d1.getTime();
                    String diffstr = "" + diff/(1000*60*60*24);
                    init(name, theDay, Math.abs(Integer.valueOf(diffstr)));
                }
                else if(Integer.valueOf(theDay.substring(5, 7)) < Integer.valueOf(today.substring(5, 7))){//月小於  年+1
                    try{
                        String d1_str = (Integer.valueOf(today.substring(0, 4)) + 1) + "-" + theDay.substring(5, 7) + "-" + theDay.substring(8, 10);
                        d1 = dateFormatter.parse(d1_str);
                    }
                    catch (ParseException e){
                        e.printStackTrace();
                    }
                    long diff = d2.getTime() - d1.getTime();
                    String diffstr = "" + diff/(1000*60*60*24);
                    init(name, theDay, Math.abs(Integer.valueOf(diffstr)));
                }
                else if(Integer.valueOf(theDay.substring(5, 7)) == Integer.valueOf(today.substring(5, 7)) && Integer.valueOf(theDay.substring(8, 10)) < Integer.valueOf(today.substring(8, 10))){//月等於 日小於  年+1
                    try{
                        String d1_str = (Integer.valueOf(today.substring(0, 4)) + 1) + "-" + theDay.substring(5, 7) + "-" + theDay.substring(8, 10);
                        d1 = dateFormatter.parse(d1_str);
                    }
                    catch (ParseException e){
                        e.printStackTrace();
                    }
                    long diff = d2.getTime() - d1.getTime();
                    String diffstr = "" + diff/(1000*60*60*24);
                    init(name, theDay, Math.abs(Integer.valueOf(diffstr)));
                }
            }
        }while(cursor.moveToNext());
    }

    //新增單筆紀念日資料
    public void init(String name, String date, int diff)
    {
        String diffDay = Integer.toString(diff);
        LinearLayout linearLayout1=(LinearLayout)findViewById(R.id.activity_service_select);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View view =LayoutInflater.from(this).inflate(R.layout.mday_data, null);
        view.setLayoutParams(lp);
        TextView tv1 = (TextView) view.findViewById(R.id.mContext);
        TextView tv2 = (TextView) view.findViewById(R.id.mTime);
        TextView tv3 = (TextView) view.findViewById(R.id.diffTime);
        tv1.setText(name);
        tv2.setText(date);
        tv3.setText(diffDay);
        linearLayout1.addView(view);
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
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_memberData) {
            // 設定從這個活動跳至 home 的活動
            Intent intent = new Intent(HomePageActivity.this, MemberData.class);
            // 開始跳頁
            startActivity(intent);
        } else if (id == R.id.nav_memorialDay) {
            // 設定從這個活動跳至 home 的活動
            Intent intent = new Intent(HomePageActivity.this, ModifyMemorialDay.class);
            // 開始跳頁
            startActivity(intent);

        } else if (id == R.id.homepage) {
            // 設定從這個活動跳至 home 的活動
            Intent intent = new Intent(HomePageActivity.this, HomePageActivity.class);
            // 開始跳頁
            startActivity(intent);

        }else if (id == R.id.nav_myTravle) {
            Intent go2 = new Intent(HomePageActivity.this, SiteAttractionActivity.class);
            startActivity(go2);

        } else if (id == R.id.nav_travleEdit) {
            Intent go2 = new Intent(HomePageActivity.this, AddNewSiteActivity.class);
            go2.putExtra("siteType", "r");
            startActivity(go2);

        } else if (id == R.id.nav_logout) {
            //登出

        } else if (id == R.id.my_attraction) {
            Intent go2 = new Intent(HomePageActivity.this, MyAttractionActivity.class);
            startActivity(go2);
        } else if (id == R.id.my_restaurant) {
            Intent go2 = new Intent(HomePageActivity.this, MyRestaurantActivity.class);
            startActivity(go2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
