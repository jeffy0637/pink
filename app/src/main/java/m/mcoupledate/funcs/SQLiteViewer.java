package m.mcoupledate.funcs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
