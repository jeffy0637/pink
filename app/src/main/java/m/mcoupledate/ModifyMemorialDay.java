package m.mcoupledate;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import m.mcoupledate.classes.NavigationActivity;
import m.mcoupledate.classes.adapters.MemorialDaysModifierAdapter;
import m.mcoupledate.classes.adapters.MemorialDaysModifierAdapter.MemorialDay;
import m.mcoupledate.funcs.Actioner;
import m.mcoupledate.funcs.PinkCon;


public class ModifyMemorialDay extends NavigationActivity {

    private SQLiteDatabase db = null;

    private RequestQueue mQueue;

    private String mId;

    private ListView memorialDaysListView;
    private MemorialDaysModifierAdapter memorialDaysModifierAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_memorial_day);

        mQueue = Volley.newRequestQueue(this);

        mId = this.getSharedPreferences("pinkpink", 0).getString("mId", null);
        memorialDaysListView = (ListView) findViewById(R.id.memorialDaysListView);

        loadMemorialDays();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.topbar_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.topbarAdd && memorialDaysModifierAdapter!=null)
            memorialDaysModifierAdapter.addByUser();

        return super.onOptionsItemSelected(item);
    }


    private void loadMemorialDays()
    {
        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);

        Cursor cursor = db.rawQuery("SELECT * FROM memorialday WHERE _id='"+mId+"' ORDER BY eventDate ASC",null);


        ArrayList<MemorialDay> memorialDaysList = new ArrayList<MemorialDay>();
        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();

            do
            {
                memorialDaysList.add(new MemorialDay(cursor.getString(1), cursor.getString(2)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();


        Actioner memorialDayUpdater = new Actioner()
        {
            @Override
            public void act(Object... args)
            {   updateMemorialDay(args);   }
        };

        memorialDaysModifierAdapter = new MemorialDaysModifierAdapter(this, memorialDaysList, memorialDayUpdater);
        memorialDaysListView.setAdapter(memorialDaysModifierAdapter);

    }

    private void updateMemorialDay(final Object[] args)
    {
        final int updateType = (int) args[0];
        final String eventName = (String) args[1];
        final String eventDate = (String) args[2];

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL + "modifyMemorialDay_update.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(ModifyMemorialDay.this, "修改完成", Toast.LENGTH_SHORT).show();
                        updateSQLiteDB(args);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        PinkCon.retryConnect(getRootView(), PinkCon.SUBMIT_FAIL, null, "HFSUBMITERR", error.getMessage(),
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {   updateMemorialDay(args);  }
                            });
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("updateType", String.valueOf(updateType));
                map.put("mId", mId);
                map.put("eventName", eventName);
                map.put("eventDate", eventDate);

                if (updateType==MemorialDaysModifierAdapter.UPDATEMEMORIALDAYTYPE_UPDATE)
                {
                    map.put("originName", (String) args[3]);
                    map.put("originDate", (String) args[4]);
                }

                return map;
            }
        };
        mQueue.add(stringRequest);
    }


    private void updateSQLiteDB(Object[] args)
    {
        int updateType = (int) args[0];
        String eventName = (String) args[1];
        String eventDate = (String) args[2];

        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);//打開SQLite資料庫

        switch (updateType)
        {
            case MemorialDaysModifierAdapter.UPDATEMEMORIALDAYTYPE_NEW:
                db.execSQL("INSERT INTO `memorialday` VALUES ('"+mId+"', '"+eventName+"', '"+eventDate+"')");
                break;

            case MemorialDaysModifierAdapter.UPDATEMEMORIALDAYTYPE_UPDATE:
                String originName = (String) args[3];
                String originDate = (String) args[4];
                db.execSQL("UPDATE `memorialday` SET eventName='"+eventName+"', eventDate='"+eventDate+"' WHERE _id='"+mId+"' AND eventName='"+originName+"' AND eventDate='"+originDate+"'");
                break;

            case MemorialDaysModifierAdapter.UPDATEMEMORIALDAYTYPE_DELETE:
                db.execSQL("DELETE FROM `memorialday` WHERE _id='"+mId+"' AND eventName='"+eventName+"' AND eventDate='"+eventDate+"'");
                break;
        }
        db.close();
    }



