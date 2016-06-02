package com.alfacast.menyou.restaurant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppConfig;
import com.alfacast.menyou.login.app.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MacPro on 04/05/16.
 */
public class InsertMenuActivity extends AppCompatActivity {
    private static final String TAG = InsertMenuActivity.class.getSimpleName();
    private Button btnInsertMenu;
    private EditText inputNameMenu;
    private ProgressDialog pDialog;
    private SQLiteHandlerMenu db;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_menu_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputNameMenu = (EditText) findViewById(R.id.nameMenu);
        btnInsertMenu = (Button) findViewById(R.id.btnInsertMenu);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandlerMenu(getApplicationContext());

        // Insert Button Click event
        btnInsertMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputNameMenu.getText().toString().trim();

                if (!name.isEmpty()) {
                    insertMenu(name);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter menu name!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void insertMenu(final String name) {
        // Tag used to cancel the request
        String tag_string_req = "req_insert";

        pDialog.setMessage("Insert ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERTMENU, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Insert Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject menu = jObj.getJSONObject("menu");
                        String name = menu.getString("nome");

                        String created_at = menu
                                .getString("created_at");

                        // Inserting row in users table
                        db.addMenu(name, uid, created_at);

                        Toast.makeText(getApplicationContext(), "Menu successfully created.", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                InsertMenuActivity.this,
                                InsertPortataActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Insert Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

