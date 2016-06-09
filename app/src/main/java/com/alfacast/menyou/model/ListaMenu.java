package com.alfacast.menyou.model;

import android.media.Image;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Gabriele Bellissima on 08/06/2016.
 */
public class ListaMenu {
    private String nomeMenu;
    private String thumbnail;
    private String nomeRistorante;

    public ListaMenu() {
    }

    public ListaMenu(String nomeMenu, String thumbnail, String nomeRistorante) {
        this.nomeMenu = nomeMenu;
        this.thumbnail = thumbnail;
        this.nomeRistorante = nomeRistorante;
    }

    public String getNomeMenu() {
        return nomeMenu;
    }

    public void setNomeMenu(String nomeMenu) {
        this.nomeMenu = nomeMenu;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getNomeRistorante() {
        return nomeRistorante;
    }

    public void setNomeRistorante(String nomeRistorante) {
        this.nomeRistorante = nomeRistorante;
    }

}
