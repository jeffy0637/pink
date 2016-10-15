package m.mcoupledate;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class StrokeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stroke);

        if (savedInstanceState == null) {
            showFragment(BoardFragment.newInstance());
        }

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_color)));
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, "fragment").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
