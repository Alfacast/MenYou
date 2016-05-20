package com.youmenu.macpro.loginyoumenu.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.youmenu.macpro.loginyoumenu.R;
import com.youmenu.macpro.loginyoumenu.app.AppConfig;
import com.youmenu.macpro.loginyoumenu.app.AppController;
import com.youmenu.macpro.loginyoumenu.helper.SQLiteHandlerRestaurant;
import com.youmenu.macpro.loginyoumenu.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MacPro on 04/05/16.
 */
public class RegisterRestaurateurActivity extends Activity {
    private static final String TAG = RegisterRestaurateurActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputNameRestaurant;
    private EditText inputAddress;
    private EditText inputPartitaIva;
    private EditText inputRestaurantEmail;
    private EditText inputRestaurantTel;
    private EditText inputRestaurantPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandlerRestaurant db;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_restaurateur);

        inputNameRestaurant = (EditText) findViewById(R.id.nameRestaurant);
        inputAddress = (EditText) findViewById(R.id.RestaurantAddress);
        inputPartitaIva = (EditText) findViewById(R.id.PartitaIva);
        inputRestaurantEmail = (EditText) findViewById(R.id.RestaurantEmail);
        inputRestaurantTel = (EditText) findViewById(R.id.RestaurantTel);
        inputRestaurantPassword = (EditText) findViewById(R.id.RestaurantPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandlerRestaurant(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterRestaurateurActivity.this,
                    MainUserActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputNameRestaurant.getText().toString().trim();
                String address = inputAddress.getText().toString().trim();
                String partitaIva = inputPartitaIva.getText().toString().trim();
                String email = inputRestaurantEmail.getText().toString().trim();
                String tel = inputRestaurantTel.getText().toString().trim();
                String password = inputRestaurantPassword.getText().toString().trim();

                if (!name.isEmpty() && !address.isEmpty() && !partitaIva.isEmpty() && !email.isEmpty() && !tel.isEmpty() && !password.isEmpty()) {
                    registerRestaurant(name, address, partitaIva, email, tel, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerRestaurant(final String name, final String address, final String partitaIva,final String email, final String tel, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RESTAURANTREGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String address = user.getString("address");
                        String partitaIva = user.getString("partitaIva");
                        String email = user.getString("email");
                        String tel = user.getString("telefono");

                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, address, partitaIva, email, tel, uid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterRestaurateurActivity.this,
                                LoginActivity.class);
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
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
                params.put("address", address);
                params.put("partitaIva", partitaIva);
                params.put("email", email);
                params.put("telefono", tel);
                params.put("password", password);

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

