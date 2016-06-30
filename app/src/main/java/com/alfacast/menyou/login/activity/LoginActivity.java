package com.alfacast.menyou.login.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alfacast.menyou.client.MainClienteActivity;
import com.alfacast.menyou.restaurant.MainRistoranteActivity;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.appevents.AppEventsLogger;
import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppConfig;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.login.helper.SQLiteHandlerUser;
import com.alfacast.menyou.login.helper.SQLiteHandlerRestaurant;
import com.alfacast.menyou.login.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private Button btnRegisterRestaurateur; //Button for Restaurateur
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandlerUser db;
    private SQLiteHandlerRestaurant dbr;

    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.login_activity);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        btnRegisterRestaurateur = (Button) findViewById(R.id.btnLinkToRegisterRestaurateur);

        //Button login con Facebook
        loginButton = (LoginButton)findViewById(R.id.login_button);

            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    /*Intent intent = new Intent(LoginActivity.this,MainClienteActivity.class);
                    startActivity(intent);*/
                    Toast.makeText(getApplicationContext(),"User ID:  " +
                            loginResult.getAccessToken().getUserId() + "\n" +
                            "Auth Token: " + loginResult.getAccessToken().getToken(), Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(),
                            "Tentativo di login annullato", Toast.LENGTH_LONG)
                            .show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(getApplicationContext(),
                            "Tentativo di login fallito", Toast.LENGTH_LONG)
                            .show();
                }
            });

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandlerUser(getApplicationContext());
        dbr = new SQLiteHandlerRestaurant(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        Log.d(TAG,"database ristorante"+dbr.getUserDetails().toString());
        Log.d(TAG,"database cliente"+db.getUserDetails().toString());
        if (session.isLoggedIn()) {
            if(dbr.getUserDetails().toString().contains("partitaIva")) {
                // Ristorante is already logged in. Take him to main activity
                Intent intent = new Intent(LoginActivity.this, MainRistoranteActivity.class);
                startActivity(intent);
                finish();
            }else{
                // User is already logged in. Take him to main activity
                Intent intent = new Intent(LoginActivity.this, MainClienteActivity.class);
                startActivity(intent);
                finish();
            }
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Inserire i dati!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterUserActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Link to Register Restaurateur
        btnRegisterRestaurateur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterRestaurateurActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login"; //??

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        Log.d(TAG, "Il json è: " + jObj.toString());

                        JSONObject user = jObj.getJSONObject("user");

                        String id_ristorante = user.getString("id");
                        String name = user.getString("name");
                        String address = user.getString("address");
                        String partitaIva = user.getString("partitaIva");
                        String sitoWeb = user.getString("sitoWeb");
                        String email = user.getString("email");
                        String tel = user.getString("telefono");
                        String foto = user.getString("foto");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table

                        if(partitaIva.toString() == "null"){
                            Log.d(TAG,"partitaIva è null ");
                            db.addUser(name, email, uid, created_at);
                            // Launch main activity
                            Intent intent = new Intent(LoginActivity.this,
                                    MainClienteActivity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            Log.d(TAG,"partitaIva non è null ");
                            dbr.addUser(id_ristorante, name, address, partitaIva, sitoWeb, email, tel, foto, uid, created_at);
                            // Launch main activity ristorante
                            Intent intent = new Intent(LoginActivity.this,
                                    MainRistoranteActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        Log.d(TAG,"partita iva è: " + partitaIva.toString());


                    } else {
                        // Error in login. Get the error message

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
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