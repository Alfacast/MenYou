package com.alfacast.menyou.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.List;

/**
 * Created by Pietro Fantuzzi on 22/06/2016.
 */
public class RistoranteDettaglioActivity extends AppCompatActivity implements OnMapReadyCallback{

    // Log tag
    private static final String TAG = RistoranteDettaglioActivity.class.getSimpleName();

    // Portata json url
    private static final String url = "http://www.cinesofia.it/alfacast/youmenulogin/get_ristorante_dettaglio.php?idristorante=";
    private ProgressDialog pDialog;

    //Our Map
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dettaglio_ristorante_activity);

        // recupero id ristorante dalla activity precedente
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        Bundle c = intent.getExtras();

        final String idristorante = b.getString("idristorante");
        final String nomeristorante = c.getString("nomeristorante");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(nomeristorante);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView foto = (ImageView) findViewById(R.id.foto);
        final TextView nome = (TextView) findViewById(R.id.nome);
        final TextView indirizzo = (TextView) findViewById(R.id.indirizzo);
        final TextView telefono = (TextView) findViewById(R.id.telefono);
        final TextView sito_web = (TextView) findViewById(R.id.sito_web);
        final TextView menuRistorante = (TextView) findViewById(R.id.menuristorante);

        menuRistorante.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Bundle b= new Bundle();
                Bundle c= new Bundle();
                b.putString("idristorante", idristorante);
                c.putString("nomeristorante", nomeristorante);
                Intent intent = new Intent(RistoranteDettaglioActivity.this, MenuRistoranteActivity.class);
                intent.putExtras(b);
                intent.putExtras(c);
                startActivity(intent);
            }
        });

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest ristoranteReq = new JsonArrayRequest(url + idristorante,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        try {

                            JSONObject obj = response.getJSONObject(0);

                            nome.setText(obj.getString("nome"));
                            indirizzo.setText(obj.getString("indirizzo"));
                            telefono.setText("Chiama: " + obj.getString("telefono"));
                            sito_web.setText(obj.getString("sito_web"));

                            byte[] decodedString = Base64.decode(String.valueOf(obj.getString("foto")), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            foto.setImageBitmap(decodedByte);

                        } catch (JSONException e) {
                            e.printStackTrace();
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
    public void onMapReady(final GoogleMap googleMap) {

        // recupero id ristorante dalla activity precedente
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        final String idristorante = b.getString("idristorante");

        // Creating volley request obj
        JsonArrayRequest ristoranteReq = new JsonArrayRequest(url + idristorante,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        try {

                            JSONObject obj = response.getJSONObject(0);

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

                                    //Initializing our map
                                    mMap = googleMap;
                                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 500, null);

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
