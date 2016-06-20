package com.alfacast.menyou.restaurant;

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
import com.alfacast.menyou.login.helper.SQLiteHandlerRestaurant;
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
    private SQLiteHandlerRestaurant dbr;



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
                String nome = inputNameMenu.getText().toString().trim();

                // recupero id dalla tabella ristorante
                dbr = new SQLiteHandlerRestaurant(getApplicationContext());
                HashMap<String, String> a = dbr.getUserDetails();
                final String id_ristorante = a.get("id_ristorante");

                Log.d(TAG, "id data è: " + id_ristorante);

                if (!nome.isEmpty()) {
                    insertMenu(nome, id_ristorante);
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
    private void insertMenu(final String nome, final String id_ristorante) {
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
                        String id = menu.getString("id");
                        String nome = menu.getString("nome");
                        String id_ristorante = menu.getString("id_ristorante");

                        String created_at = menu
                                .getString("created_at");

                        // Inserting row in menu table
                        db.addMenu(nome, uid, created_at, id_ristorante);

                        Toast.makeText(getApplicationContext(), "Menu successfully created.", Toast.LENGTH_LONG).show();

                        // Launch Insert portata activity
                        Bundle b= new Bundle();
                        b.putString("idmenu", id);
                        Intent intent = new Intent(
                                InsertMenuActivity.this,
                                InsertPortataActivity.class);
                        intent.putExtras(b);
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
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nome", nome);
                params.put("id_ristorante", id_ristorante);

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

