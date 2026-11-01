package com.sdm.framework.outils;

public class MapFile {
    private String nomFichier;
    private String type;
    private byte[] contenu;
    private String cheminSauvegarde;

    public MapFile() {
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getContenu() {
        return contenu;
    }

    public void setContenu(byte[] contenu) {
        this.contenu = contenu;
    }

    public String getCheminSauvegarde() {
        return cheminSauvegarde;
    }

    public void setCheminSauvegarde(String cheminSauvegarde) {
        this.cheminSauvegarde = cheminSauvegarde;
    }

    public long getTaille() {
        return contenu == null ? 0 : contenu.length;
    }
}