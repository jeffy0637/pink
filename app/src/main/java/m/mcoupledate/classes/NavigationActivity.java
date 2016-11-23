package m.mcoupledate.classes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import m.mcoupledate.HomePageActivity;
import m.mcoupledate.MainActivity;
import m.mcoupledate.MemberData;
import m.mcoupledate.ModifyMemorialDay;
import m.mcoupledate.MyLike;
import m.mcoupledate.MyTrip;
import m.mcoupledate.R;
import m.mcoupledate.SearchSites;

import static m.mcoupledate.R.id.appBarLayout;

/**
 *      用於左側選單，使用時只要 extends NavigationActivity即可
 *
 *      執行時若發生錯誤，先檢查AndroidManifest裡，該activity有沒有
 *      android:theme="@style/AppTheme.NoActionBar
 *      這個屬性，不行再跟我說
 *
 *
 *      若需用到onBackPressed這條函式(按下手機上的返回鍵時觸發) (不要Override這條!!)
 *      改Override  public Boolean onBackPressedAct() 這條
 *      若在動作完成後不需再執行其他動作(已完成所有動作)時return true，反則return false
 *      可參考EditSite或SearchSites
 *
 *
 *      若需要右上角出現圖示，例如搜尋
 *      自行Override
 *      public boolean onCreateOptionsMenu(Menu menu)
 *      public boolean onOptionsItemSelected(MenuItem item)
 *      若沒有用到的話，把原本activity裏頭的刪掉，因為有些activity預設會有他~~
 *
 *
 *      相關的xml檔的命名都是 navigation_%
 *      修改menu內容 -> menu / navigation_directmenu_drawer.xml
 *      修改相關的導向 -> 這個java檔最下面的onNavigationItemSelected
 */

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private LayoutInflater layoutInflater;

    private RelativeLayout rootView;
    private Toolbar toolbar;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.navigation_template);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        layoutInflater = LayoutInflater.from(this);

        rootView = (RelativeLayout) findViewById(R.id.contentContainer);

        pref = this.getSharedPreferences("pinkpink", 0);

        View header = navigationView.inflateHeaderView(R.layout.navigation_header);
        ((TextView) header.findViewById(R.id.userName)).setText(pref.getString("mName", null));


    }

    @Override
    public void setContentView(@LayoutRes int layoutId)
    {
        View contentView = layoutInflater.inflate(layoutId, null);
        contentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

        rootView.addView(contentView);
    }

    public RelativeLayout getRootView()
    {   return rootView;    }
//
//    public void toolbarInflate(@LayoutRes int layoutId)
//    {
//        View toolbarContent = layoutInflater.inflate(layoutId, null);
//        toolbar.addView(toolbarContent);
//    }

    public SharedPreferences getPref()
    {
        return pref;
    }

    protected void setTitle(String title)
    {   this.getSupportActionBar().setTitle(title); }

    public AppBarLayout getAppBarLayout()
    {
        return (AppBarLayout) findViewById(appBarLayout);
    }



    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }





    @Override@CallSuper
    /**
         *      should not be override!!!!!!!  if want use ->  onBackPressedAct()
         */
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else if (!onBackPressedAct())
            super.onBackPressed();
    }

    public Boolean onBackPressedAct()
    {   return false;   }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.navigation_topbar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//
//        //noinspection SimplifiableIfStatement
////        if (item.getItemId() == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent = null;
        switch (item.getItemId()) {

            case R.id.homepage:
                intent = new Intent(this, HomePageActivity.class);
                break;

            case R.id.nav_memberData:
                // 設定從這個活動跳至 home 的活動
                intent = new Intent(this, MemberData.class);
                break;

            case R.id.nav_memorialDay:
                // 設定從這個活動跳至 home 的活動
                intent = new Intent(this, ModifyMemorialDay.class);
                break;

            case R.id.nav_logout:
                FacebookSdk.sdkInitialize(getApplicationContext());
                LoginManager.getInstance().logOut();
                pref.edit().clear().commit();
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                //登出
                break;

            case R.id.my_attraction:
                intent = new Intent(this, MyLike.class);
                intent.putExtra("searchType", SearchSites.SEARCHTYPE_MYLIKES);
                break;

            case R.id.my_trip:
                intent = new Intent(this, MyTrip.class);
                intent.putExtra("trip","my");
                break;
            case R.id.my_ctrip:
                intent = new Intent(this, MyTrip.class);
                intent.putExtra("trip","collection");
                break;

        }
        startActivity(intent);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
