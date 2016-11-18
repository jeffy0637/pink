package m.mcoupledate;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import m.mcoupledate.classes.NavigationActivity;
import m.mcoupledate.classes.adapters.PinkFragmentPagerAdapter;

public class SiteSearchActivity extends NavigationActivity implements
        ViewPager.OnPageChangeListener
{

    private int initSearchType;

    private ViewPager mViewPager;
    private List fragmentList;
    private boolean isScrolling = false; // 手指是否在滑动
    private boolean isBackScrolling = false; // 手指离开后的回弹
    private long startTime = 0;
    private long currentTime = 0;

    private TextView tabAttraction, tabRestaurant;
    private ImageView tabUnderline; // tab选项卡的下划线
    private int tabSelectedColor, tabUnselectedColor;

    private int moveOne = 0; // 下划线移动一个选项卡
    private BottomBar mBottomBar;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_search);

        initView();

        if (initSearchType==SearchSites.SEARCHTYPE_BROWSE)
        {
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
                    Intent go2 = new Intent(SiteSearchActivity.this, HomePageActivity.class);
                    startActivity(go2);

                }
            });

            // 当点击不同按钮的时候，设置不同的颜色
            // 可以用以下三种方式来设置颜色.
            mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
            mBottomBar.mapColorForTab(1, 0xFF5D4037);
            mBottomBar.mapColorForTab(2, "#7B1FA2");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //保存BottomBar的状态
        if (initSearchType==SearchSites.SEARCHTYPE_BROWSE)
            mBottomBar.onSaveInstanceState(outState);
    }


    public void initView()
    {
        mViewPager = (ViewPager) findViewById(R.id.myViewPager);

        if (this.getIntent().getIntExtra("searchType", -1)==SearchSites.SEARCHTYPE_MYLIKES)
            initSearchType = SearchSites.SEARCHTYPE_MYLIKES;
        else
            initSearchType = SearchSites.SEARCHTYPE_BROWSE;

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(SearchSites.newInstance(initSearchType, SearchSites.SITETYPE_ATTRACTION, true));
        fragmentList.add(SearchSites.newInstance(initSearchType, SearchSites.SITETYPE_RESTAURANT, false));


        PinkFragmentPagerAdapter pinkFragmentPagerAdapter = new PinkFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(pinkFragmentPagerAdapter);

        tabSelectedColor = ContextCompat.getColor(this, R.color.pinkpink);
        tabUnselectedColor = ContextCompat.getColor(this, R.color.white);

        String[] tabNames = {"景點", "餐廳"};
        HashMap tabWidgets = setTabs(tabNames);
        tabAttraction = ((ArrayList<TextView>)tabWidgets.get("tabs")).get(0);
        tabRestaurant = ((ArrayList<TextView>)tabWidgets.get("tabs")).get(1);
        tabAttraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   mViewPager.setCurrentItem(0);   }
        });
        tabRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   mViewPager.setCurrentItem(1);   }
        });

        tabUnderline = (ImageView) tabWidgets.get("tabUnderline");

        if (this.getIntent().getIntExtra("siteType", -1)==SearchSites.SITETYPE_RESTAURANT)
        {
            mViewPager.setCurrentItem(1);
            tabRestaurant.setTextColor(tabSelectedColor);
            movePositionX(1, 0);
        }
        else
        {
            mViewPager.setCurrentItem(0);
            tabAttraction.setTextColor(tabSelectedColor);
        }

        mViewPager.setOnPageChangeListener(this);
    }


    public HashMap<String, Object> setTabs(String[] tabNames)
    {
        LinearLayout tabsContainer = new LinearLayout(this);
        tabsContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 70));

        ArrayList<TextView> tabList = new ArrayList<>();
        LinearLayout.LayoutParams tabParam = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        int tabTextDefaultColor = ContextCompat.getColor(this, R.color.white);

        for (String tabName : tabNames)
        {
            TextView aTab = new TextView(this);
            aTab.setLayoutParams(tabParam);
            aTab.setText(tabName);
            aTab.setTextSize(16);
            aTab.setTextColor(tabTextDefaultColor);
            aTab.setGravity(Gravity.CENTER);

            tabList.add(aTab);
            tabsContainer.addView(aTab);
        }

        ImageView tabUnderline = new ImageView(this);
        tabUnderline.setBackgroundColor(ContextCompat.getColor(this, R.color.pinkpink));
        initLineImage(tabUnderline);


        AppBarLayout appBarLayout = getAppBarLayout();
        appBarLayout.addView(tabsContainer);
        appBarLayout.addView(tabUnderline);


        HashMap<String, Object> tabWidgets = new HashMap<>();
        tabWidgets.put("tabs", tabList);
        tabWidgets.put("tabUnderline", tabUnderline);

        return tabWidgets;
    }

    private void initLineImage(ImageView tabUnderline)
    {
        // TODO Auto-generated method stub
        /** * 获取屏幕的宽度 */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        /** * 重新设置下划线的宽度 */;
        int lineWidth = (screenW / 2);
        tabUnderline.setLayoutParams(new ViewGroup.LayoutParams(lineWidth, 5));
        moveOne = lineWidth;
        // 滑动一个页面的距离
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
                tabAttraction.setTextColor(tabSelectedColor);
                tabRestaurant.setTextColor(tabUnselectedColor);
                movePositionX(0);
                break;
            case 1:
                tabAttraction.setTextColor(tabUnselectedColor);
                tabRestaurant.setTextColor(tabSelectedColor);
                movePositionX(1);
                break;
            default:
                break; }
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

        switch (state)
        {
            case ViewPager.SCROLL_STATE_DRAGGING:
                isScrolling = true;
                isBackScrolling = false;
                ((SearchSites) fragmentList.get(mViewPager.getCurrentItem())).onScrollOut();
                break;

            case ViewPager.SCROLL_STATE_IDLE:
            case ViewPager.SCROLL_STATE_SETTLING:
                isScrolling = false;
                isBackScrolling = true;
                ((SearchSites) fragmentList.get(mViewPager.getCurrentItem())).onScrolledIn();
                if (!searchView.isIconified())
                    passSearchQuery(searchView.getQuery().toString());
                break;

            default:
                isScrolling = false;
                isBackScrolling = false;
                break;
        }
    }

    private void movePositionX(int toPosition, float positionOffsetPixels) {
        // TODO Auto-generated method stub
        float curTranslationX = tabUnderline.getTranslationX();
        float toPositionX = moveOne * toPosition + positionOffsetPixels;
        ObjectAnimator animator = ObjectAnimator.ofFloat(tabUnderline, "translationX", curTranslationX, toPositionX);
        animator.setDuration(250).start();

//            ((SearchSites) fragmentList.get(mViewPager.getCurrentItem())).searchByText(searchView.getQuery().toString());
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

        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.topbarSearch));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                onQueryTextChange(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
                passSearchQuery(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose()
            {
                ((SearchSites) fragmentList.get(mViewPager.getCurrentItem())).setInitSiteList();
                return false;
            }
        });

        return true;
    }

    private void passSearchQuery(String query)
    {
        ((SearchSites) fragmentList.get(mViewPager.getCurrentItem())).searchByText(query);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        if (item.getItemId() == R.id.topbarSearch)
//            ((SearchSites) fragmentList.get(mViewPager.getCurrentItem())).searchByClasses();
//
//        return super.onOptionsItemSelected(item);
//    }

}