//    //印出紀念日們(新版)
//    public void printMemorialDays() {
//        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);
//        Cursor cursor = db.rawQuery("SELECT * FROM memorialday WHERE _id='"+mId+"'",null);
//        cursor.moveToFirst();
//        do{
//            if(cursor.getCount() != 0){
////                int id = Integer.parseInt(cursor.getString(0));
//                int id = Integer.parseInt("1234");
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
//                    init(id,name, theDay, Math.abs(Integer.valueOf(diffstr)));
//                }
//                else if(Integer.valueOf(theDay.substring(5, 7)) == Integer.valueOf(today.substring(5, 7)) && Integer.valueOf(theDay.substring(8, 10)) >= Integer.valueOf(today.substring(8, 10))){//月等於 比日
//                    long diff = d2.getTime() - d1.getTime();
//                    String diffstr = "" + diff/(1000*60*60*24);
//                    init(id,name, theDay, Math.abs(Integer.valueOf(diffstr)));
//                }
//                else if(Integer.valueOf(theDay.substring(5, 7)) < Integer.valueOf(today.substring(5, 7))){//月小於  年+1
//                    try{
//                        String d1_str = (Integer.valueOf(today.substring(0, 4))+1) + "-" + theDay.substring(5, 7) + "-" + theDay.substring(8, 10);
//                        d1 = dateFormatter.parse(d1_str);
//                    }
//                    catch (ParseException e){
//                        e.printStackTrace();
//                    }
//                    long diff = d2.getTime() - d1.getTime();
//                    String diffstr = "" + diff/(1000*60*60*24);
//                    init(id,name, theDay, Math.abs(Integer.valueOf(diffstr)));
//                }
//                else if(Integer.valueOf(theDay.substring(5, 7)) == Integer.valueOf(today.substring(5, 7)) && Integer.valueOf(theDay.substring(8, 10)) < Integer.valueOf(today.substring(8, 10))){//月等於 日小於  年+1
//                    try{
//                        String d1_str = (Integer.valueOf(today.substring(0, 4))+1) + "-" + theDay.substring(5, 7) + "-" + theDay.substring(8, 10);
//                        d1 = dateFormatter.parse(d1_str);
//                    }
//                    catch (ParseException e){
//                        e.printStackTrace();
//                    }
//                    long diff = d2.getTime() - d1.getTime();
//                    String diffstr = "" + diff/(1000*60*60*24);
//                    init(id,name, theDay, Math.abs(Integer.valueOf(diffstr)));
//                }
//            }
//        }while(cursor.moveToNext());
//    }
//
//    //新增單筆紀念日資料
//    public void init(int id, final String name, String date, int diff)
//    {
//        String diffDay = Integer.toString(diff);
//        LinearLayout linearLayout1=(LinearLayout)findViewById(R.id.activity_service_select);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        View view =LayoutInflater.from(this).inflate(R.layout.my_mday, null);
//        view.setLayoutParams(lp);
//        TextView tv1 = (TextView) view.findViewById(R.id.m_context);
//        TextView tv2 = (TextView) view.findViewById(R.id.m_day);
//        TextView tv3 = (TextView) view.findViewById(R.id.m_diffTime);
//        View edit = view.findViewById(R.id.edit);
//        View delete = view.findViewById(R.id.delete);
//        tv1.setText(name);
//        tv2.setText(date);
//        tv3.setText(diffDay);
//        edit.setId(id);
//        delete.setId(id);
//
//        final int editID = edit.getId();
//        int deleteID = edit.getId();
//
//        linearLayout1.addView(view);
//
//        edit.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//                // TODO Auto-generated method stub
//                new AlertDialog.Builder(ModifyMemorialDay.this).setTitle(String.valueOf(editID))
//                        .setTitle("紀念日名稱")
//                        .setView(new EditText(ModifyMemorialDay.this))
//                        .setPositiveButton("确定", null).setNegativeButton("取消", null).show();
//            }
//        });
//
//        delete.setOnClickListener(new Button.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//
//                // TODO Auto-generated method stub
//                new AlertDialog.Builder(ModifyMemorialDay.this)
//                        .setTitle("你確定要刪除:"+name)
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(ModifyMemorialDay.this, "已刪除", Toast.LENGTH_LONG)
//                                    .show();
//                            }
//                        })
//                        .setNegativeButton("取消", null)
//                        .show();
//
//
//            }
//        });
//    }


}
/*改進
原本是紀念日的藥不同顏色
輕按標示原本紀念日
久按修改紀念日

把自己生日也顯示

*/
/*待做
印出原本是紀念日的日子為其他顏色
從sqlite撈資料印出
存入sqlite
sqlite與資料庫的同步
*/
