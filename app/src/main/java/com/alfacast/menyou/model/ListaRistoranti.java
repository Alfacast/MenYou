package com.alfacast.menyou.model;

/**
 * Created by Pietro Fantuzzi on 20/06/2016.
 */
public class ListaRistoranti {

    private String nome;
    private String indirizzo;
    private String telefono;
    private String idRistorante;
    private String thumbnail;

    public ListaRistoranti(){

    }

    public ListaRistoranti (String nome, String indirizzo, String telefono, String idRistorante, String thumbnail){
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.telefono = telefono;
        this.idRistorante = idRistorante;
        this.thumbnail = thumbnail;

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getIdRistorante() {
        return idRistorante;
    }

    public void setIdRistorante(String idRistorante) {
        this.idRistorante = idRistorante;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
