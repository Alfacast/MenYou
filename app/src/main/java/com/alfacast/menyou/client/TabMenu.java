package com.alfacast.menyou.client;

/**
 * Created by Gabriele Bellissima on 07/06/2016.
 * Gestione del tab menu su MainClienteActivity
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

public class TabMenu extends Fragment {

    // Log tag
    private static final String TAG = TabMenu.class.getSimpleName();

    // Menu json url
    private static final String url = "http://www.cinesofia.it/alfacast/youmenulogin/get_menu.php";
    private ProgressDialog pDialog;
    private List<ListaMenu> menuList = new ArrayList<ListaMenu>();
    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_menu, container, false);

        listView = (ListView) view.findViewById(R.id.list);
        adapter = new CustomListAdapter(getActivity(), menuList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest menuReq = new JsonArrayRequest(url,
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
                                menu.setNomeMenu(obj.getString("nomemenu"));
                                menu.setThumbnail(obj.getString("foto"));
                                menu.setNomeRistorante(obj.getString("nomeristorante"));
                                menu.setIdMenu(obj.getString("id"));

                                // adding menu to menu array
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

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {

                String id = ((TextView) view.findViewById(R.id.idmenu)).getText().toString();

                // send menu id to portata list activity to get list of portate under that menu

                Bundle b= new Bundle();
                b.putString("idmenu", id);
                Intent intent = new Intent(
                        getActivity(),
                        PortataActivity.class);
                intent.putExtras(b);
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
