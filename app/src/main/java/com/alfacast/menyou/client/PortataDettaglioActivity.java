package com.alfacast.menyou.client;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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

public class PortataDettaglioActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG = PortataDettaglioActivity.class.getSimpleName();

    // Portata json url
    private static final String url = "http://www.cinesofia.it/alfacast/youmenulogin/get_portata_dettaglio.php?idportata=";
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(nomeportata);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final ImageView foto = (ImageView) findViewById(R.id.foto);
        final ImageView thumbNailPortata = (ImageView) findViewById(R.id.thumbnailportata);
        final TextView nomePortata = (TextView) findViewById(R.id.nomeportata);
        final TextView nomeRistorante = (TextView) findViewById(R.id.nomeristorante);
        final TextView idRistorante = (TextView) findViewById(R.id.idristorante);
        final TextView indirizzo = (TextView) findViewById(R.id.indirizzo);
        final TextView telefono = (TextView) findViewById(R.id.telefono);
        final TextView descrizionePortata = (TextView) findViewById(R.id.descrizioneportata);
        final TextView categoria = (TextView) findViewById(R.id.categoria);
        final TextView prezzo = (TextView) findViewById(R.id.prezzo);
        final TextView idPortata = (TextView) findViewById(R.id.idportata);

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

        // Creating volley request obj
        JsonArrayRequest portataReq = new JsonArrayRequest(url+idportata,

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
