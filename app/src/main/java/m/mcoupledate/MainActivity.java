package m.mcoupledate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import m.mcoupledate.funcs.PinkCon;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    //callback錯誤
    private CallbackManager fbCallbackManager;


    RequestQueue mQueue;

    private View rootView;


    private final int REQ_FB_LOGIN = 64206;

    private ImageButton fbLogin, fbLogout;

    private JSONArray drawnPlaceTopics = new JSONArray();



    //存使用者ID
    private static String mId;

    SQLiteDatabase db = null;


    //  初始化頁面和變數設定
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = this.getSharedPreferences("pinkpink", 0);
        prefEditor = pref.edit();

        //狀態列透明
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

//        fbLogout();

        mQueue = Volley.newRequestQueue(this);

        rootView = findViewById(R.id.rootView);


        fbLogin = (ImageButton) findViewById(R.id.fbLogin);
        fbLogout = (ImageButton) findViewById(R.id.fbLogout);

        fbLogin.setOnClickListener(this);
        fbLogout.setOnClickListener(this);

        initFBLoginBtn();
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.fbLogin:
                fbLogin();
                break;
            case R.id.fbLogout:
                fbLogout();
                break;
        }

    }



    //  當FB或GooglePlus傳回結果時在此操作
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQ_FB_LOGIN:
                fbCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;

        }

    }


    //  初始化FB登入按鈕
    protected void initFBLoginBtn()
    {
        fbCallbackManager = CallbackManager.Factory.create();
//        fbLogin.setReadPermissions(Arrays.asList("user_birthday, user_likes, user_tagged_places"));

        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {


                final GraphRequest request = GraphRequest.newGraphPathRequest(loginResult.getAccessToken(), "me?fields=id,name,gender,birthday&locale=zh_TW",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(final GraphResponse response)
                            {
                                final JSONObject fbUser = response.getJSONObject();

                                mId = fbUser.optString("id");

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL +"login_fbLogin.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                checkSQLiteTable();

                                                prefEditor.putString("mId", mId);
                                                prefEditor.putString("mName", fbUser.optString("name"));
                                                prefEditor.commit();

                                                try
                                                {
                                                    JSONObject drawTaggedPlacesDetail = new JSONObject(response);

                                                    if (drawTaggedPlacesDetail.optBoolean("ifNeedDraw"))
                                                        drawTaggedPlaces(loginResult.getAccessToken(), drawTaggedPlacesDetail.optString("lastLoginDate"));

                                                }
                                                catch (JSONException e)
                                                {   e.printStackTrace();    }


                                                startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                                                MainActivity.this.finish();
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                PinkCon.retryConnect(rootView, PinkCon.CONNECT_FAIL, null,
                                                        new View.OnClickListener()
                                                        {
                                                            @Override
                                                            public void onClick(View view)
                                                            {   onCompleted(response);  }
                                                        });
                                            }
                                        }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> map = new HashMap<String, String>();

                                        map.put("fbUser", fbUser.toString());

                                        return map;
                                    }
                                };
                                mQueue.add(stringRequest);
                            }
                        });
                request.executeAsync();
            }

            @Override
            public void onCancel()
            {
                Toast.makeText(MainActivity.this, "登入失敗", Toast.LENGTH_LONG).show();
                fbLogout();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MainActivity.this, "登入失敗", Toast.LENGTH_LONG).show();
                Log.d("HFLOGINFAIL", exception.getMessage());
            }
        });

    }


    private void drawTaggedPlaces(AccessToken accessToken, final String lastLoginDate)
    {
        final Response.Listener nextURLListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    processNext(new JSONObject(response), lastLoginDate, this);
                }
                catch (JSONException e)
                {   e.printStackTrace();    }
            }
        };

