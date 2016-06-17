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
import com.alfacast.menyou.model.ListaPortata;
import com.alfacast.menyou.model.PortataDettaglio;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * Created by Gabriele Bellissima on 08/06/2016.
 */
public class CustomListAdapterPortataDettaglio extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<PortataDettaglio> portataItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapterPortataDettaglio(Activity activity, List<PortataDettaglio> portataItems) {
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
            convertView = inflater.inflate(R.layout.list_row_portata_dettaglio, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageView foto = (ImageView) convertView.findViewById(R.id.foto);
        ImageView thumbNailPortata = (ImageView) convertView.findViewById(R.id.thumbnailportata);
        TextView nomePortata = (TextView) convertView.findViewById(R.id.nomeportata);
        TextView nomeRistorante = (TextView) convertView.findViewById(R.id.nomeristorante);
        TextView indirizzo = (TextView) convertView.findViewById(R.id.indirizzo);
        TextView telefono = (TextView) convertView.findViewById(R.id.telefono);
        TextView descrizionePortata = (TextView) convertView.findViewById(R.id.descrizioneportata);
        TextView categoria = (TextView) convertView.findViewById(R.id.categoria);
        TextView prezzo = (TextView) convertView.findViewById(R.id.prezzo);
        TextView idPortata = (TextView) convertView.findViewById(R.id.idportata);

        // getting menu data for the row
        PortataDettaglio p = portataItems.get(position);

        byte[] decodedString = Base64.decode(String.valueOf(p.getThumbnailPortata()), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        thumbNailPortata.setImageBitmap(decodedByte);

        byte[] decodedStringFoto = Base64.decode(String.valueOf(p.getFoto()), Base64.DEFAULT);
        Bitmap decodedByteFoto = BitmapFactory.decodeByteArray(decodedStringFoto, 0, decodedStringFoto.length);

        foto.setImageBitmap(decodedByteFoto);

        // nome portata
        nomePortata.setText(p.getNomePortata());

        // descrizione portata
        descrizionePortata.setText(p.getDescrizionePortata());

        // categoria
        categoria.setText("Categoria: " + String.valueOf(p.getCategoria()));

        // prezzo portata
        prezzo.setText("Prezzo: € " + String.valueOf(p.getPrezzo()));

        // Nome ristorante
        nomeRistorante.setText("Ristorante: " + String.valueOf(p.getNomeRistorante()));

        // indirizzo
        indirizzo.setText("Indirizzo: " + String.valueOf(p.getIndirizzo()));

        // telefono
        telefono.setText("Telefono: " + String.valueOf(p.getTelefono()));

        idPortata.setText(p.getIdPortata());

        return convertView;
    }

}
