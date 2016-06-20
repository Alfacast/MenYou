package com.alfacast.menyou.client;

/**
 * Created by Gabriele Bellissima on 07/06/2016.
 * Gestione dei Fragment (tab menu e ristoranti)
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabMenu tab1 = new TabMenu();
                return tab1;
            case 1:
                TabRistoranti tab2 = new TabRistoranti();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
