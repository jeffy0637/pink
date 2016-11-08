package m.mcoupledate;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import m.mcoupledate.classes.NavigationActivity;
import m.mcoupledate.classes.customView.DateInputEditText;
import m.mcoupledate.funcs.PinkCon;

public class MemberData extends NavigationActivity
{
    private Context mContext;
    private RequestQueue mRequestQueue;

    private String id;
    private TextView username;
    private DateInputEditText birthday, relationshipDate;
    private ImageButton birthdayEditBtn, relationshipDateEditBtn;
    private Button submitBtn, cancelBtn;

    SQLiteDatabase db = null;

    private Calendar calendar;
    private DatePickerDialog birthdayPickerDialog, relationshipDatePickerDialog;
    private int[] birthdayArr, relationshipDateArr;

    private Boolean ifNewMember = false;



    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_data);

        id = this.getSharedPreferences("pinkpink", 0).getString("mId", null);

        username = (TextView) findViewById(R.id.username);
        birthday = (DateInputEditText) findViewById(R.id.birthday);
        relationshipDate = (DateInputEditText) findViewById(R.id.relationshipDate);


        calendar = Calendar.getInstance();
        birthdayArr = new int[]{calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.DAY_OF_MONTH)};
        relationshipDateArr = new int[]{calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH)+1), calendar.get(Calendar.DAY_OF_MONTH)};

        initData();


        birthdayPickerDialog = new DatePickerDialog(this,
                new OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        dialogSetDateInputText(birthday, year, monthOfYear, dayOfMonth);
                    }
                }
                , birthdayArr[0], (birthdayArr[1]-1), birthdayArr[2]
        );

        birthdayEditBtn = (ImageButton)findViewById(R.id.birthdayEditBtn);
        birthdayEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   birthdayPickerDialog.show();  }
        });



        relationshipDatePickerDialog = new DatePickerDialog(this,
                new OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        dialogSetDateInputText(relationshipDate, year, monthOfYear, dayOfMonth);
                    }
                }
                , relationshipDateArr[0], (relationshipDateArr[1]-1), relationshipDateArr[2]
        );

        relationshipDateEditBtn = (ImageButton)findViewById(R.id.relationshipDateEditBtn);
        relationshipDateEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {   relationshipDatePickerDialog.show();  }
        });


        submitBtn = (Button) findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (birthday.getValue()!=null && relationshipDate.getValue()!=null)
                    updateMemberData(id, username.getText().toString(), birthday.getValue(), relationshipDate.getValue());
                else
                    Toast.makeText(MemberData.this, "請正確填寫日期", Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        if (ifNewMember)
        {
            cancelBtn.setVisibility(View.GONE);
        }
        else
        {
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {   finish();   }
            });
        }




    }


    private void initData()
    {
        Intent intent = this.getIntent();
        if (intent.getStringExtra("newMemberName")!=null)
        {
            ifNewMember = true;

            username.setText(intent.getStringExtra("newMemberName"));

            String[] newMemberBirthday = intent.getStringExtra("newMemberBirthday").split("/");
            birthday.setText(newMemberBirthday[2] + "/" + newMemberBirthday[0] + "/" + newMemberBirthday[1]);

            birthdayArr = new int[]{Integer.valueOf(newMemberBirthday[2]), Integer.valueOf(newMemberBirthday[0]), Integer.valueOf(newMemberBirthday[1])};
        }
        else
        {
            //從SQLite取資料印在頁面上
            db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);//打開資料庫
            Cursor cursor = db.rawQuery("SELECT name, gender, birthday, relationship_date FROM member WHERE _id='"+id+"'", null);
            cursor.moveToFirst();
            do
            {
                username.setText(cursor.getString(0));

                if(cursor.getString(2) != null)
                {
                    //不為空值才設定起始年月日 => 生日

                    birthday.setText(cursor.getString(2).replace("-", "/"));

                    String[] birthdayTextArr = cursor.getString(2).split("-");
                    birthdayArr = new int[]{Integer.valueOf(birthdayTextArr[0]), Integer.valueOf(birthdayTextArr[1]), Integer.valueOf(birthdayTextArr[1])};
                }

                if(cursor.getString(3) != null)
                {
                    //不為空值才設定起始年月日 => 交往日

                    relationshipDate.setText(cursor.getString(3).replace("-", "/"));

                    String[] relationshipDateTextArr = cursor.getString(3).split("-");
                    relationshipDateArr = new int[]{Integer.valueOf(relationshipDateTextArr[0]), Integer.valueOf(relationshipDateTextArr[1]), Integer.valueOf(relationshipDateTextArr[2])};

                }

            }
            while(cursor.moveToNext());


            cursor.close();

            db.close();
        }
    }

    private void dialogSetDateInputText(DateInputEditText input, int year, int monthOfYear, int dayOfMonth)
    {
        String monthOfYearStr = String.valueOf(monthOfYear+1);
        String dayOfMonthStr = String.valueOf(dayOfMonth);

        if ((monthOfYear+1)<10)
            monthOfYearStr = "0" + monthOfYearStr;

        if (dayOfMonth<10)
            dayOfMonthStr = "0" + dayOfMonthStr;

        input.setText(year + "/" + monthOfYearStr + "/" + dayOfMonthStr);
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.member_data, menu);
//        return true;
//    }

