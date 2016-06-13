package com.alfacast.menyou.model;

/**
 * Created by Gabriele Bellissima on 08/06/2016.
 */
public class ListaPortata {
    private String nomePortata;
    private String thumbnailPortata;
    private String descrizionePortata;
    private String categoria;
    private String prezzo;
    private String idPortata;

    public ListaPortata() {
    }

    public ListaPortata(String nomePortata, String thumbnailPortata, String descrizionePortata, String idPortata, String categoria, String prezzo) {
        this.nomePortata = nomePortata;
        this.thumbnailPortata = thumbnailPortata;
        this.descrizionePortata = descrizionePortata;
        this.categoria = categoria;
        this.prezzo = prezzo;
        this.idPortata = idPortata;
    }

    public String getNomePortata() {
        return nomePortata;
    }

    public void setNomePortata(String nomePortata) {
        this.nomePortata = nomePortata;
    }

    public String getThumbnailPortata() {
        return thumbnailPortata;
    }

    public void setThumbnailPortata(String thumbnailPortata) {
        this.thumbnailPortata = thumbnailPortata;
    }

    public String getDescrizionePortata() {
        return descrizionePortata;
    }

    public void setDescrizionePortata(String descrizionePortata) {
        this.descrizionePortata = descrizionePortata;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(String prezzo) {
        this.prezzo = prezzo;
    }

    public String getIdPortata() {
        return idPortata;
    }

    public void setIdPortata(String idPortata) {
        this.idPortata = idPortata;
    }

}
