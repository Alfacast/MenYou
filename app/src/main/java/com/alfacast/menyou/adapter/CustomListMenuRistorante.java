package com.alfacast.menyou.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;
import com.alfacast.menyou.model.ListaMenu;
import com.alfacast.menyou.restaurant.PortataActivityRistorante;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * Created by Gabriele Bellissima on 08/06/2016.
 */
public class CustomListMenuRistorante extends BaseAdapter {
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_menu_ristorante, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView nomeMenu = (TextView) convertView.findViewById(R.id.nomemenu);
        TextView nomeRistorante = (TextView) convertView.findViewById(R.id.nomeristorante);
        final TextView idMenu = (TextView) convertView.findViewById(R.id.idmenu);
        ImageButton btnDeleteMenu = (ImageButton) convertView.findViewById(R.id.btnDeleteMenu);
        ImageButton btnItemizeMenu = (ImageButton) convertView.findViewById(R.id.btnItemizeMenu);
        ImageButton btnEditMenu = (ImageButton) convertView.findViewById(R.id.btnEditMenu);

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

        btnDeleteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Delete button Clicked",
                        Toast.LENGTH_LONG).show();
            }
        });

        btnEditMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Edit button Clicked",
                        Toast.LENGTH_LONG).show();
            }
        });

        btnItemizeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send menu id to portata list activity to get list of portate under that menu

                Bundle b= new Bundle();
                b.putString("idmenu", idMenu.getText().toString());
                Intent intent = new Intent(
                        v.getContext(),
                        PortataActivityRistorante.class);
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

}
