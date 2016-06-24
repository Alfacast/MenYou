package com.alfacast.menyou.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Pietro Fantuzzi on 22/06/2016.
 */
public class RistoranteDettaglioActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG = RistoranteDettaglioActivity.class.getSimpleName();

    // Portata json url
    private static final String url = "http://www.cinesofia.it/alfacast/youmenulogin/get_ristorante_dettaglio.php?idristorante=";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dettaglio_ristorante_activity);

        // recupero id ristorante dalla activity precedente
        Intent intent=getIntent();
        Bundle b=intent.getExtras();

        final String idristorante=b.getString("idristorante");
        Log.d(TAG,"id ristorante: "+ idristorante);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final ImageView foto = (ImageView) findViewById(R.id.foto);
        final TextView nome = (TextView) findViewById(R.id.nome);
        final TextView indirizzo = (TextView) findViewById(R.id.indirizzo);
        final TextView telefono = (TextView) findViewById(R.id.telefono);
        final TextView sito_web = (TextView) findViewById(R.id.sito_web);
        final TextView email = (TextView) findViewById(R.id.email);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        // Creating volley request obj
        JsonArrayRequest portataReq = new JsonArrayRequest(url+idristorante,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        try {

                            JSONObject obj = response.getJSONObject(0);

                            nome.setText(obj.getString("nome"));
                            indirizzo.setText(obj.getString("indirizzo"));
                            telefono.setText(obj.getString("telefono"));
                            email.setText(obj.getString("email"));
                            sito_web.setText(obj.getString("sito_web"));

                            byte[] decodedString = Base64.decode(String.valueOf(obj.getString("foto")), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            foto.setImageBitmap(decodedByte);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }

        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(portataReq);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}
