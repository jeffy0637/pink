package m.mcoupledate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import m.mcoupledate.classes.DropDownMenu.DropDownMenu;

public class SiteAttractionActivity extends AppCompatActivity {

    private String headers[] = {"行政區", "愛心指數"};

    DropDownMenu mDropDownMenu;

    public static String select_area = "";
    public static String select_love = "";

    // private m.mcoupledate.ListDropDownAdapter cityAdapter;


    private int[] types = new int[]{ DropDownMenu.TYPE_GRID , DropDownMenu.TYPE_LIST_CITY, DropDownMenu.TYPE_LIST_SIMPLE ,  DropDownMenu.TYPE_CUSTOM};
    private String area[] = {"不限", "楠梓區", "左營區", "鼓山區", "三民區", "鹽埕區", "前金區", "新興區", "苓雅區", "前鎮區", "旗津區", "小港區", "鳳山區", "大寮區", "鳥松區", "林園區", "仁武區", "大樹區", "大社區", "岡山區"
            , "路竹區", "橋頭區", "梓官區", "彌陀區", "永安區", "燕巢區", "田寮區", "阿蓮區", "茄萣區", "湖內區", "旗山區", "美濃區", "內門區", "杉林區", "甲仙區", "六龜區", "茂林區", "桃源區", "那瑪夏區"};
    private String love[] = {"不限", "未滿1心", "1心以上", "2心以上", "3心以上", "4心以上","5心"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_attraction);

        mDropDownMenu= (DropDownMenu) findViewById( R.id.dropDownMenu);
        initView();
    }

    private void initView() {
        View contentView = getLayoutInflater().inflate(R.layout.dropdownmenu_contentview, null);
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), initViewData(), contentView);
        init();
        //该监听回调只监听默认类型，如果是自定义view请自行设置，参照demo
        mDropDownMenu.addMenuSelectListener(new DropDownMenu.OnDefultMenuSelectListener() {
            @Override
            public void onSelectDefaultMenu(int index, int pos, String clickstr) {
                String end = null;
                //index:点击的tab索引，pos：单项菜单中点击的位置索引，clickstr：点击位置的字符串
                switch( index )  /*status 只能為整數、長整數或字元變數.*/
                {
                    case 0:
                        select_area = clickstr;
                        break;
                    case 1:
                        select_love = clickstr;
                        break;
                }
                Toast.makeText(getBaseContext(), clickstr, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //設置篩選選單
    private List<HashMap<String, Object>> initViewData() {
        List<HashMap<String, Object>> viewDatas = new ArrayList<>();
        HashMap<String, Object> map1,map2;
        map1 = new HashMap<String, Object>();
        map2 = new HashMap<String, Object>();

        map1.put(DropDownMenu.KEY, DropDownMenu.TYPE_GRID);
        map1.put(DropDownMenu.VALUE, area);
        viewDatas.add(map1);

        map2.put(DropDownMenu.KEY, DropDownMenu.TYPE_LIST_SIMPLE);
        map2.put(DropDownMenu.VALUE, love);
        viewDatas.add(map2);

        return viewDatas;
    }

    //動態抓取
    public void init()
    {
        String name = "HAHA";
        String date = "LOC";
        LinearLayout linearLayout1=(LinearLayout)findViewById(R.id.site_info);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.attraction_init, null);
        view.setLayoutParams(lp);
//圖片
//      ImageView imageView1 = (ImageView) findViewById( R.id.attraction_image);

        TextView tv1 = (TextView) view.findViewById(R.id.title);
        TextView tv2 = (TextView) view.findViewById(R.id.location);
        tv1.setText(name);
        tv2.setText(date);
//RatingBar抓分數
//        float val = Float.parseFloat("4");
//        RatingBar rb = (RatingBar)findViewById(R.id.ratingBar);
//        rb.setRating(val);

        linearLayout1.addView(view);
    }

    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    //設置搜尋按鈕
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.topbar_search, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
            }

            if(!msg.equals("")) {
                Toast.makeText(SiteAttractionActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };
}
