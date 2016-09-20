package m.mcoupledate;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.androidquery.AQuery;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener  {

    //callback錯誤
    private CallbackManager fbCallbackManager;
    private GoogleApiClient mGoogleApiClient;

    private AQuery mAQuery;

    private String conAPI = "http://140.117.71.216/pinkCon/";
    RequestQueue mQueue;

    private final int REQ_FB_LOGIN = 64206;
    private final int REQ_GPLUS_LOGIN = 0;

    private TextView mDialog;
    private ImageButton fbLogin, fbLogout, gplusLogin;
    private Button gplusLogout;

    //存使用者ID
    private static String id;

    SQLiteDatabase db = null;

    private Context mContext;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;


    //  初始化頁面和變數設定
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //狀態列透明
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //偵測使用者
        mQueue = Volley.newRequestQueue(this);

        mAQuery = new AQuery(this);

        gplusLogin = (ImageButton) findViewById(R.id.gplusLogin);
        gplusLogout = (Button) findViewById(R.id.gplusLogout);
        fbLogin = (ImageButton) findViewById(R.id.fbLogin);
        fbLogout = (ImageButton) findViewById(R.id.fbLogout);

        gplusLogin.setOnClickListener(this);
        gplusLogout.setOnClickListener(this);
        fbLogin.setOnClickListener(this);
        fbLogout.setOnClickListener(this);

        initFBLoginBtn();
        initGPlusLoginBtn();


    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.gplusLogin:
                gplusLogin();
                break;
            case R.id.gplusLogout:
                gplusLogout();
                break;
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
            case REQ_GPLUS_LOGIN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                break;
        }

    }


    //  將使用者資訊存入資料庫
    protected void initUserProfile(final String id, final String name, final String gender, final String birthday, final String relationDate)
    {
        PinkCon.exec("INSERT INTO `member` VALUES ('"+id+"', '"+name+"', '"+gender+"', '"+birthday+"', '"+relationDate+"')", mQueue, conAPI+"pinkCon.php");
    }


    //  初始化FB登入按鈕
    protected void initFBLoginBtn()
    {
        fbCallbackManager = CallbackManager.Factory.create();
//        fbLogin.setReadPermissions(Arrays.asList("user_birthday, user_likes, user_tagged_places"));

        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(final JSONObject object, GraphResponse response) {

                                //使用者ID
                                id = object.optString("id");

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, conAPI+"fbLogin.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                //跳轉與建sqlite
                                                checkSQLiteTableAndGoHome();
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                //mDialog.setText(error.getMessage()+"-----"+error.toString());
                                            }
                                        }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> map = new HashMap<String, String>();

                                        map.put("fbUser", object.toString());

                                        return map;
                                    }
                                };
                                mQueue.add(stringRequest);
                            }
                        });

                Bundle params = new Bundle();
                params.putString("fields", "id,name,gender,birthday,likes{id,name,about,description,location{latitude,longitude,street},phone,public_transit,emails,website,category},tagged_places{place{id,name,about,description,location{latitude,longitude,street},phone,public_transit,emails,website,category}}");
                params.putString("locale", "zh_TW");
                request.setParameters(params);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        });

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

        /*
                            在此處理F登出後結果

         */
    }

    //  初始化GooglePlus登入按鈕
    protected void initGPlusLoginBtn()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        gplusLogin.setSize(SignInButton.SIZE_STANDARD);
//        gplusLogin.setScopes(gso.getScopeArray());

    }

    //  GooglePlus登入
    private void gplusLogin()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQ_GPLUS_LOGIN);
    }

    //  GooglePlus處理登入結果
    private void handleSignInResult(GoogleSignInResult result)
    {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
//            try {
//                Plus.PeopleApi.load(mGoogleApiClient, acct.getId()).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
//                    @Override
//                    public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {
//                        Person me = loadPeopleResult.getPersonBuffer().get(0);
//
//                        mDialog.setText(me.getId()+"---"+me.getDisplayName()+"---"+Integer.toString(me.getGender())+"---"+me.getBirthday());
//                        initUserProfile(me.getId(), me.getDisplayName(), Integer.toString(me.getGender()), me.getBirthday(), "");
//
//                    }
//                });
//            }catch(Exception e){
//
//
//                mDialog.setText(e.toString()+"----"+e.getMessage());
//            }[
            mDialog.setText(acct.getPhotoUrl().toString());
//            mAQuery.id(profilePic).image(acct.getPhotoUrl().toString(), true, true, 0, android.R.drawable.ic_menu_gallery);
            initUserProfile(acct.getId(), acct.getDisplayName(), "", null, null);

            //GooglePlus 登入成功後在此處理內容
            //跳轉與建sqlite
            checkSQLiteTableAndGoHome();


//            mDialog.setText(acct.getPhotoUrl().toString());
        } else {
            // Signed out, show unauthenticated UI.
//            mDialog.setText();
//            mDialog.setText();
        }
    }

    // GooglePlus 登入失敗處理
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        try {
            mDialog.setText("try");
            connectionResult.startResolutionForResult(this, REQ_GPLUS_LOGIN);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            mDialog.setText(e.getMessage());
        }
    }

    //  GooglePlus 登出
    private void gplusLogout() {
        try {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            /**
             *          登出後在此處理
             */
            mDialog.setText("gplus out");
        } catch (Exception e) {
            mDialog.setText(e.getMessage());
        }
    }

    //給其他頁面要求使用者id
    public static String getUserId(){
        return id;
    }

    /**
     * 判斷SQLite有沒有存在資料庫
     */
    public void checkSQLiteTableAndGoHome(){

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
        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
        startActivity(intent);
    }

    /**
     * 從資料撈資料並存入對應SQLite table
     * @param choose 要哪種資料 1.會員 2.紀念日 3.收藏景點 4.收藏行程
     */
    public void insertDataFromMariadbToSQLite(int choose){
        mContext = this;
        mRequestQueue = Volley.newRequestQueue(mContext);
        switch (choose){
            case 1://取得會員 資料並存入sqlite
                mStringRequest = new StringRequest(Request.Method.POST, conAPI+"memberData.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jArr = new JSONArray(response);
                                    JSONObject o;
                                    for (int a=0; a<jArr.length(); ++a) {
                                        o = jArr.getJSONObject(a);
                                        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);
                                        db.execSQL("INSERT INTO member values('"+id+"', '"+o.getString("name")+"', '"+o.getInt("gender")+"', '"+o.getString("birthday")+"', '"+o.getString("relationship_date")+"')");
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
                        map.put("User", id);
                        return map;
                    }
                };
                mRequestQueue.add(mStringRequest);
                break;
            case 2://取得紀念日資料並存入sqlite
                mStringRequest = new StringRequest(Request.Method.POST, conAPI+"memorialDays.php",
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
                        map.put("User", id);
                        return map;
                    }
                };
                mRequestQueue.add(mStringRequest);
                break;
        }
        }
}
