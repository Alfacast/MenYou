package com.alfacast.menyou.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alfacast.menyou.client.PortataDettaglioActivity;
import com.alfacast.menyou.login.R;
import com.alfacast.menyou.model.ListaPortataCerca;

import java.util.Collections;
import java.util.List;

/**
 * Created by Gabriele Bellissima on 08/06/2016.
 */
public class CustomListAdapterPortataCerca extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<ListaPortataCerca> data= Collections.emptyList();
    ListaPortataCerca current;
    int currentPos=0;

    public CustomListAdapterPortataCerca(Context context, List<ListaPortataCerca> data) {
        this.context = context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.list_row_cerca, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        ListaPortataCerca current=data.get(position);
        myHolder.nomePortata.setText(current.nomePortata);
        myHolder.descrizionePortata.setText("Descrizione: " + current.descrizionePortata);
        myHolder.categoria.setText("Categoria: " + current.categoria);
        myHolder.prezzo.setText("Prezzo € " + current.prezzo);
        myHolder.idPortata.setText(current.idPortata);

        byte[] decodedString = Base64.decode(String.valueOf(current.thumbnailPortata), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        myHolder.thumbNailPortata.setImageBitmap(decodedByte);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView thumbNailPortata;
        TextView nomePortata;
        TextView descrizionePortata;
        TextView categoria;
        TextView prezzo;
        TextView idPortata;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            thumbNailPortata = (ImageView) itemView.findViewById(R.id.thumbnailportata);
            nomePortata = (TextView) itemView.findViewById(R.id.nomeportata);
            descrizionePortata = (TextView) itemView.findViewById(R.id.descrizioneportata);
            categoria = (TextView) itemView.findViewById(R.id.categoria);
            prezzo = (TextView) itemView.findViewById(R.id.prezzo);
            idPortata = (TextView) itemView.findViewById(R.id.idportata);
            itemView.setOnClickListener(this);
        }

        // Click event for all items
        @Override
        public void onClick(View v) {

            String id = ((TextView) v.findViewById(R.id.idportata)).getText().toString();

            Bundle b= new Bundle();
            b.putString("idportata", id);
            Intent intent = new Intent(
                    v.getContext(),
                    PortataDettaglioActivity.class);
            intent.putExtras(b);
            v.getContext().startActivity(intent);

        }

    }

}
