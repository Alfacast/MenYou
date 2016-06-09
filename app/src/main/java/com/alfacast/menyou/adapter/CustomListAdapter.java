package com.alfacast.menyou.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by Gabriele Bellissima on 08/06/2016.
 */
public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ListaMenu> menuItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<ListaMenu> menuItems) {
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
            convertView = inflater.inflate(R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView nomeMenu = (TextView) convertView.findViewById(R.id.nomemenu);
        TextView nomeRistorante = (TextView) convertView.findViewById(R.id.nomeristorante);

        // getting menu data for the row
        ListaMenu m = menuItems.get(position);

        byte[] decodedString = Base64.decode(String.valueOf(m.getThumbnail()), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        thumbNail.setImageBitmap(decodedByte);

        // thumbnail image
        //thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // nome menu
        nomeMenu.setText(m.getNomeMenu());

        // nome ristorante
        nomeRistorante.setText("Ristorante: " + String.valueOf(m.getNomeRistorante()));

        return convertView;
    }

}
