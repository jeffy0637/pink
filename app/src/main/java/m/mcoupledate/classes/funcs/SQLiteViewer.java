package m.mcoupledate.classes.funcs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import m.mcoupledate.R;
import m.mcoupledate.classes.NavigationActivity;

public class SQLiteViewer extends NavigationActivity
{
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_viewer);



        StringRequest stringRequest = new StringRequest(Request.Method.GET, PinkCon.URL + "1234.R",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("HFRTEST", response);
                        ((TextView)findViewById(R.id.rTest)).setText(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {}
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        db = openOrCreateDatabase("userdb.db", MODE_PRIVATE, null);//打開資料庫

        TextView memberText = (TextView) findViewById(R.id.member);
        memberText.setText(getSqlResult("SELECT * FROM member"));

        TextView memerialDayText = (TextView) findViewById(R.id.memorialDay);
        memerialDayText.setText(getSqlResult("SELECT * FROM memorialday"));

        db.close();
    }


    private String getSqlResult(String sql)
    {
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        String text = "\n--\n";

        do
        {
            int a = 0;
            while(true)
            {
                try
                {
                    text += cursor.getString(a) + ",  ";
                    ++a;
                }
                catch (Exception e)
                {
                    Log.d("HFerror", e.getMessage());
                    break;
                }
            }
            text += "\n";
        }
        while(cursor.moveToNext());

        return text;
    }
}
