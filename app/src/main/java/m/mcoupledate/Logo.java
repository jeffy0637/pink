package m.mcoupledate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class Logo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        //狀態列透明
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try
                {
                    if (Logo.this.getSharedPreferences("pinkpink", 0).getString("mId", null)!=null)
                    {
                        startActivity(new Intent(Logo.this, HomePageActivity.class));
                        Logo.this.finish();
                    }
                    else
                    {
                        throw new Exception("");
                    }
                }
                catch(Exception e)
                {
                    startActivity(new Intent().setClass(Logo.this, MainActivity.class));
                    Logo.this.finish();
                }
            }
        }, 2000);
    }
}