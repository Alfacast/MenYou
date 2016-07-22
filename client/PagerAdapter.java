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
                TabRistoranti tab1 = new TabRistoranti();
                return tab1;
            case 1:
                TabMenu tab2 = new TabMenu();
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
