package m.mcoupledate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import m.mcoupledate.classes.NavigationActivity;
import m.mcoupledate.classes.funcs.PinkCon;

public class MyTrip extends NavigationActivity {

    private Context context;

    private RequestQueue mQueue;

    private String mId;

    private ListView strokeListView;
    private StrokeSearchAdapter strokeSearchAdapter;

    Button startDateEditBtn,endDateEditBtn;
    Intent intent;
    String tripType;
    private int yearS, monthOfYearS, dayOfMonthS;
    private int yearE, monthOfYearE, dayOfMonthE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip);

        this.context = this;
        mQueue = Volley.newRequestQueue(context);

        mId = getPref().getString("mId", "");

        intent = this.getIntent();
        tripType = intent.getStringExtra("trip");



        Calendar calendar = Calendar.getInstance();
        yearS = calendar.get(Calendar.YEAR);
        monthOfYearS = calendar.get(Calendar.MONTH);
        dayOfMonthS = calendar.get(Calendar.DAY_OF_MONTH);
        yearE = calendar.get(Calendar.YEAR);
        monthOfYearE = calendar.get(Calendar.MONTH);
        dayOfMonthE = calendar.get(Calendar.DAY_OF_MONTH);





        strokeSearchAdapter = new StrokeSearchAdapter(context);
        strokeListView = (ListView) findViewById(R.id.tripListView);
        strokeListView.setAdapter(strokeSearchAdapter);
        strokeListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent();
                intent.setClass(MyTrip.this, StrokeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tripId", strokeSearchAdapter.getItem(position).tId);
                bundle.putString("tripType", tripType);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        strokeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long id) {
                return true;
            }
        });


        initData();



    }

    /*
    初始化数据
     */
    private void initData()
    {
        Log.d("HFinitData", tripType);
        if (tripType.compareTo("collection")==0)
            initDataByCollection();
        else
            initDataByMy();
    }



    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (tripType.compareTo("my")==0) {
            getMenuInflater().inflate(R.menu.mytrip, menu);//这里是调用menu文件夹中的main.xml，在登陆界面label右上角的三角里显示其他功能
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (tripType.compareTo("my")==0) {
            switch (item.getItemId()) {
                case R.id.add_travel:
                    LayoutInflater inflater = LayoutInflater.from(MyTrip.this);
                    final View v = inflater.inflate(R.layout.dialog_add_trip, null);
                    //語法一：new AlertDialog.Builder
                    final String url = "https://couple-project.firebaseio.com";
                    new AlertDialog.Builder(MyTrip.this)
                            .setTitle("新增行程")
                            .setView(v)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String travelName, startDate, endDate;
                                    EditText editText = (EditText) (v.findViewById(R.id.name));
                                    travelName = editText.getText().toString();
                                    startDate = yearS + "-" + monthOfYearS + "-" + dayOfMonthS;
                                    endDate = yearE + "-" + monthOfYearE + "-" + dayOfMonthE;
                                    //加入firebase 取得要加入的node
                                    Firebase.setAndroidContext(getApplicationContext());//this用mBoardView.getContext()取代
                                    new Firebase(url).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            Long tId = dataSnapshot.getChildrenCount();
                                            //Toast.makeText(getApplicationContext(), ""+tId, Toast.LENGTH_SHORT).show();
                                            Stroke travel  = new Stroke(travelName, startDate,endDate,""+tId);
                                            strokeSearchAdapter.add(travel);
                                            Firebase addTravelRef = (dataSnapshot.child("" + tId)).getRef();
                                            addTravelRef.setValue(travel);
                                            //MyTrip.this.getSharedPreferences("pinkpink", 0).getString("mId", "")
                                            Firebase addTravelMIdRef = (dataSnapshot.child("" + tId).child("editor")).getRef();
                                            Map<String, Object> nameMap = new HashMap<String, Object>();
                                            nameMap.put("0", MyTrip.this.getSharedPreferences("pinkpink", 0).getString("mId", ""));
                                            addTravelMIdRef.updateChildren(nameMap);
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });

                                }
                            })
                            .show();
                    startDateEditBtn = (Button) v.findViewById(R.id.startDateEditBtn);
                    endDateEditBtn = (Button) v.findViewById(R.id.endDateEditBtn);
                    startDateEditBtn.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View v) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(MyTrip.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    yearS = year;
                                    monthOfYearS = monthOfYear + 1;
                                    dayOfMonthS = dayOfMonth;
                                    startDateEditBtn.setText(yearS + "-" + monthOfYearS + "-" + dayOfMonthS);
                                }
                            }, yearS, monthOfYearS, dayOfMonthS);
                            datePickerDialog.show();

                        }
                    });
                    endDateEditBtn.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View v) {
                            DatePickerDialog datePickerDialog2 = new DatePickerDialog(MyTrip.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    yearE = year;
                                    monthOfYearE = monthOfYear + 1;
                                    dayOfMonthE = dayOfMonth;
                                    endDateEditBtn.setText(yearE + "-" + monthOfYearE + "-" + dayOfMonthE);
                                }
                            }, yearE, monthOfYearE, dayOfMonthE);
                            datePickerDialog2.show();

                        }
                    });

                    return true;
                case R.id.remove_travel:
                    new android.support.v7.app.AlertDialog.Builder(MyTrip.this)
                            .setTitle("刪除行程")
                            .setMessage("長按行程即可刪除")
                            .setPositiveButton("知道了", null)
                            .show();
                    return true;
            }
        }
        else
        {

        }


        return super.onOptionsItemSelected(item);
    }



    private void initDataByCollection()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL+"myTrip_getMyTravelCollection.php?mId=" + mId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            Log.d("HFresponse", response);
                            JSONArray myTravelCollection = new JSONArray(response);
                            initDataFromFirebase(myTravelCollection);
                        }
                        catch (JSONException e)
                        {   e.printStackTrace();    }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                    }
                });

        mQueue.add(stringRequest);

    }



    private void initDataByMy()
    {
        final String url = "https://couple-project.firebaseio.com";

        //丟一個for迴圈印出各個行程
        Firebase.setAndroidContext(this);//this用mBoardView.getContext()取代
        new Firebase(url).addChildEventListener(new ChildEventListener() {
            int count = 0;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(int i = 0 ; i < dataSnapshot.getChildrenCount() ; i++){
                    for(int j = 0 ; j < dataSnapshot.child(""+i).child("editor").getChildrenCount() ; j ++){
                        if((""+dataSnapshot.child("" + i).child("editor").child("" + j).getValue()).equals(MyTrip.this.getSharedPreferences("pinkpink", 0).getString("mId", ""))){
                            String tName = "" + dataSnapshot.child("" + i).child("tripName").getValue();
                            String sDate = "" + dataSnapshot.child("" + i).child("start_date").getValue();
                            String eDate = "" + dataSnapshot.child("" + i).child("end_date").getValue();
                            String MyTripTId = "" + dataSnapshot.child("" + i).child("tId").getValue();
                            Stroke travel1  = new Stroke(tName, sDate, eDate,MyTripTId );
                            strokeSearchAdapter.add(travel1);
                        }
                    }

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }



    private void initDataFromFirebase(final JSONArray tIdJSONArray)
    {
        final String firebaseUrl = "https://couple-project.firebaseio.com/travel";
        Firebase.setAndroidContext(this);

        Log.d("HFfromeFirebase", tIdJSONArray.toString());
        new Firebase(firebaseUrl).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                JSONArray strokeJSONArray = new JSONArray();

                for(int i = 0 ; i < dataSnapshot.getChildrenCount() ; i++)
                {
                    DataSnapshot snapShotChild = dataSnapshot.child(String.valueOf(i));

                    String tId = String.valueOf(snapShotChild.child("tId").getValue());

                    if (!ifJSONArrayContains(tIdJSONArray, tId))
                        continue;


                    String tName = String.valueOf(snapShotChild.child("tripName").getValue());
                    String startDate = String.valueOf(snapShotChild.child("start_date").getValue());
                    String endDate = String.valueOf(snapShotChild.child("end_date").getValue());


                    JSONObject aTrip =new JSONObject();
                    try
                    {
                        aTrip.put("tId", tId);
                        aTrip.put("tName", tName);
                        aTrip.put("startDate", startDate);
                        aTrip.put("endDate", endDate);
                    }
                    catch (JSONException e)
                    {   e.printStackTrace();    }

                    JSONArray tripDays = new JSONArray();
                    for(int j = 1 ; j <= snapShotChild.child("site").getChildrenCount() ; j++)
                    {
                        JSONArray tripDaySites = new JSONArray(); //  一天中的景點
                        for(int k = 0 ; k < snapShotChild.child("site").child("day" + j).getChildrenCount() ; k++)
                        {
                            String sId = String.valueOf(snapShotChild.child("site").child("day" + j).child("" + k).child("sId").getValue());
                            tripDaySites.put(sId);
                        }
                        tripDays.put(tripDaySites);
                    }

                    try
                    {
                        aTrip.put("sites", tripDays);
                    }
                    catch (JSONException e)
                    {   e.printStackTrace();    }

                    strokeJSONArray.put(aTrip);
                }

                strokeSearchAdapter.addAll(strokeJSONArray);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    private Boolean ifJSONArrayContains(JSONArray jsonArray, String string)
    {
        for (int a=0; a<jsonArray.length(); ++a)
        {
            if (jsonArray.optString(a).compareTo(string)==0)
            {
                return true;
            }
        }

        return false;
    }



}
