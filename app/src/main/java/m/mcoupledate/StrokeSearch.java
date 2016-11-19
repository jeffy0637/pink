package m.mcoupledate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;
import java.util.List;

import m.mcoupledate.classes.NavigationActivity;

public class StrokeSearch extends NavigationActivity {

    private BottomBar mBottomBar;
    //定义数据
    private List<Stroke> mData;
    //定义ListView对象
    private ListView mListViewArray;

    Intent intent;
    String tripType;
    private SearchView searchView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stroke_search);

        //为ListView对象赋值
        mListViewArray = (ListView) findViewById(R.id.list);
        LayoutInflater inflater =getLayoutInflater();
        //初始化数据
        initData();
        //创建自定义Adapter的对象
        MyAdapter adapter = new MyAdapter(inflater,mData);
        //将布局添加到ListView中
        mListViewArray.setAdapter(adapter);
        mListViewArray.setOnItemClickListener(listener);

        intent = this.getIntent();
        tripType = intent.getStringExtra("trip");

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

        //initView();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }

    /*
    初始化数据
     */
    private void initData() {
        mData = new ArrayList<Stroke>();
        Stroke zhangsan  = new Stroke("哈哈之旅", "2016-06-23", "" );
        mData.add(zhangsan);
    }

    private ListView.OnItemClickListener listener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(StrokeSearch.this, StrokeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("tripId", position);
            bundle.putString("tripType", tripType);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

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
                return false;
            }
        });

        return true;
    }

    private void passSearchQuery(String query)
    {
        //
    }
}