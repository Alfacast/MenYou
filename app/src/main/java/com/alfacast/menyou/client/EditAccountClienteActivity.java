package com.alfacast.menyou.client;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppConfig;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.login.helper.SQLiteHandlerRestaurant;
import com.alfacast.menyou.login.helper.SQLiteHandlerUser;
import com.alfacast.menyou.login.helper.SessionManager;
import com.alfacast.menyou.restaurant.MainRistoranteActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditAccountClienteActivity extends AppCompatActivity {

    private static final String TAG = EditAccountClienteActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private Button btnEdit;
    private TextView uidCliente;
    private EditText nameCliente;
    private EditText clienteEmail;
    private EditText clientePassword;

    private SQLiteHandlerUser db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_edit_account_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uidCliente = (TextView) findViewById(R.id.uid);
        nameCliente = (EditText) findViewById(R.id.nameCliente);
        clienteEmail = (EditText) findViewById(R.id.ClienteEmail);
        clientePassword = (EditText) findViewById(R.id.ClientePassword);
        btnEdit = (Button) findViewById(R.id.btnEditAccount);

        // SqLite database handler
        db = new SQLiteHandlerUser(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String uid = user.get("uid");
        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        uidCliente.setText(uid);
        nameCliente.setText(name);
        clienteEmail.setText(email);

        // Update Button Click event
        btnEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String uid = uidCliente.getText().toString().trim();
                String nome = nameCliente.getText().toString().trim();
                String email = clienteEmail.getText().toString().trim();
                String password = clientePassword.getText().toString().trim();

                if (!uid.isEmpty() && !nome.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    editCliente(uid, nome, email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    private void editCliente(final String uid, final String nome, final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_USERUPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("nome");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                        db.updateUser(name, email, uid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully update!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                EditAccountClienteActivity.this,
                                MainClienteActivity.class);
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
                params.put("uid", uid);
                params.put("nome", nome);
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
