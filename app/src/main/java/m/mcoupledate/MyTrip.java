package m.mcoupledate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import m.mcoupledate.classes.NavigationActivity;

public class MyTrip extends NavigationActivity {

    //定义数据
    private List<Stroke> mData;
    //定义ListView对象
    private ListView mListViewArray;

    Intent intent;
    String tripType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip);

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

    }

    /*
    初始化数据
     */
    private void initData() {
        mData = new ArrayList<Stroke>();
        Stroke travel1  = new Stroke("哈哈之旅", "2016-06-23", "" );
        mData.add(travel1);
    }

    private ListView.OnItemClickListener listener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(MyTrip.this, StrokeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("tripId", position);
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
                final View item1 = LayoutInflater.from(MyTrip.this).inflate(R.layout.add_travel, null);
                new AlertDialog.Builder(MyTrip.this)
                        .setTitle("請輸入行程名稱")
                        .setView(item1)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) item1.findViewById(R.id.edittext);
                                String content = editText.toString();
                                Stroke new1  = new Stroke(content,"","");
                                mData.add(new1);
                            }
                        })
                        .show();
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
