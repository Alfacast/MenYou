package com.alfacast.menyou.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alfacast.menyou.adapter.CustomListAdapter;
import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.model.ListaMenu;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuRistoranteActivity extends AppCompatActivity {

    private static final String TAG = MenuRistoranteActivity.class.getSimpleName();

    // Portata json url
    private static final String url = "http://www.cinesofia.it/alfacast/youmenulogin/get_menu_ristorante.php?idristorante=";
    private ProgressDialog pDialog;
    private List<ListaMenu> menuList = new ArrayList<ListaMenu>();
    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_ristorante_activity);

        // recupero id ristorante dalla activity precedente
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        Bundle c = intent.getExtras();

        final String idristorante = b.getString("idristorante");
        final String nomeristorante = c.getString("nomeristorante");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu "+nomeristorante);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListAdapter(this, menuList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest menuReq = new JsonArrayRequest(url+idristorante,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);

                                ListaMenu menu = new ListaMenu();
                                menu.setIdMenu(obj.getString("id"));
                                menu.setNomeMenu(obj.getString("nomemenu"));
                                menu.setNomeRistorante(obj.getString("nomeristorante"));
                                menu.setThumbnail(obj.getString("foto"));

                                // adding portata to portata array
                                menuList.add(menu);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {

                String id = ((TextView) view.findViewById(R.id.idmenu)).getText().toString();

                // send portata id to portata list activity to get list of portate under that menu

                Bundle b= new Bundle();
                b.putString("idmenu", id);
                Intent intent = new Intent(
                        getApplicationContext(),
                        PortataActivity.class);
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
