package com.alfacast.menyou.client;

/**
 * Created by Pietro Fantuzzi on 07/06/2016.
 * Gestione tab ristoranti su MainClienteActivity
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alfacast.menyou.login.R;
import com.google.android.gms.maps.MapView;

public class TabRistoranti extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate( R.layout.tab_ristoranti, container,
                false );
        mMapView = (MapView) v.findViewById( R.id.mapView );
        mMapView.onCreate( savedInstanceState );


        return v;
    }

    protected MapView mMapView;

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (NullPointerException e) {

            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState( outState );
        if (mMapView != null) {
            mMapView.onSaveInstanceState( outState );
        }
    }
}
