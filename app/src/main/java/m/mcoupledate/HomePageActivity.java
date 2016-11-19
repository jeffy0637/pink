package m.mcoupledate;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import m.mcoupledate.classes.NavigationActivity;
import m.mcoupledate.classes.adapters.MemorialDaysAdapter;
import m.mcoupledate.classes.adapters.MemorialDaysAdapter.MemorialDay;
import m.mcoupledate.classes.customView.ResponsiveListView;


public class HomePageActivity extends NavigationActivity {

    private String mId;
    private SQLiteDatabase db = null;


    private TextView totalDays = null;
    private ResponsiveListView memorialDaysListView = null;
    private MemorialDaysAdapter memorialDaysAdapter = null;


    private BottomBar mBottomBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mId = this.getSharedPreferences("pinkpink", 0).getString("mId", null);

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
                        Intent go3 = new Intent(HomePageActivity.this, StrokeSearch.class);
                        startActivity(go3);
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                //重选事件，当前已经选择了这个，又点了这个tab。
                //Intent go2 = new Intent(HomePageActivity.this, SiteSearchActivity.class);
                //startActivity(go2);
            }
        });

        // 当点击不同按钮的时候，设置不同的颜色
        // 可以用以下三种方式来设置颜色.
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(1, 0xFF5D4037);
        mBottomBar.mapColorForTab(2, "#7B1FA2");




//        setTotalDays();
//        loadMemorialDays();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //保存BottomBar的状态
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (totalDays==null)
            totalDays = (TextView) findViewById(R.id.totalDays);
        if (memorialDaysListView==null)
            memorialDaysListView = (ResponsiveListView) findViewById(R.id.memorialDaysListView);

        setTotalDays();
        loadMemorialDays();
    }


    //算總共交往多久(新版)
    public void setTotalDays()
    {
        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);

        Cursor relationshipCursor = db.rawQuery("SELECT relationship_date FROM member WHERE _id='"+mId+"'",null);

        if(relationshipCursor.getCount() != 0)
        {
            relationshipCursor.moveToFirst();

            String theDay = relationshipCursor.getString(0);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = null;
            try{
                d1 = formatter.parse(theDay);
            }
            catch (ParseException e){
                e.printStackTrace();
            }
            Date d2 = new Date();
            long diff = (d2.getTime() - d1.getTime()) / (1000*60*60*24);

            if (diff<0)
                diff = 0;

            totalDays.setText(String.valueOf(diff));
        }

        db.close();
    }

    private void loadMemorialDays()
    {
        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Cursor cursor = db.rawQuery("SELECT * FROM memorialday WHERE _id='"+mId+"' ORDER BY eventDate ASC",null);


        ArrayList<MemorialDay> memorialDaysList = new ArrayList<MemorialDay>();
        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();

            do
            {
                memorialDaysList.add(new MemorialDay(cursor.getString(1), cursor.getString(2), formatter, calendar));
            }
            while (cursor.moveToNext());
        }
//        memorialDaysList.add(new MemorialDay("21", "2016-12-21"));
//        memorialDaysList.add(new MemorialDay("22", "2016-12-22"));
//        memorialDaysList.add(new MemorialDay("23", "2016-12-23"));
//        memorialDaysList.add(new MemorialDay("24", "2016-12-24"));
//        memorialDaysList.add(new MemorialDay("25", "2016-12-25"));
//        memorialDaysList.add(new MemorialDay("26", "2016-12-26"));

        if (memorialDaysAdapter==null)
        {
            memorialDaysAdapter = new MemorialDaysAdapter(this, memorialDaysList);
            memorialDaysListView.setAdapter(memorialDaysAdapter);
        }
        else
        {
            memorialDaysAdapter.changeData(memorialDaysList);
        }


    }



    //印出紀念日們(新版)
