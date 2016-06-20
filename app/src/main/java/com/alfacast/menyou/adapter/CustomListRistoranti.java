package com.alfacast.menyou.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfacast.menyou.login.R;
import com.alfacast.menyou.login.app.AppController;

import com.alfacast.menyou.model.ListaMenu;
import com.alfacast.menyou.model.ListaRistoranti;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * Created by ANLE on 20/06/2016.
 */
public class CustomListRistoranti extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ListaRistoranti> ristorantiItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListRistoranti(Activity activity, List<ListaRistoranti> ristorantiItems) {
        this.activity = activity;
        this.ristorantiItems =ristorantiItems;
    }

    @Override
    public int getCount() {
        return ristorantiItems.size();
    }

    @Override
    public Object getItem(int location) {
        return ristorantiItems.get(location);
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
            convertView = inflater.inflate(R.layout.list_row_ristorante, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView nome = (TextView) convertView.findViewById(R.id.nome);
        TextView indirizzo = (TextView) convertView.findViewById(R.id.indirizzo);
        TextView idRistorante = (TextView) convertView.findViewById(R.id.idristorante);
        TextView telefono = (TextView) convertView.findViewById(R.id.telefono);

        // getting ristoranti data for the row
        ListaRistoranti m = ristorantiItems.get(position);

        byte[] decodedString = Base64.decode(String.valueOf(m.getThumbnail()), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        thumbNail.setImageBitmap(decodedByte);

        nome.setText(m.getNome());

        indirizzo.setText(m.getIndirizzo());

        idRistorante.setText(m.getIdRistorante());

        telefono.setText(m.getTelefono());

        return convertView;
    }


}
