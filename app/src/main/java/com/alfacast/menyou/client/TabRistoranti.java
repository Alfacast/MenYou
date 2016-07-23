package com.alfacast.menyou.client;

/**
 * Created by Pietro Fantuzzi on 07/06/2016.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.alfacast.menyou.UrlConfig;
import com.alfacast.menyou.adapter.CustomListRistoranti;
import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;

import com.alfacast.menyou.model.ListaRistoranti;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TabRistoranti extends Fragment {

    // Log tag
    private static final String TAG = TabRistoranti.class.getSimpleName();

    private ProgressDialog pDialog;
    private List<ListaRistoranti> ristorantiList = new ArrayList<ListaRistoranti>();
    private ListView listView;
    private CustomListRistoranti adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_ristoranti, container, false);

        listView = (ListView) view.findViewById(R.id.list);
        adapter = new CustomListRistoranti(getActivity(), ristorantiList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest menuReq = new JsonArrayRequest(UrlConfig.URL_TabRistorante,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                ListaRistoranti ristoranti = new ListaRistoranti();
                                ristoranti.setNome(obj.getString("nome"));
                                ristoranti.setIndirizzo(obj.getString("indirizzo"));
                                ristoranti.setTelefono(obj.getString("telefono"));
                                ristoranti.setIdRistorante(obj.getString("id"));
                                ristoranti.setThumbnail(obj.getString("foto"));

                                // adding menu to menu array
                                ristorantiList.add(ristoranti);

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
        AppController.getInstance().addToRequestQueue(menuReq);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {

                String id = ((TextView) view.findViewById(R.id.idristorante)).getText().toString();
                String nome = ((TextView) view.findViewById(R.id.nome)).getText().toString();

                // send menu id to portata list activity to get list of portate under that menu

                Bundle b= new Bundle();
                b.putString("idristorante", id);
                Bundle c= new Bundle();
                c.putString("nomeristorante", nome);
                Intent intent = new Intent(
                        getActivity(),
                        RistoranteDettaglioActivity.class);
                intent.putExtras(b);
                intent.putExtras(c);
                startActivity(intent);
            }
        });

        return view;
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