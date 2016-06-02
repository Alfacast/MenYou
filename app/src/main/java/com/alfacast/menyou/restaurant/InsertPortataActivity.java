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
public class InsertPortataActivity extends AppCompatActivity {
    private static final String TAG = InsertPortataActivity.class.getSimpleName();
    private Button btnInsertPortata;
    private EditText inputNamePortata;
    private EditText inputCategoriaPortata;
    private EditText inputDescrizionePortata;
    private EditText inputPrezzoPortata;
    private EditText inputOpzioniPortata;
    private EditText inputDisponibilePortata;
    private EditText inputFotoPortata;
    private ProgressDialog pDialog;
    private SQLiteHandlerPortata db;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_portata_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputNamePortata = (EditText) findViewById(R.id.namePortata);
        btnInsertPortata = (Button) findViewById(R.id.btnInsertPortata);
        inputCategoriaPortata = (EditText) findViewById(R.id.categoriaPortata);
        inputDescrizionePortata = (EditText) findViewById(R.id.descrizionePortata);
        inputPrezzoPortata = (EditText) findViewById(R.id.prezzoPortata);
        inputOpzioniPortata = (EditText) findViewById(R.id.opzioniPortata);
        inputDisponibilePortata = (EditText) findViewById(R.id.disponibilePortata);
        inputFotoPortata = (EditText) findViewById(R.id.fotoPortata);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandlerPortata(getApplicationContext());

        // Insert Button Click event
        btnInsertPortata.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputNamePortata.getText().toString().trim();
                String categoria = inputCategoriaPortata.getText().toString().trim();
                String descrizione = inputDescrizionePortata.getText().toString().trim();
                String prezzo = inputPrezzoPortata.getText().toString().trim();
                String opzioni = inputOpzioniPortata.getText().toString().trim();
                String disponibile = inputDisponibilePortata.getText().toString().trim();
                String foto = inputFotoPortata.getText().toString().trim();

                if (!name.isEmpty()) {
                    insertPortata(name, categoria, descrizione, prezzo, opzioni, disponibile, foto);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter portata name!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void insertPortata(final String name, final String categoria, final String descrizione, final String prezzo, final String opzioni, final String disponibile, final String foto) {
        // Tag used to cancel the request
        String tag_string_req = "req_insert";

        pDialog.setMessage("Insert ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERTPORTATA, new Response.Listener<String>() {

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

                        JSONObject portata = jObj.getJSONObject("portata");
                        String name = portata.getString("nome");
                        String categoria = portata.getString("categoria");
                        String descrizione = portata.getString("descrizione");
                        String prezzo = portata.getString("prezzo");
                        String opzioni = portata.getString("opzioni");
                        String disponibile = portata.getString("disponibile");
                        String foto = portata.getString("foto");

                        String created_at = portata
                                .getString("created_at");

                        // Inserting row in users table
                        db.addPortata(name, uid, categoria, descrizione, prezzo, opzioni, disponibile, foto, created_at);

                        Toast.makeText(getApplicationContext(), "Portata successfully created.", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                InsertPortataActivity.this,
                                MainRistoranteActivity.class);
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
                params.put("categoria", categoria);
                params.put("descrizione", descrizione);
                params.put("prezzo", prezzo);
                params.put("opzioni", opzioni);
                params.put("disponibile", disponibile);
                params.put("foto", foto);

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

