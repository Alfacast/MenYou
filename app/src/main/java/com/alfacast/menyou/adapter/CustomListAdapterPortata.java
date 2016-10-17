package com.alfacast.menyou.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfacast.menyou.UrlConfig;
import com.alfacast.menyou.client.PortataActivity;
import com.alfacast.menyou.client.PortataDettaglioActivity;
import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.login.helper.SQLiteHandlerUser;
import com.alfacast.menyou.model.ListaPortata;
import com.alfacast.menyou.restaurant.MainRistoranteActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gabriele Bellissima on 08/06/2016.
 */
public class CustomListAdapterPortata extends BaseAdapter {
    private SQLiteHandlerUser db;

    // Log tag
    private static final String TAG = CustomListAdapterPortata.class.getSimpleName();
    private Activity activity;
    private LayoutInflater inflater;
    private List<ListaPortata> portataItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapterPortata(Activity activity, List<ListaPortata> portataItems) {
        this.activity = activity;
        this.portataItems = portataItems;
    }

    @Override
    public int getCount() {
        return portataItems.size();
    }

    @Override
    public Object getItem(int location) {
        return portataItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_portata, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageView thumbNailPortata = (ImageView) convertView.findViewById(R.id.thumbnailportata);
        TextView nomePortata = (TextView) convertView.findViewById(R.id.nomeportata);
        TextView descrizionePortata = (TextView) convertView.findViewById(R.id.descrizioneportata);
        TextView categoria = (TextView) convertView.findViewById(R.id.categoria);
        TextView prezzo = (TextView) convertView.findViewById(R.id.prezzo);
        TextView idPortata = (TextView) convertView.findViewById(R.id.idportata);
        CheckBox preferiti = (CheckBox) convertView.findViewById(R.id.preferiti);

        // getting portata data for the row
        ListaPortata p = portataItems.get(position);

        db = new SQLiteHandlerUser(convertView.getContext());
        HashMap<String, String> a = db.getUserDetails();
        final String uiduser = a.get("uid");
        final String idportata = p.getIdPortata();

        // decodifica immagine da db
        byte[] decodedString = Base64.decode(String.valueOf(p.getThumbnailPortata()), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        thumbNailPortata.setImageBitmap(decodedByte);

        // nome portata
        nomePortata.setText(p.getNomePortata());

        // descrizione portata
        descrizionePortata.setText(p.getDescrizionePortata());

        // categoria
        categoria.setText("Categoria: " + String.valueOf(p.getCategoria()));

        // prezzo portata
        prezzo.setText("Prezzo: € " + String.valueOf(p.getPrezzo()));

        idPortata.setText(p.getIdPortata());

        if(p.isCheckbox()){
            preferiti.setChecked(true);
        }
        else {
            preferiti.setChecked(false);
        }

        preferiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()){
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

                                } else {

                                    // Error occurred in registration. Get the error
                                    // message
                                    String errorMsg = jObj.getString("error_msg");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Insert Error: " + error.getMessage());
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
                } else {
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

        return convertView;
    }
}
