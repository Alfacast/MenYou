package com.youmenu.macpro.loginyoumenu.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.youmenu.macpro.loginyoumenu.R;
import com.youmenu.macpro.loginyoumenu.helper.SQLiteHandler;
import com.youmenu.macpro.loginyoumenu.helper.SQLiteHandlerRestaurant;
import com.youmenu.macpro.loginyoumenu.helper.SessionManager;

import java.util.HashMap;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;

    private SQLiteHandler db;
    private SessionManager session;
    private SQLiteHandlerRestaurant dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        dbr = new SQLiteHandlerRestaurant(getApplicationContext());



        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }


        // Recuperare dati utente da SQLite
        HashMap<String, String> user = dbr.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");
        String partitaIva = user.get("partitaIva");

        Log.d(TAG, "db cliente");


        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(partitaIva);



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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}