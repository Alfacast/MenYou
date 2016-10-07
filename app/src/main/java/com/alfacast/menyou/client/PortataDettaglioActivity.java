package com.alfacast.menyou.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfacast.menyou.UrlConfig;
import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.login.helper.SQLiteHandlerUser;
import com.alfacast.menyou.restaurant.InsertMenuActivity;
import com.alfacast.menyou.restaurant.InsertPortataActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.StringRequest;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.util.HashMap;
import java.util.Map;

import static com.alfacast.menyou.login.R.drawable.preferiti;

public class PortataDettaglioActivity extends AppCompatActivity {

    private ShareDialog mShareDialog;
    private SQLiteHandlerUser db;

    // Log tag
    private static final String TAG = PortataDettaglioActivity.class.getSimpleName();

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portata_dettaglio_activity);

        // recupero id portata dalla activity precedente
        Intent intent=getIntent();
        Bundle b=intent.getExtras();
        Bundle c=intent.getExtras();

        final String idportata=b.getString("idportata");
        final String nomeportata=c.getString("nomeportata");
        Log.d(TAG,"id portata: "+ idportata);

        db = new SQLiteHandlerUser(getApplicationContext());
        HashMap<String, String> a = db.getUserDetails();
        final String uiduser = a.get("uid");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(nomeportata);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mShareDialog = new ShareDialog(this);

        final ImageView foto = (ImageView) findViewById(R.id.foto);
        final ImageView thumbNailPortata = (ImageView) findViewById(R.id.thumbnailportata);
        final TextView nomePortata = (TextView) findViewById(R.id.nomeportata);
        final TextView nomeRistorante = (TextView) findViewById(R.id.nomeristorante);
        final TextView idRistorante = (TextView) findViewById(R.id.idristorante);
        final TextView indirizzo = (TextView) findViewById(R.id.indirizzo);
        final TextView telefono = (TextView) findViewById(R.id.telefono);
        final TextView sitoWeb = (TextView) findViewById(R.id.sitoweb);
        final TextView descrizionePortata = (TextView) findViewById(R.id.descrizioneportata);
        final TextView categoria = (TextView) findViewById(R.id.categoria);
        final TextView prezzo = (TextView) findViewById(R.id.prezzo);
        final TextView idPortata = (TextView) findViewById(R.id.idportata);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final CheckBox preferiti = (CheckBox) findViewById(R.id.preferiti);
        preferiti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (preferiti.isChecked()){
                    insertPreferito(idportata,uiduser);
                }else{
                    JsonArrayRequest prefReq = new JsonArrayRequest(UrlConfig.URL_DeletePreferito+idportata+"&uiduser="+uiduser,
                            new Response.Listener<JSONArray>() {

                                @Override
                                public void onResponse(JSONArray response) {
                                    Log.d(TAG, response.toString());

                                    // Parsing json
                                    try {

                                        JSONObject obj = response.getJSONObject(0);

                                        String message = obj.getString("message");
                                        Log.d(TAG,"messaggio json: "+ message);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());

                        }

                    });
                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(prefReq);
                }
            }
        });

        final ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentTitle = nomePortata.getText().toString()+" al "+nomeRistorante.getText().toString();
                if (sitoWeb.getText().toString().equals("")) {
                    sitoWeb.setText(UrlConfig.URL_Dominio);
                }
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentTitle(contentTitle)
                        .setContentDescription(descrizionePortata.getText().toString())
                        .setContentUrl(Uri.parse(sitoWeb.getText().toString()))
                        .build();
                mShareDialog.show(content);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contentTitle = nomePortata.getText().toString()+" al "+nomeRistorante.getText().toString();

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, contentTitle+" "+sitoWeb.getText().toString());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, descrizionePortata.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Condividi con"));

            }
        });

        nomeRistorante.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Bundle b= new Bundle();
                Bundle c= new Bundle();
                b.putString("idristorante", idRistorante.getText().toString());
                c.putString("nomeristorante", nomeRistorante.getText().toString());
                Intent intent = new Intent(PortataDettaglioActivity.this, RistoranteDettaglioActivity.class);
                intent.putExtras(b);
                intent.putExtras(c);
                startActivity(intent);
            }
        });

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        getPreferito(idportata,uiduser);

        // Creating volley request obj
        JsonArrayRequest portataReq = new JsonArrayRequest(UrlConfig.URL_PortataDettaglioActivity+idportata,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                            try {

                                JSONObject obj = response.getJSONObject(0);

                                nomePortata.setText(obj.getString("nome"));
                                descrizionePortata.setText(obj.getString("descrizione"));
                                categoria.setText("Categoria: "+obj.getString("categoria"));
                                prezzo.setText("Prezzo: € "+obj.getString("prezzo"));
                                nomeRistorante.setText("Ristorante: "+obj.getString("nomeristorante"));
                                idRistorante.setText(obj.getString("idristorante"));
                                indirizzo.setText(obj.getString("indirizzo"));
                                telefono.setText(obj.getString("telefono"));
                                sitoWeb.setText(obj.getString("sitoweb"));
                                idPortata.setText(obj.getString("id"));

                                //decodifica immagine da db
                                byte[] decodedString = Base64.decode(String.valueOf(obj.getString("foto")), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                thumbNailPortata.setImageBitmap(decodedByte);

                                byte[] decodedStringFoto = Base64.decode(String.valueOf(obj.getString("fotoportata")), Base64.DEFAULT);
                                Bitmap decodedByteFoto = BitmapFactory.decodeByteArray(decodedStringFoto, 0, decodedStringFoto.length);

                                foto.setImageBitmap(decodedByteFoto);

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

    private void getPreferito(final String idportata, final String uiduser) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                UrlConfig.URL_GetPreferiti, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        JSONObject preferito = jObj.getJSONObject("preferito");
                        String id = preferito.getString("id_portata");
                        Log.d(TAG,"id portata activity: "+idportata);

                        CheckBox preferiti = (CheckBox) findViewById(R.id.preferiti);
                        if (id.equals(idportata)){
                            preferiti.setChecked(true);
                        }

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
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("idportata", idportata);
                params.put("uiduser", uiduser);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void insertPreferito(final String idportata, final String uiduser) {
        // Tag used to cancel the request
        String tag_string_req = "req_insert";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                UrlConfig.URL_InsertPreferito, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Insert Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        JSONObject preferito = jObj.getJSONObject("preferito");
                        String id_portata = preferito.getString("id_portata");
                        String uid_user = preferito.getString("uid_user");

                        Toast.makeText(getApplicationContext(), "Portata aggiunta ai preferiti.", Toast.LENGTH_LONG).show();

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
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("idportata", idportata);
                params.put("uiduser", uiduser);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
