package com.alfacast.menyou.restaurant;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alfacast.menyou.adapter.CustomListAdapter;
import com.alfacast.menyou.adapter.CustomListMenuRistorante;
import com.alfacast.menyou.login.R;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alfacast.menyou.login.activity.LoginActivity;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.login.helper.SQLiteHandlerRestaurant;
import com.alfacast.menyou.login.helper.SessionManager;
import com.alfacast.menyou.model.ListaMenu;
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

public class MainRistoranteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainRistoranteActivity.class.getSimpleName();

    // Portata json url
    private static final String url = "http://www.cinesofia.it/alfacast/youmenulogin/get_menu_ristorante.php?idristorante=";
    private ProgressDialog pDialog;
    private List<ListaMenu> menuList = new ArrayList<ListaMenu>();
    private ListView listView;
    private CustomListMenuRistorante adapter;

    private SessionManager session;
    private SQLiteHandlerRestaurant dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ristorante_main_activity);

        dbr = new SQLiteHandlerRestaurant(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Recuperare dati utente da SQLite
        HashMap<String, String> ristorante = dbr.getUserDetails();
        String idristorante = ristorante.get("id_ristorante");

        Log.d(TAG, "id ristorante: "+idristorante);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.list);
        adapter = new CustomListMenuRistorante(this, menuList);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_ristorante, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent i = new Intent(getApplicationContext(),
                    EditAccountRistoranteActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_menu_add) {
            Intent i = new Intent(getApplicationContext(),
                    InsertMenuActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_help) {
            Intent i = new Intent(getApplicationContext(),
                    TutorialActivity.class);
            startActivity(i);
        } else if (id == R.id.btnLogout) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        //elimina utente da sqlite
        dbr.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainRistoranteActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
