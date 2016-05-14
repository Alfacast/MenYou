package com.youmenu.macpro.loginyoumenu.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.youmenu.macpro.loginyoumenu.R;
import com.youmenu.macpro.loginyoumenu.helper.SQLiteHandler;
import com.youmenu.macpro.loginyoumenu.helper.SessionManager;

public class StartActivity extends Activity {
    private static final String TAG = StartActivity.class.getSimpleName();

    private Button btnLoginCliente;
    private Button btnLoginRistorante;
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnLoginCliente = (Button) findViewById(R.id.btnLoginCliente);
        btnLoginRistorante = (Button) findViewById(R.id.btnLoginRistorante);

        session = new SessionManager(getApplicationContext());

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        db = new SQLiteHandler(getApplicationContext());


        // Link to Register Screen Cliente
        btnLoginCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginClientActivity.class);
                startActivity(i);
                finish();
            }
        });

        //Link to Register Screen Ristorante
        btnLoginRistorante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginRestaurantActivity.class);
                startActivity(i);
                finish();
            }
        });





    }
}