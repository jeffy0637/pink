package m.mcoupledate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import m.mcoupledate.classes.NavigationActivity;

public class StrokeSearch extends NavigationActivity {

    private BottomBar mBottomBar;
    private ViewPager mViewPager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_search);

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
                        //Intent go2 = new Intent(StrokeSearch.this, SiteSearchActivity.class);
                        //startActivity(go2);
                        break;
                    case R.id.bb_menu_trip:

                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                //重选事件，当前已经选择了这个，又点了这个tab。
                switch (menuItemId) {
                    case R.id.bb_menu_memorialday:
                        Intent go = new Intent(StrokeSearch.this, HomePageActivity.class);
                        startActivity(go);
                        break;
                    case R.id.bb_menu_site:
                        Intent go2 = new Intent(StrokeSearch.this, SiteSearchActivity.class);
                        startActivity(go2);
                        break;
                    case R.id.bb_menu_trip:

                        break;
                }

            }
        });

        // 当点击不同按钮的时候，设置不同的颜色
        // 可以用以下三种方式来设置颜色.
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(1, 0xFF5D4037);
        mBottomBar.mapColorForTab(2, "#7B1FA2");

        initView();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //保存BottomBar的状态
        mBottomBar.onSaveInstanceState(outState);
    }

    public void initView(){

        int initSearchType;
        if (this.getIntent().getIntExtra("searchType", -1)==SearchSites.SEARCHTYPE_MYLIKES)
            initSearchType = SearchSites.SEARCHTYPE_MYLIKES;
        else
            initSearchType = SearchSites.SEARCHTYPE_BROWSE;

    }
}