package com.alfacast.menyou.client;

/**
 * Created by Gabriele Bellissima on 07/06/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alfacast.menyou.login.R;

public class TabRistoranti extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_ristoranti, container, false);
    }
}
