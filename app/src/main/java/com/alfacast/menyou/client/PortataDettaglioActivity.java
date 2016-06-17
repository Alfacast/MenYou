package com.alfacast.menyou.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alfacast.menyou.adapter.CustomListAdapterPortata;
import com.alfacast.menyou.adapter.CustomListAdapterPortataDettaglio;
import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.model.ListaPortata;
import com.alfacast.menyou.model.PortataDettaglio;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PortataDettaglioActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG = PortataDettaglioActivity.class.getSimpleName();

    // Portata json url
    private static final String url = "http://www.cinesofia.it/alfacast/youmenulogin/get_portata_dettaglio.php?idportata=";
    private ProgressDialog pDialog;
    private List<PortataDettaglio> portataList = new ArrayList<PortataDettaglio>();
    private ListView listView;
    private CustomListAdapterPortataDettaglio adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portata_dettaglio_activity);

        // recupero id portata dalla activity precedente
        Intent intent=getIntent();
        Bundle b=intent.getExtras();

        final String idportata=b.getString("idportata");
        Log.d(TAG,"id portata: "+ idportata);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        listView = (ListView) findViewById(R.id.listdettaglio);
        adapter = new CustomListAdapterPortataDettaglio(this, portataList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest portataReq = new JsonArrayRequest(url+idportata,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);

                                PortataDettaglio portata = new PortataDettaglio();
                                portata.setNomePortata(obj.getString("nome"));
                                portata.setThumbnailPortata(obj.getString("foto")); //foto ristorante
                                portata.setFoto(obj.getString("fotoportata")); //foto portata
                                portata.setDescrizionePortata(obj.getString("descrizione"));
                                portata.setCategoria(obj.getString("categoria"));
                                portata.setPrezzo(obj.getString("prezzo"));
                                portata.setNomeRistorante(obj.getString("nomeristorante"));
                                portata.setIndirizzo(obj.getString("indirizzo"));
                                portata.setTelefono(obj.getString("telefono"));
                                portata.setIdPortata(obj.getString("id"));

                                // adding portata to portata array
                                portataList.add(portata);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(portataReq);

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
