package dev.tienminh.freebie.TabsFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by thang on 23/02/2017.
 */

public class PagerFragment extends FragmentStatePagerAdapter {

    ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    ArrayList<String> mTitleList = new ArrayList<String>();

    public PagerFragment(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mTitleList.size();
    }
    public CharSequence getPageTitle (int position){
        //return mTitleList.get(position);
        return null;
    }
    public void AddTabLayout (Fragment fragment,String title){
        mFragmentList.add(fragment);
        mTitleList.add(title);
    }
}
