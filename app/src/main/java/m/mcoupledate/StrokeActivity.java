package m.mcoupledate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import m.mcoupledate.classes.NavigationActivity;

public class StrokeActivity extends NavigationActivity {

    Intent intent;
    String tripId;
    String tripType;

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
        bundle.putString("TT",tripType);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "fragment").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //
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
        menu.findItem(R.id.action_lists).setVisible(!listFragment);
        menu.findItem(R.id.action_board).setVisible(listFragment);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_lists:
                showFragment(ListFragment.newInstance());
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
                        Toast.makeText(this,"共享喔",Toast.LENGTH_SHORT).show();
                }
                break;
            case "collection":

                break;
            case "search":

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getTripType()
    {
        String t = tripType;
        return t;
    }
}
