package com.mdweb.tunnumerique.data.model;

/**
 * Created by Mdweb on 23/11/2018.
 */

public class HoraireSalat {
    private int id;
    private String nomSalat;
    private String heure;
    private int icon;
    private String dateSalat;
    private String diffPrecedentPriere;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomSalat() {
        return nomSalat;
    }

    public void setNomSalat(String nomSalat) {
        this.nomSalat = nomSalat;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getDiffPrecedentPriere() {
        return diffPrecedentPriere;
    }

    public void setDiffPrecedentPriere(String diffPrecedentPriere) {
        this.diffPrecedentPriere = diffPrecedentPriere;
    }

    public String getDateSalat() {
        return dateSalat;
    }

    public void setDateSalat(String dateSalat) {
        this.dateSalat = dateSalat;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public HoraireSalat(String nomSalat, String heure, int icon, String dateSalat, String diffPrecedentPriere) {
        this.nomSalat = nomSalat;
        this.heure = heure;
        this.icon = icon;
        this.dateSalat = dateSalat;
        this.diffPrecedentPriere = diffPrecedentPriere;
    }

    public HoraireSalat(String nomSalat, String heure, String diffPrecedentPriere, String dateSalat, int icon) {
        this.nomSalat = nomSalat;
        this.heure = heure;
        this.diffPrecedentPriere = diffPrecedentPriere;
        this.dateSalat = dateSalat;
        this.icon = icon;
    }

    public HoraireSalat() {
    }
}
