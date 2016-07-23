package com.alfacast.menyou.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfacast.menyou.UrlConfig;
import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.model.ListaMenu;
import com.alfacast.menyou.restaurant.EditMenuActivity;
import com.alfacast.menyou.restaurant.MainRistoranteActivity;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Gabriele Bellissima on 08/06/2016.
 */
public class CustomListMenuRistorante extends BaseAdapter {

    // Log tag
    private static final String TAG = CustomListMenuRistorante.class.getSimpleName();

    private Activity activity;
    private LayoutInflater inflater;
    private List<ListaMenu> menuItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListMenuRistorante(Activity activity, List<ListaMenu> menuItems) {
        this.activity = activity;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int location) {
        return menuItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_menu_ristorante, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        final TextView nomeMenu = (TextView) convertView.findViewById(R.id.nomemenu);
        TextView nomeRistorante = (TextView) convertView.findViewById(R.id.nomeristorante);
        final TextView idMenu = (TextView) convertView.findViewById(R.id.idmenu);
        Button btnDeleteMenu = (Button) convertView.findViewById(R.id.btnDeleteMenu);
        Button btnEditMenu = (Button) convertView.findViewById(R.id.btnEditMenu);

        // getting menu data for the row
        ListaMenu m = menuItems.get(position);

        //Decodifica immmagine da db
        byte[] decodedString = Base64.decode(String.valueOf(m.getThumbnail()), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        thumbNail.setImageBitmap(decodedByte);

        // Set immagine senza decodifica
        //thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // nome menu
        nomeMenu.setText(m.getNomeMenu());

        // nome ristorante
        nomeRistorante.setText("Ristorante: " + String.valueOf(m.getNomeRistorante()));

        idMenu.setText(m.getIdMenu());

        //On click del bottone delete
        btnDeleteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        v.getContext());

                // set title
                alertDialogBuilder.setTitle("Elimina Menu");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Sei sicuro di voler eliminare il menu?")
                        .setCancelable(false)
                        .setPositiveButton("Si",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // se cliccato elimino il menu e lancio la mainactivity
                                final String idDelMenu = idMenu.getText().toString();
                                Log.d(TAG,"id menu: "+ idDelMenu);
                                JsonArrayRequest menuReq = new JsonArrayRequest(UrlConfig.URL_ListMenuRistorante+idDelMenu,
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
                                AppController.getInstance().addToRequestQueue(menuReq);

                                //lancio la mainactivity
                                Intent i = new Intent(v.getContext(),
                                        MainRistoranteActivity.class);
                                v.getContext().startActivity(i);
                                ((Activity)v.getContext()).finish();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
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

        //On click del bottone edit
        btnEditMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b= new Bundle();
                b.putString("nomemenu", nomeMenu.getText().toString());

                Bundle c= new Bundle();
                c.putString("idmenu", idMenu.getText().toString());


                Intent intent = new Intent(
                        v.getContext(),
                        EditMenuActivity.class);
                intent.putExtras(b);
                intent.putExtras(c);

                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

}
