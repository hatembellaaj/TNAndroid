package com.mdweb.tunnumerique.data.model;

public class Country {
    private String idPays;
    private String nomPaysAr;
    private String nomPaysFr;
    private String paysIconURL;
    private String iconName;
    private String priereURL;
    private String priereFileName;
    private String nomZoneAdminisrativeAr;
    private String nomZoneAdminisrativeFr;
    private String nomZoneAdminisrativeEn;

    public Country(String idPays, String nomPaysAr, String nomPaysFr, String paysIconURL, String iconName, String priereURL, String priereFileName, String nomZoneAdminisrativeAr, String nomZoneAdminisrativeFr, String nomZoneAdminisrativeEn) {
        this.idPays = idPays;
        this.nomPaysAr = nomPaysAr;
        this.nomPaysFr = nomPaysFr;
        this.paysIconURL = paysIconURL;
        this.iconName = iconName;
        this.priereURL = priereURL;
        this.priereFileName = priereFileName;
        this.nomZoneAdminisrativeAr = nomZoneAdminisrativeAr;
        this.nomZoneAdminisrativeFr = nomZoneAdminisrativeFr;
        this.nomZoneAdminisrativeEn = nomZoneAdminisrativeEn;
    }

    public Country() {
    }

    public String getIdPays() {
        return idPays;
    }

    public String getNomPaysAr() {
        return nomPaysAr;
    }

    public String getNomPaysFr() {
        return nomPaysFr;
    }

    public String getPaysIconURL() {
        return paysIconURL;
    }

    public String getIconName() {
        return iconName;
    }

    public String getPriereURL() {
        return priereURL;
    }

    public String getPriereFileName() {
        return priereFileName;
    }

    public void setIdPays(String idPays) {
        this.idPays = idPays;
    }

    public void setNomPaysAr(String nomPaysAr) {
        this.nomPaysAr = nomPaysAr;
    }

    public void setNomPaysFr(String nomPaysFr) {
        this.nomPaysFr = nomPaysFr;
    }

    public void setPaysIconURL(String paysIconURL) {
        this.paysIconURL = paysIconURL;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public void setPriereURL(String priereURL) {
        this.priereURL = priereURL;
    }

    public void setPriereFileName(String priereFileName) {
        this.priereFileName = priereFileName;
    }

    public String getNomZoneAdminisrativeAr() {
        return nomZoneAdminisrativeAr;
    }

    public String getNomZoneAdminisrativeFr() {
        return nomZoneAdminisrativeFr;
    }

    public void setNomZoneAdminisrativeAr(String nomZoneAdminisrativeAr) {
        this.nomZoneAdminisrativeAr = nomZoneAdminisrativeAr;
    }

    public void setNomZoneAdminisrativeFr(String nomZoneAdminisrativeFr) {
        this.nomZoneAdminisrativeFr = nomZoneAdminisrativeFr;
    }

    public String getNomZoneAdminisrativeEn() {
        return nomZoneAdminisrativeEn;
    }

    public void setNomZoneAdminisrativeEn(String nomZoneAdminisrativeEn) {
        this.nomZoneAdminisrativeEn = nomZoneAdminisrativeEn;
    }
}