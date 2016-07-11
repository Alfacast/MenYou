package com.alfacast.menyou.restaurant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

import java.io.ByteArrayOutputStream;

public class PortataDettaglioRistoranteActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG = PortataDettaglioRistoranteActivity.class.getSimpleName();

    // Portata json url
    private static final String url = "http://www.cinesofia.it/alfacast/youmenulogin/get_portata_dettaglio.php?idportata=";
    private static final String urldel = "http://www.cinesofia.it/alfacast/youmenulogin/delete_portata.php?idportata=";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portata_dettaglio_ristorante_activity);

        // recupero id portata dalla activity precedente
        Intent intent=getIntent();
        Bundle b=intent.getExtras();
        Bundle c=intent.getExtras();

        final String idportata=b.getString("idportata");
        Log.d(TAG,"id portata: "+ idportata);

        final String idmenu=c.getString("idmenu");
        Log.d(TAG,"id menu: "+ idmenu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final ImageView foto = (ImageView) findViewById(R.id.foto);
        final ImageView thumbNailPortata = (ImageView) findViewById(R.id.thumbnailportata);
        final TextView nomePortata = (TextView) findViewById(R.id.nomeportata);
        final TextView nomeRistorante = (TextView) findViewById(R.id.nomeristorante);
        final TextView indirizzo = (TextView) findViewById(R.id.indirizzo);
        final TextView telefono = (TextView) findViewById(R.id.telefono);
        final TextView descrizionePortata = (TextView) findViewById(R.id.descrizioneportata);
        final TextView categoria = (TextView) findViewById(R.id.categoria);
        final TextView prezzo = (TextView) findViewById(R.id.prezzo);
        final TextView idPortata = (TextView) findViewById(R.id.idportata);
        final ImageButton btnDeletePortata = (ImageButton) findViewById(R.id.btnDeletePortata);
        final TextView opzioniPortata = (TextView)findViewById(R.id.opzioniPortata);

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
                            indirizzo.setText(obj.getString("indirizzo"));
                            telefono.setText(obj.getString("telefono"));
                            idPortata.setText(obj.getString("id"));
                            opzioniPortata.setText("Opzioni: "+obj.getString("opzioni"));

                            //Decodifica immagine da db
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

        btnDeletePortata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        view.getContext());

                // set title
                alertDialogBuilder.setTitle("Elimina Portata");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Sei sicuro di voler eliminare la portata?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // Creating volley request obj
                                final JsonArrayRequest portataReq = new JsonArrayRequest(urldel + idportata,

                                        new Response.Listener<JSONArray>() {

                                            @Override
                                            public void onResponse(JSONArray response) {
                                                Log.d(TAG, response.toString());

                                                // Parsing json
                                                try {

                                                    JSONObject obj = response.getJSONObject(0);

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
                                AppController.getInstance().addToRequestQueue(portataReq);

                                Bundle b = new Bundle();
                                b.putString("idmenu", idmenu);

                                //lancio la portata activity ristorante
                                Intent i = new Intent(view.getContext(),
                                        PortataActivityRistorante.class);
                                i.putExtras(b);
                                view.getContext().startActivity(i);
                                ((Activity) view.getContext()).finish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabedit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //lancio edit portata

                Bundle c= new Bundle();
                c.putString("nomeportata", nomePortata.getText().toString());

                Bundle d= new Bundle();
                d.putString("descrizioneportata", descrizionePortata.getText().toString());

                Bundle e= new Bundle();
                e.putString("prezzoportata", prezzo.getText().toString());

                Bundle f= new Bundle();
                f.putString("opzioni", opzioniPortata.getText().toString());

                foto.buildDrawingCache();
                Bitmap bitmap = foto.getDrawingCache();
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
                final byte[] image=stream.toByteArray();
                String fotoPortata = Base64.encodeToString(image, Base64.NO_WRAP);

                Bundle g= new Bundle();
                g.putString("decodedStringFoto", fotoPortata);

                Bundle h= new Bundle();
                h.putString("idportata", idPortata.getText().toString());

                Intent i = new Intent(view.getContext(),
                        EditPortataActivity.class);

                i.putExtras(c);
                i.putExtras(d);
                i.putExtras(e);
                i.putExtras(f);
                i.putExtras(g);
                i.putExtras(h);

                view.getContext().startActivity(i);
                ((Activity)view.getContext()).finish();

            }
        });
    }

    private void showDialog() {
        if (pDialog.isShowing())
            pDialog.show();
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