//    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
//        @Override
//        public boolean onMenuItemClick(MenuItem menuItem) {
//            String msg = "";
//            switch (menuItem.getItemId()) {
//                case R.id.action_check:
//                    String nameStr = username.getText().toString();
//
//                    //設定初始值為空
//                    String yearB="";
//                    String monthB="";
//                    String dayB="";
//                    String yearM="";
//                    String monthM="";
//                    String dayM="";
//                    int tag=0;
//
//                    //如果為空 予空值  若非則提取值
//                    if("".equals(birth_year.getText().toString().trim())) {
//                        yearB = "";
//                        tag = 1;
//                    }
//                    else yearB = birth_year.getText().toString();
//
//                    if("".equals(birth_month.getText().toString().trim())) {
//                        monthB = "";
//                        tag = 2;
//                    }
//                    else monthB = birth_month.getText().toString();
//
//                    if("".equals(birth_day.getText().toString().trim())) {
//                        dayB = "";
//                        tag = 3;
//                    }
//                    else dayB = birth_day.getText().toString();
//
//                    if("".equals(relationship_year.getText().toString().trim())) {
//                        yearM ="";
//                        tag=4;
//                    }
//                    else yearM = relationship_year.getText().toString();
//
//                    if("".equals(relationship_month.getText().toString().trim())) {
//                        monthM ="";
//                        tag=5;
//                    }
//                    else monthM = relationship_month.getText().toString();
//
//                    if("".equals(relationship_day.getText().toString().trim())) {
//                        dayM ="";
//                        tag=6;
//                    }
//                    else dayM = relationship_day.getText().toString();
//
//
//                    //空值提醒
//                    if(tag==1) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
//                    else if(tag==2) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
//                    else if(tag==3) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
//                    else if(tag==4) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
//                    else if(tag==5) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
//                    else if(tag==6) Toast.makeText(MemberData.this, "請輸入您的生日", Toast.LENGTH_SHORT).show();
//
//                    else {
//                        //正確輸入範圍判 else {斷
//
//                        //測試用
//                        //Toast.makeText(MemberData.this, "YES", Toast.LENGTH_SHORT).show();
//
//                        int b_year = Integer.parseInt(yearB);
//                        int b_month = Integer.parseInt(monthB);
//                        int b_day = Integer.parseInt(dayB);
//                        int m_year = Integer.parseInt(yearM);
//                        int m_month = Integer.parseInt(monthM);
//                        int m_day = Integer.parseInt(dayM);
//
//                        if (b_year < 1900 || b_year > 2016 || yearB == "")
//                            Toast.makeText(MemberData.this, "請輸入正確生日年份(西元)", Toast.LENGTH_SHORT).show();
//                        else if (b_month > 12)
//                            Toast.makeText(MemberData.this, "請輸入正確生日月份", Toast.LENGTH_SHORT).show();
//                        else if (b_day > 31)
//                            Toast.makeText(MemberData.this, "請輸入正確生日日期", Toast.LENGTH_SHORT).show();
//
//                        else if (m_year < 1900 || m_year > 2016 || yearM == "")
//                            Toast.makeText(MemberData.this, "請輸入正確紀念日年份(西元)", Toast.LENGTH_SHORT).show();
//                        else if (m_month > 12)
//                            Toast.makeText(MemberData.this, "請輸入正確紀念日月份", Toast.LENGTH_SHORT).show();
//                        else if (m_day > 31)
//                            Toast.makeText(MemberData.this, "請輸入正確紀念日日期", Toast.LENGTH_SHORT).show();
//
//                        else {
//                            String b_yearS = Integer.toString(b_year);
//                            String b_monthS = Integer.toString(b_month);
//                            String b_dayS = Integer.toString(b_day);
//                            String m_yearS = Integer.toString(m_year);
//                            String m_monthS = Integer.toString(m_month);
//                            String m_dayS = Integer.toString(m_day);
//
//                            String birthdayStr = "";
//                            if(b_month < 10 && b_day< 10)
//                                birthdayStr = b_yearS + "-0" + b_monthS + "-0" +b_dayS;
//                            else if(b_month < 10 && b_day >= 10)
//                                birthdayStr =b_yearS + "-0" + b_monthS + "-" + b_dayS;
//                            else if(b_month >= 10 && b_day < 10)
//                                birthdayStr = b_yearS + "-" + b_monthS + "-0" + b_dayS;
//                            else
//                                birthdayStr = b_yearS + "-" + b_monthS + "-" + b_dayS;
//
//                            //彈出是視窗顯示birthdayStr
//                            Toast.makeText(MemberData.this, "生日"+ birthdayStr, Toast.LENGTH_SHORT).show();
//
//                            String relationshipstr = "";
//                            if(m_month < 10 && m_day < 10)
//                                relationshipstr = m_yearS + "-0" + m_monthS.toString() + "-0" + m_dayS;
//                            else if(m_month < 10 && m_day >= 10)
//                                relationshipstr = m_yearS + "-0" + m_monthS + "-" + m_dayS;
//                            else if(m_month >= 10 && m_day < 10)
//                                relationshipstr = m_yearS + "-" + m_monthS + "-0" + m_dayS;
//                            else
//                                relationshipstr =m_yearS + "-" + m_monthS + "-" + m_dayS;
//
//                            //彈出是視窗顯示relationshipstr
//                            Toast.makeText(MemberData.this, "紀念日"+ relationshipstr, Toast.LENGTH_SHORT).show();
//
//                            //傳資料給SQLite MariaDB
//                            int g=0;
//                            db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);//打開SQLite資料庫
//                            db.execSQL("UPDATE member SET name = '"+nameStr+"', gender = '"+g+"', birthday = '"+birthdayStr+"', relationship_date = '"+relationshipstr+"' WHERE _id = '"+id+"'");
//                            db.close();
//                            insertIntoMariaDB(nameStr,g , birthdayStr, relationshipstr);//MariaDB
//                            //跳回首頁
//                            Intent intent = new Intent(MemberData.this, HomePageActivity.class);
//                            startActivity(intent);
//                        }
//                    }
//            }
//
//            if(!msg.equals("")) {
//                Toast.makeText(MemberData.this, msg, Toast.LENGTH_SHORT).show();
//            }
//            return true;
//        }
//    };



    /**
     * 將修改的資料放入MariaDB
     */
    private void updateMemberData(final String id, final String userName, final String birthday, final String relationship)
    {
        mRequestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL + "memberData_updateData.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        updateSQLiteDB(id, userName, birthday, relationship);
                        Toast.makeText(MemberData.this, "修改成功", Toast.LENGTH_SHORT).show();

                        if (ifNewMember)
                            startActivity(new Intent(MemberData.this, HomePageActivity.class));

                        MemberData.this.finish();


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("mId", id);
                map.put("name", userName);
//                map.put("Gender", String.valueOf(gender));
                map.put("Birthday", birthday);
                map.put("Relationship", relationship);
                return map;
            }
        };
        mRequestQueue.add(stringRequest);
    }


    private void updateSQLiteDB(String id, String userName, String birthday, String relationshipDate)
    {
        //傳資料給SQLite MariaDB
        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);//打開SQLite資料庫

        if (relationshipDate.compareTo("''")!=0)
            db.execSQL("UPDATE member SET name='"+userName+"',  birthday = '"+birthday+"', relationship_date = '"+relationshipDate+"' WHERE _id = '"+id+"'");
        else
            db.execSQL("UPDATE member SET name='"+userName+"', birthday = '"+birthday+"', relationship_date=null WHERE _id = '"+id+"'");

        db.close();
    }


}


//第一次登入從直接跳來這裡
