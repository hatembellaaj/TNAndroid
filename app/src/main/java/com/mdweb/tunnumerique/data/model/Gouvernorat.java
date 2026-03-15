package com.mdweb.tunnumerique.data.model;

import java.util.ArrayList;

/**
 * Created by Mdweb on 23/11/2018.
 */

public class Gouvernorat {
    private  int id;
    private String nomGouvernoratAr;
    private String nomGouvernoratFr;
    ArrayList<HoraireSalat> mListHoraire;

    public Gouvernorat(int id, String nomGouvernoratAr, String nomGouvernoratFr, ArrayList<HoraireSalat> mListHoraire) {
        this.id = id;
        this.nomGouvernoratAr = nomGouvernoratAr;
        this.nomGouvernoratFr = nomGouvernoratFr;
        this.mListHoraire = mListHoraire;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomGouvernoratAr() {
        return nomGouvernoratAr;
    }

    public void setNomGouvernoratAr(String nomGouvernoratAr) {
        this.nomGouvernoratAr = nomGouvernoratAr;
    }

    public String getNomGouvernoratFr() {
        return nomGouvernoratFr;
    }

    public void setNomGouvernoratFr(String nomGouvernoratFr) {
        this.nomGouvernoratFr = nomGouvernoratFr;
    }

    public ArrayList<HoraireSalat> getmListHoraire() {
        return mListHoraire;
    }

    public void setmListHoraire(ArrayList<HoraireSalat> mListHoraire) {
        this.mListHoraire = mListHoraire;
    }

    public Gouvernorat() {
    }
}
