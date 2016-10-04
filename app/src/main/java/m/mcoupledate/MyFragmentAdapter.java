package m.mcoupledate;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by user on 2016/9/28.
 */

public class MyFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    public MyFragmentAdapter(FragmentManager fm, List fragmentList) {
        super(fm);
        // TODO Auto-generated constructor stub this.
        this.fragmentList = fragmentList;
    }
    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub return fragmentList.
        return fragmentList.get(arg0);
    }
    @Override public int getCount() {
        // TODO Auto-generated method stub
        return fragmentList.size(); } }

