package m.mcoupledate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import m.mcoupledate.classes.NavigationActivity;

public class MyTrip extends NavigationActivity {

    //定义数据
    private List<Stroke> mData;
    //定义ListView对象
    private ListView mListViewArray;
    private MyAdapter adapter;
    private Dialog addTrip;
    private View addTripView;
    Button startDateEditBtn,endDateEditBtn;
    Intent intent;
    String tripType;
    private int yearS, monthOfYearS, dayOfMonthS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip);

        Calendar calendar = Calendar.getInstance();
        yearS = calendar.get(Calendar.YEAR);
        monthOfYearS = calendar.get(Calendar.MONTH);
        dayOfMonthS = calendar.get(Calendar.DAY_OF_MONTH);

        //为ListView对象赋值
        mListViewArray = (ListView) findViewById(R.id.list);
        LayoutInflater inflater =getLayoutInflater();
        //初始化数据
        initData();
        //创建自定义Adapter的对象
        adapter = new MyAdapter(inflater,mData);
        //将布局添加到ListView中
        mListViewArray.setAdapter(adapter);
        mListViewArray.setOnItemClickListener(listener);

        intent = this.getIntent();
        tripType = intent.getStringExtra("trip");

    }

    /*
    初始化数据
     */
    private void initData() {
        mData = new ArrayList<Stroke>();
        Stroke travel1  = new Stroke("哈哈之旅", "2016-06-23", "","12345" );
        mData.add(travel1);
    }

    private ListView.OnItemClickListener listener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(MyTrip.this, StrokeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("tripId", adapter.getItem(position).tId);
            bundle.putString("tripType", tripType);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mytrip, menu);//这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_travel:
                LayoutInflater inflater = LayoutInflater.from(MyTrip.this);
                final View v = inflater.inflate(R.layout.dialog_add_trip, null);
                //語法一：new AlertDialog.Builder
                new AlertDialog.Builder(MyTrip.this)
                        .setTitle("新增行程")
                        .setView(v)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String travelName,startDate,endDate;
                                EditText editText = (EditText) (v.findViewById(R.id.name));
                                travelName = editText.getText().toString();
                                startDate = yearS+"-"+monthOfYearS+"-"+dayOfMonthS;
                                Stroke travel  = new Stroke(travelName, startDate,"","12345" );
                                mData.add(travel);
                                Toast.makeText(getApplicationContext(),travelName+startDate , Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                startDateEditBtn = (Button) v.findViewById(R.id.startDateEditBtn);
                endDateEditBtn = (Button) v.findViewById(R.id.endDateEditBtn);
                startDateEditBtn.setOnClickListener(new Button.OnClickListener(){
                    public void onClick(View v) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(MyTrip.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
                                yearS = year;
                                monthOfYearS = monthOfYear+1;
                                dayOfMonthS= dayOfMonth;
                                startDateEditBtn.setText(yearS+"-"+monthOfYearS+"-"+dayOfMonthS);
                            }
                        }, yearS, monthOfYearS, dayOfMonthS);
                        datePickerDialog.show();
                    }});

                return true;
            case R.id.remove_travel:
                new android.support.v7.app.AlertDialog.Builder(MyTrip.this)
                        .setTitle("刪除行程")
                        .setMessage("長按行程即可刪除")
                        .setPositiveButton("知道了",null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
