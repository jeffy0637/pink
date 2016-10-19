package m.mcoupledate.classes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import m.mcoupledate.EditSite;
import m.mcoupledate.HomePageActivity;
import m.mcoupledate.MainActivity;
import m.mcoupledate.MemberData;
import m.mcoupledate.ModifyMemorialDay;
import m.mcoupledate.R;
import m.mcoupledate.SiteAttractionActivity;
import m.mcoupledate.SiteInfo;
import m.mcoupledate.StrokeActivity;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.navigation_template);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pref = this.getSharedPreferences("pinkpink", 0);

        View header = navigationView.inflateHeaderView(R.layout.nav_header_home_page);
        ((TextView) header.findViewById(R.id.userName)).setText(getPref().getString("mName", null));
    }

    @Override
    public void setContentView(@LayoutRes int layoutId)
    {
        ((RelativeLayout) findViewById(R.id.contentContainer)).addView(LayoutInflater.from(this).inflate(layoutId, null));
    }

    public SharedPreferences getPref()
    {   return pref;    }

    public Editor getPrefEditor()
    {   return pref.edit();    }




    @Override@CallSuper
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.navigation_topbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
//        if (item.getItemId() == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent = null;
        switch (item.getItemId()) {

            case R.id.nav_memberData:
                // 設定從這個活動跳至 home 的活動
                intent = new Intent(this, MemberData.class);
                break;

            case R.id.nav_memorialDay:
                // 設定從這個活動跳至 home 的活動
                intent = new Intent(this, ModifyMemorialDay.class);
                break;

            case R.id.homepage:
                // 設定從這個活動跳至 home 的活動
                intent = new Intent(this, HomePageActivity.class);
                break;

            case R.id.nav_myTravle:
                intent = new Intent(this, SiteAttractionActivity.class);
                break;

            case R.id.nav_travleEdit:
                intent = new Intent(this, EditSite.class);
                intent.putExtra("siteType", "r");
                break;

            case R.id.nav_logout:
                FacebookSdk.sdkInitialize(getApplicationContext());
                LoginManager.getInstance().logOut();
                this.getSharedPreferences("pinkpink", 0).edit().clear().commit();
                intent = new Intent(this, MainActivity.class);

                //登出
                break;

            case R.id.my_attraction:
                intent = new Intent(this, SiteInfo.class);
                intent.putExtra("sId", "7");
                break;

            case R.id.my_restaurant:
                intent = new Intent(this, StrokeActivity.class);
                break;

        }
        startActivity(intent);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
