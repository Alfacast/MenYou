package com.alfacast.menyou.model;

/**
 * Created by Gabriele Bellissima on 08/06/2016.
 */
public class PortataDettaglio {
    private String foto;
    private String nomePortata;
    private String nomeRistorante;
    private String indirizzo;
    private String telefono;
    private String thumbnailPortata;
    private String descrizionePortata;
    private String categoria;
    private String prezzo;
    private String idPortata;

    public PortataDettaglio() {
    }

    public PortataDettaglio(String foto, String nomePortata, String nomeRistorante, String indirizzo, String telefono, String thumbnailPortata, String descrizionePortata, String idPortata, String categoria, String prezzo) {
        this.foto = foto;
        this.nomePortata = nomePortata;
        this.nomeRistorante = nomeRistorante;
        this.indirizzo = indirizzo;
        this.telefono = telefono;
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

    public String getNomeRistorante() {
        return nomeRistorante;
    }

    public void setNomeRistorante(String nomeRistorante) {
        this.nomeRistorante = nomeRistorante;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getThumbnailPortata() {
        return thumbnailPortata;
    }

    public void setThumbnailPortata(String thumbnailPortata) {
        this.thumbnailPortata = thumbnailPortata;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
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
