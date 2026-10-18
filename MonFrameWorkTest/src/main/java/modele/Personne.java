package main.java.modele;

import com.sdm.framework.outils.MapFile;

public class Personne {
    private String nom;
    private int age;
    private Adresse adresse;
    private Sexe sexe;
    private MapFile photo;

    public Personne() {
    }

    public Personne(String nom, int age) {
        this.nom = nom;
        this.age = age;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public MapFile getPhoto() {
        return photo;
    }

    public void setPhoto(MapFile photo) {
        this.photo = photo;
    }
}