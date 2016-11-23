package m.mcoupledate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import m.mcoupledate.classes.NavigationActivity;

public class StrokeActivity extends NavigationActivity {

    private static String tripType;
    Intent intent;
    String tripId;
    //String tripType;
    EditText share_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stroke);

        intent = this.getIntent();
        // String tripId = intent.getStringExtra("tripId");

        Bundle bundle = this.getIntent().getExtras();
        tripId = bundle.getString("tripId");
        tripType = bundle.getString("tripType");

        if (savedInstanceState == null) {
            showFragment(BoardFragment.newInstance());
        }

        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color)));
    }

    private void showFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("TI",tripId);
        bundle.putString("TT",tripType);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "fragment").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        //getMenuInflater().inflate(R.menu.menu_board_move, menu); 原版
        switch (tripType){
            case "my":
                getMenuInflater().inflate(R.menu.menu_board, menu);
                Toast.makeText(this,"進入:"+tripType+" ID為"+tripId,Toast.LENGTH_SHORT).show();
                break;
            case "collection":
                getMenuInflater().inflate(R.menu.mytrip_like, menu);
                Toast.makeText(this,"進入:"+tripType+" ID為"+tripId,Toast.LENGTH_SHORT).show();
                break;
            case "search":
                getMenuInflater().inflate(R.menu.travel_search, menu);
                Toast.makeText(this,"進入:"+tripType+" ID為"+tripId,Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean listFragment = getSupportFragmentManager().findFragmentByTag("fragment") instanceof ListFragment;
        //menu.findItem(R.id.action_lists).setVisible(!listFragment);
        //menu.findItem(R.id.action_board).setVisible(listFragment);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_lists:
//                showFragment(ListFragment.newInstance());
                return true;
            case R.id.action_board:
                showFragment(BoardFragment.newInstance());
                return true;
        }
        //各按鈕作用 首項為測試
        switch (tripType){
            case "my":
                switch (item.getItemId()) {
                    case  R.id.together :
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.share_others, null);
                        dialog.setView(layout);
                        share_dialog = (EditText)layout.findViewById(R.id.name);
                        dialog.setPositiveButton("查找", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String searchC = share_dialog.getText().toString();
                                Toast.makeText(StrokeActivity.this,searchC,Toast.LENGTH_SHORT).show();
                            }
                        });

                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        });
                        dialog.show();

                }
                break;
            case "collection":

                break;
            case "search":
                switch (item.getItemId()) {
                    case  R.id.together :
                        Toast.makeText(this,"共享喔",Toast.LENGTH_SHORT).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String getTripType()
    {
        String t = tripType;
        return t;
    }
}
