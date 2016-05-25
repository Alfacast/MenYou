package com.youmenu.macpro.loginyoumenu.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.youmenu.macpro.loginyoumenu.R;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.youmenu.macpro.loginyoumenu.helper.SQLiteHandlerRestaurant;
import com.youmenu.macpro.loginyoumenu.helper.SessionManager;

import java.util.HashMap;

public class MainRistoranteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainRistoranteActivity.class.getSimpleName();

    private TextView txtName;
    private TextView txtPartitaIva;


    private SessionManager session;
    private SQLiteHandlerRestaurant dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ristorante);

        txtName = (TextView) findViewById(R.id.name);
        txtPartitaIva = (TextView) findViewById(R.id.partitaIva);


        dbr = new SQLiteHandlerRestaurant(getApplicationContext());


        // session manager
        session = new SessionManager(getApplicationContext());



        if (!session.isLoggedIn()) {
            logoutUser();
        }


        // Recuperare dati utente da SQLite
        HashMap<String, String> ristorante = dbr.getUserDetails();
        String name = ristorante.get("name");
        String partitaIva = ristorante.get("partitaIva");

        Log.d(TAG, "db ristorante");


        // Displaying the user details on the screen
        txtName.setText(name);
        txtPartitaIva.setText(partitaIva);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

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

        //dbr.deleteUsers();
        dbr.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainRistoranteActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