//        Response.ErrorListener nextURLErrorListener;

        GraphRequest request = GraphRequest.newGraphPathRequest(accessToken, "/me?fields=tagged_places{created_time,place{id,name,category,category_list{id}}}&locale=zh_TW",
                new GraphRequest.Callback()
                {
                    @Override
                    public void onCompleted(GraphResponse response)
                    {
                        processNext(response.getJSONObject().optJSONObject("tagged_places"), lastLoginDate, nextURLListener);
                    }
                });

        request.executeAsync();
    }

    private void processNext(JSONObject taggedPlaces, String lastLoginDate, Response.Listener nextURLListener)
    {

        try
        {
            JSONArray places = taggedPlaces.optJSONArray("data");

            for (int a=0; a<places.length(); ++a)
            {
//                if (places.getJSONObject(a).optString("created_time").substring(0, 10).compareTo("2004-12-26")<0)
                if (places.getJSONObject(a).optString("created_time").substring(0, 10).compareTo(lastLoginDate)<0)
                {
                    saveTaggedPlaceTopics();
                    return ;
                }

                drawnPlaceTopics.put(places.optJSONObject(a).optJSONObject("place").optJSONArray("category_list"));
            }


            String nextURL = taggedPlaces.optJSONObject("paging").optString("next").replace("\\", "");

            if (nextURL.compareTo("")!=0)
            {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, nextURL, nextURLListener, null);
                mQueue.add(stringRequest);
            }
            else
            {
                saveTaggedPlaceTopics();
            }
        }
        catch (Exception e)
        {   Log.d("HFNextErr", e.getMessage());    }

    }

    private void saveTaggedPlaceTopics()
    {
        Log.d("HFdrawedPlaceTopics", drawnPlaceTopics.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL +"login_saveTaggedPlaceTopics.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("HFsaveResponse", response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        PinkCon.retryConnect(rootView, PinkCon.SUBMIT_FAIL, null,
//                                new View.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(View view)
//                                    {   saveTaggedPlaceTopics();    }
//                                });
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("mId", mId);
                map.put("drawnPlaceTopics", drawnPlaceTopics.toString());

                return map;
            }
        };
        mQueue.add(stringRequest);
    }




    protected void fbLogin()
    {
        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("user_birthday, user_likes, user_tagged_places"));
        /*
                            登入後結果
                            在 initFBLoginBtn() 的 onSuccess中處理
                */
    }


    protected void fbLogout()
    {
        LoginManager.getInstance().logOut();
        prefEditor.clear().commit();
    }




    //給其他頁面要求使用者id
    @Deprecated
    public static String getUserId(){
        return mId;
    }

    /**
     * 判斷SQLite有沒有存在資料庫
     */
    public void checkSQLiteTable(){

        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='member'", null);
        if(cursor.getCount() == 0){
            //沒有member資料表 要創建
            db.execSQL("CREATE TABLE member(_id varchar(255) primary key , name varchar(255), gender INTEGER, birthday varchar(255), relationship_date varchar(255))");
            db.close();
            //從資料庫匯入
            insertDataFromMariadbToSQLite(1);
        }
        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);
        Cursor cursor2 = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table' AND name='memorialday'", null);
        if(cursor2.getCount() == 0){
            //沒有memorialday資料表 要創建
            db.execSQL("CREATE TABLE memorialday(_id INTEGER primary key autoincrement, eventName varchar(255) , eventDate varchar(255))");
            db.close();
            //從資料庫匯入
            insertDataFromMariadbToSQLite(2);
        }

        /*if(第一次近來或會員資料空缺)
        Intent intent = new Intent(MainActivity.this, MemberData.class);
        startActivity(intent);*/

    }

    /**
     * 從資料撈資料並存入對應SQLite table
     * @param choose 要哪種資料 1.會員 2.紀念日 3.收藏景點 4.收藏行程
     */
    public void insertDataFromMariadbToSQLite(int choose){

        StringRequest stringRequest;
        switch (choose){
            case 1://取得會員 資料並存入sqlite
                stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL +"memberData.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jArr = new JSONArray(response);
                                    JSONObject o;
                                    for (int a=0; a<jArr.length(); ++a) {
                                        o = jArr.getJSONObject(a);
                                        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);
                                        db.execSQL("INSERT INTO member values('"+ mId +"', '"+o.getString("name")+"', '"+o.getInt("gender")+"', '"+o.getString("birthday")+"', '"+o.getString("relationship_date")+"')");
                                        db.close();
                                    }
                                }
                                catch (Exception e) {
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("User", mId);
                        return map;
                    }
                };
                mQueue.add(stringRequest);
                break;

            case 2://取得紀念日資料並存入sqlite
                stringRequest = new StringRequest(Request.Method.POST, PinkCon.URL +"memorialDays.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jArr = new JSONArray(response);
                                    JSONObject o;
                                    for (int a=0; a<jArr.length(); a++) {
                                        o = jArr.getJSONObject(a);
                                        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);
                                        db.execSQL("INSERT INTO memorialday(eventName, eventDate) values('"+o.getString("eventName")+"',  '"+o.getString("eventDate")+"')");
                                        db.close();
                                    }
                                }
                                catch (Exception e) {
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("User", mId);
                        return map;
                    }
                };
                mQueue.add(stringRequest);
                break;
        }
        }
}
