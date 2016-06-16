package com.alfacast.menyou.restaurant;

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

import com.alfacast.menyou.adapter.CustomListAdapterPortata;
import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.model.ListaPortata;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PortataActivityRistorante extends AppCompatActivity {

    // Log tag
    private static final String TAG = PortataActivityRistorante.class.getSimpleName();

    // Portata json url
    private static final String url = "http://www.cinesofia.it/alfacast/youmenulogin/get_portata.php?idmenu=";
    private ProgressDialog pDialog;
    private List<ListaPortata> portataList = new ArrayList<ListaPortata>();
    private ListView listView;
    private CustomListAdapterPortata adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ristorante_portata_activity);

        // recupero id menu dalla activity precedente
        Intent intent=getIntent();
        Bundle b=intent.getExtras();

        final String idmenu=b.getString("idmenu");
        Log.d(TAG,"id menu: "+ idmenu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Lancio InsertPortataActivity

                Intent i=getIntent();
                Bundle b=i.getExtras();

                Intent intent = new Intent(
                        PortataActivityRistorante.this,
                        InsertPortataActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapterPortata(this, portataList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest portataReq = new JsonArrayRequest(url+idmenu,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);

                                ListaPortata portata = new ListaPortata();
                                portata.setNomePortata(obj.getString("nome"));
                                portata.setThumbnailPortata(obj.getString("foto"));
                                portata.setDescrizionePortata(obj.getString("descrizione"));
                                portata.setCategoria(obj.getString("categoria"));
                                portata.setPrezzo(obj.getString("prezzo"));
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
