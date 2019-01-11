package com.example.q.madcamp_project_3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                PoolFragment frg_pool = new PoolFragment();
                return frg_pool;
            case 1:
                ShareFragment frg_share = new ShareFragment();
                return frg_share;
            case 2:
                AlarmFragment frg_alarm = new AlarmFragment();
                return frg_alarm;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
