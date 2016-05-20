package com.youmenu.macpro.loginyoumenu.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.youmenu.macpro.loginyoumenu.R;
import com.youmenu.macpro.loginyoumenu.helper.SQLiteHandlerRestaurant;
import com.youmenu.macpro.loginyoumenu.helper.SessionManager;

import java.util.HashMap;

public class MainRestaurateurActivity extends Activity {

    private static final String TAG = MainRestaurateurActivity.class.getSimpleName();

    private TextView txtName;
    private TextView txtPartitaIva;
    private Button btnLogout;


    private SessionManager session;
    private SQLiteHandlerRestaurant dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_restaurateur);

        txtName = (TextView) findViewById(R.id.name);
        txtPartitaIva = (TextView) findViewById(R.id.partitaIva);
        btnLogout = (Button) findViewById(R.id.btnLogout);


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


        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
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
        Intent intent = new Intent(MainRestaurateurActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
