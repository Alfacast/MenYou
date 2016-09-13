package com.alfacast.menyou.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alfacast.menyou.UrlConfig;
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

public class PortataActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG = PortataActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private List<ListaPortata> portataList = new ArrayList<ListaPortata>();
    private ListView listView;
    private CustomListAdapterPortata adapter;

    private RecyclerView horizontal_recycler_view;
    private ArrayList<String> horizontalList;
    private HorizontalAdapter horizontalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portata_activity);

        // recupero id menu dalla activity precedente
        Intent intent=getIntent();
        Bundle b=intent.getExtras();

        final String idmenu=b.getString("idmenu");
        Log.d(TAG,"id menu: "+ idmenu);

        //creo il menu orizzontale per le categorie
        horizontal_recycler_view= (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        horizontalList=new ArrayList<>();

        // Creating volley request obj
        final JsonArrayRequest categoriaReq = new JsonArrayRequest(UrlConfig.URL_PortataActivity_2 + idmenu,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG,"response" + response.toString());
                        //hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);

                                horizontalList.add(obj.getString("categoria"));

                                Log.d(TAG," categoria :" +  horizontalList.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        horizontalAdapter=new HorizontalAdapter(horizontalList);

                        LinearLayoutManager horizontalLayoutManagaer
                                = new LinearLayoutManager(PortataActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
                        horizontal_recycler_view.setAdapter(horizontalAdapter);
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //hidePDialog();

            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(categoriaReq);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        JsonArrayRequest portataReq = new JsonArrayRequest(UrlConfig.URL_PortataActivity_1+idmenu,

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
        Log.d(TAG, "portataList è: "+ UrlConfig.URL_PortataActivity_1);

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

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        private List<String> horizontalList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txtView;

            public MyViewHolder(View view) {
                super(view);
                txtView = (TextView) view.findViewById(R.id.categoria);

            }
        }

        public HorizontalAdapter(List<String> horizontalList) {
            this.horizontalList = horizontalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.portata_horizontal_item_view, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.txtView.setText(horizontalList.get(position));

            holder.txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(PortataActivity.this,holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();

                    //cancella la listView per caricare la categoria
                    portataList.clear();
                    // recupero id menu dalla activity precedente
                    Intent intent=getIntent();
                    Bundle b=intent.getExtras();
                    final String idmenu=b.getString("idmenu");

                    //recupero categoria per mandarla all'API
                    String categoria = holder.txtView.getText().toString();
                    Log.d(TAG,"categoria  è: "+ categoria);


                    // Creating volley request obj
                    JsonArrayRequest portataCat = new JsonArrayRequest(UrlConfig.URL_PortataActivity_3+idmenu+"&categoria="+categoria,

                            new Response.Listener<JSONArray>() {

                                @Override
                                public void onResponse(JSONArray response) {
                                    Log.d(TAG, response.toString());
                                    //hidePDialog();

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
                                            Log.d(TAG, "List è: "+ portataList.toString());

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
                            //hidePDialog();

                        }

                    });

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(portataCat);

                }
            });
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
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
