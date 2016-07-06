package com.alfacast.menyou.client;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Log tag
    private static final String TAG = RistoranteDettaglioActivity.class.getSimpleName();

    // Portata json url
    private static final String url = "http://www.cinesofia.it/alfacast/youmenulogin/get_ristoranti_map.php";
    private ProgressDialog pDialog;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        // Creating volley request obj
        JsonArrayRequest ristoranteReq = new JsonArrayRequest(url,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);

                                String nomeR = obj.getString("nome");
                                String indirizzoR = obj.getString("indirizzo");

                                Geocoder geocoder = new Geocoder(getBaseContext());
                                List<Address> addresses;
                                try {
                                    addresses = geocoder.getFromLocationName(indirizzoR, 1);
                                    if (addresses.size() > 0) {
                                        double latitude = addresses.get(0).getLatitude();
                                        double longitude = addresses.get(0).getLongitude();

                                        LatLng position = new LatLng(latitude, longitude);
                                        LatLng start = new LatLng(42.214890, 13.309858);

                                        //Initializing our map
                                        mMap = googleMap;
                                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 5));
                                        mMap.animateCamera(CameraUpdateFactory.zoomTo(5), 500, null);

                                        mMap.addMarker(new MarkerOptions()
                                                .position(position)
                                                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.logo_marker)))
                                                .title(nomeR));

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(ristoranteReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}
