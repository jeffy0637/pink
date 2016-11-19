package m.mcoupledate;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;
import java.util.List;

import m.mcoupledate.classes.NavigationActivity;
import m.mcoupledate.classes.adapters.PinkFragmentPagerAdapter;

public class SiteSearchActivity extends NavigationActivity implements
        ViewPager.OnPageChangeListener,
        View.OnClickListener
{

    private ViewPager mViewPager;
    private List fragmentList;
    private ImageView line_tab; // tab选项卡的下划线
    private boolean isScrolling = false; // 手指是否在滑动
    private boolean isBackScrolling = false; // 手指离开后的回弹
    private long startTime = 0;
    private long currentTime = 0;
    private TextView tv_tab0, tv_tab1, tv_tab2; // 3个选项卡
    private int moveOne = 0; // 下划线移动一个选项卡
    private BottomBar mBottomBar;

    @Override
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
                        /*Intent go2 = new Intent(SiteSearch.this, HomePageActivity.class);
                        startActivity(go2);*/
                        break;
                    case R.id.bb_menu_site:
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
                        Intent go2 = new Intent(SiteSearchActivity.this, HomePageActivity.class);
                        startActivity(go2);
                        break;
                    case R.id.bb_menu_site:
                        break;
                    case R.id.bb_menu_trip:
                        Intent go3 = new Intent(SiteSearchActivity.this, StrokeSearch.class);
                        startActivity(go3);
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
        initLineImage();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //保存BottomBar的状态
        mBottomBar.onSaveInstanceState(outState);
    }


    public void initView(){

        mViewPager = (ViewPager) findViewById(R.id.myViewPager);

        int initSearchType;
        if (this.getIntent().getIntExtra("searchType", -1)==SearchSites.SEARCHTYPE_MYLIKES)
            initSearchType = SearchSites.SEARCHTYPE_MYLIKES;
        else
            initSearchType = SearchSites.SEARCHTYPE_BROWSE;

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(SearchSites.newInstance(initSearchType, SearchSites.SITETYPE_ATTRACTION));
        fragmentList.add(SearchSites.newInstance(initSearchType, SearchSites.SITETYPE_RESTAURANT));


        PinkFragmentPagerAdapter pinkFragmentPagerAdapter = new PinkFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(pinkFragmentPagerAdapter);

        tv_tab0 = (TextView) findViewById(R.id.tab0);
        tv_tab1 = (TextView) findViewById(R.id.tab1);
        mViewPager.setCurrentItem(0);
        tv_tab0.setTextColor(Color.parseColor("#F7CAC9"));
        tv_tab1.setTextColor(Color.parseColor("#FFFFFF"));
        tv_tab0.setOnClickListener(this);
        tv_tab1.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(this);

        line_tab = (ImageView) findViewById(R.id.line_tab);

    }

    private void initLineImage() {
        // TODO Auto-generated method stub
        /** * 获取屏幕的宽度 */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        /** * 重新设置下划线的宽度 */
        ViewGroup.LayoutParams lp = line_tab.getLayoutParams();
        lp.width = screenW / 2;
        line_tab.setLayoutParams(lp);
        moveOne = lp.width;
        // 滑动一个页面的距离
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tab0: mViewPager.setCurrentItem(0);
                break;
            case R.id.tab1: mViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentTime = System.currentTimeMillis();
        if (isScrolling && (currentTime - startTime > 200)) {
            movePositionX(position, moveOne * positionOffset);
            startTime = currentTime;
        }
        if (isBackScrolling) {
            movePositionX(position);
        }
    }
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                tv_tab0.setTextColor(Color.parseColor("#F7CAC9"));
                tv_tab1.setTextColor(Color.parseColor("#FFFFFF"));
                movePositionX(0);
                break;
            case 1:
                tv_tab0.setTextColor(Color.parseColor("#FFFFFF"));
                tv_tab1.setTextColor(Color.parseColor("#F7CAC9"));
                movePositionX(1);
                break;
            default:
                break; }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 1:
                isScrolling = true;
                isBackScrolling = false;
                break;
            case 2:
                isScrolling = false;
                isBackScrolling = true;
                break;
            default:
                isScrolling = false;
                isBackScrolling = false;
                break;
        }
    }

    private void movePositionX(int toPosition, float positionOffsetPixels) {
        // TODO Auto-generated method stub
        float curTranslationX = line_tab.getTranslationX();
        float toPositionX = moveOne * toPosition + positionOffsetPixels;
        ObjectAnimator animator = ObjectAnimator.ofFloat(line_tab, "translationX", curTranslationX, toPositionX);
        animator.setDuration(500); animator.start();
    }


    private void movePositionX(int toPosition) {
        // TODO Auto-generated method stub
        movePositionX(toPosition, 0);
//        if (!((SearchSites) fragmentList.get(toPosition)).isTabInit())
//            ((SearchSites) fragmentList.get(toPosition)).initTabOption();

    }


    @Override
    public Boolean onBackPressedAct()
    {
        return ((SearchSites) fragmentList.get(0)).onBackPressedAct();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.topbar_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.topbarSearch)
            ((SearchSites) fragmentList.get(mViewPager.getCurrentItem())).searchByClasses();

        return super.onOptionsItemSelected(item);
    }

}