//    public void printMemorialDays()
//    {
//        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);
//        Cursor cursor = db.rawQuery("select * from memorialday WHERE _id='"+mId+"' ORDER BY eventDate ASC",null);
//        cursor.moveToFirst();
//        do{
//            if(cursor.getCount() != 0){
//                String name = cursor.getString(1);
//                String theDay = cursor.getString(2);
//                DateFormat stringFormatter = new SimpleDateFormat("yyyy-MM-dd");//要轉成String的
//                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");//要轉成Date的
//
//                Date now = Calendar.getInstance().getTime();//取得現在時間
//                String today = stringFormatter.format(now);//將取得的時間轉成String
//
//                //String to Date 紀念日時間
//                Date d1 = null;
//                try{
//                    String d1_str = today.substring(0, 4) + "-" + theDay.substring(5, 7) + "-" + theDay.substring(8, 10);
//                    d1 = dateFormatter.parse(d1_str);
//                }
//                catch (ParseException e){
//                    e.printStackTrace();
//                }
//                //String to Date 現在時間
//                Date d2 = null;
//                try{
//                    d2 = dateFormatter.parse(today);
//                }
//                catch (ParseException e){
//                    e.printStackTrace();
//                }
//                //把過的忽略
//                if(Integer.valueOf(theDay.substring(5, 7)) > Integer.valueOf(today.substring(5, 7))){//月大於 日就不用比了
//                    long diff = d2.getTime() - d1.getTime();
//                    String diffstr = "" + diff/(1000*60*60*24);
//                    init(name, theDay, Math.abs(Integer.valueOf(diffstr)));
//                }
//                else if(Integer.valueOf(theDay.substring(5, 7)) == Integer.valueOf(today.substring(5, 7)) && Integer.valueOf(theDay.substring(8, 10)) >= Integer.valueOf(today.substring(8, 10))){//月等於 比日
//                    long diff = d2.getTime() - d1.getTime();
//                    String diffstr = "" + diff/(1000*60*60*24);
//                    init(name, theDay, Math.abs(Integer.valueOf(diffstr)));
//                }
//                else if(Integer.valueOf(theDay.substring(5, 7)) < Integer.valueOf(today.substring(5, 7))){//月小於  年+1
//                    try{
//                        String d1_str = (Integer.valueOf(today.substring(0, 4)) + 1) + "-" + theDay.substring(5, 7) + "-" + theDay.substring(8, 10);
//                        d1 = dateFormatter.parse(d1_str);
//                    }
//                    catch (ParseException e){
//                        e.printStackTrace();
//                    }
//                    long diff = d2.getTime() - d1.getTime();
//                    String diffstr = "" + diff/(1000*60*60*24);
//                    init(name, theDay, Math.abs(Integer.valueOf(diffstr)));
//                }
//                else if(Integer.valueOf(theDay.substring(5, 7)) == Integer.valueOf(today.substring(5, 7)) && Integer.valueOf(theDay.substring(8, 10)) < Integer.valueOf(today.substring(8, 10))){//月等於 日小於  年+1
//                    try{
//                        String d1_str = (Integer.valueOf(today.substring(0, 4)) + 1) + "-" + theDay.substring(5, 7) + "-" + theDay.substring(8, 10);
//                        d1 = dateFormatter.parse(d1_str);
//                    }
//                    catch (ParseException e){
//                        e.printStackTrace();
//                    }
//                    long diff = d2.getTime() - d1.getTime();
//                    String diffstr = "" + diff/(1000*60*60*24);
//                    init(name, theDay, Math.abs(Integer.valueOf(diffstr)));
//                }
//            }
//        }while(cursor.moveToNext());
//    }

    //新增單筆紀念日資料
//    public void init(String name, String date, int diff)
//    {
//        String diffDay = Integer.toString(diff);
//        LinearLayout linearLayout1=(LinearLayout)findViewById(R.id.activity_service_select);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        View view = LayoutInflater.from(this).inflate(R.layout.item_memorialday, null);
//        view.setLayoutParams(lp);
//        TextView tv1 = (TextView) view.findViewById(R.id.mContext);
//        TextView tv2 = (TextView) view.findViewById(R.id.mTime);
//        TextView tv3 = (TextView) view.findViewById(R.id.diffTime);
//        tv1.setText(name);
//        tv2.setText(date);
//        tv3.setText(diffDay);
//        linearLayout1.addView(view);
//    }


}
