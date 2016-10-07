package com.alfacast.menyou.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alfacast.menyou.UrlConfig;
import com.alfacast.menyou.adapter.CustomListAdapterPortata;
import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.login.helper.SQLiteHandlerUser;
import com.alfacast.menyou.model.ListaPortata;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PreferitiActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG = PortataActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private List<ListaPortata> portataList = new ArrayList<ListaPortata>();
    private ListView listView;
    private CustomListAdapterPortata adapter;
    private SQLiteHandlerUser db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferiti);

        db = new SQLiteHandlerUser(getApplicationContext());
        HashMap<String, String> a = db.getUserDetails();
        final String uiduser = a.get("uid");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //creo la listView verticale per le portate
        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapterPortata(this, portataList);
        listView.setAdapter(adapter);

        Log.d(TAG, "adapter è: "+ adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest portataReq = new JsonArrayRequest(UrlConfig.URL_Preferiti+uiduser,

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
                                Log.d(TAG, "portataList è: "+ portataList.toString());

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

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {

                String id = ((TextView) view.findViewById(R.id.idportata)).getText().toString();

                // send portata id to portata list activity to get list of portate under that menu

                Bundle b= new Bundle();
                b.putString("idportata", id);
                Intent intent = new Intent(
                        getApplicationContext(),
                        PortataDettaglioActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

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
