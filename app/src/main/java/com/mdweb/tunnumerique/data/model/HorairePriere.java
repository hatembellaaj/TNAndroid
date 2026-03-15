package com.mdweb.tunnumerique.data.model;


import java.io.Serializable;

public class HorairePriere implements Serializable{
    private String dateMillidai;
    private String fajer;
    private String sobeh;
    private String dhohr;
    private String aser;
    private String maghreb;
    private String icha;

//    public HorairePriere() {
//
//    }

    public HorairePriere() {
        this.dateMillidai = "";
        this.fajer = "";
        this.sobeh = "";
        this.dhohr = "";
        this.aser = "";
        this.maghreb = "";
        this.icha = "";
    }

    public String getDateMillidai() {
        return dateMillidai;
    }

    public void setDateMillidai(String dateMillidai) {
        this.dateMillidai = dateMillidai;
    }

    public String getFajer() {
        return fajer;
    }

    public void setFajer(String fajer) {
        this.fajer = fajer;
    }

    public String getSobeh() {
        return sobeh;
    }

    public void setSobeh(String sobeh) {
        this.sobeh = sobeh;
    }

    public String getDhohr() {
        return dhohr;
    }

    public String getAser() {
        return aser;
    }

    public void setAser(String aser) {
        this.aser = aser;
    }

    public void setDhohr(String dhohr) {
        this.dhohr = dhohr;
    }

    public String getMaghreb() {
        return maghreb;
    }

    public void setMaghreb(String maghreb) {
        this.maghreb = maghreb;
    }

    public String getIcha() {
        return icha;
    }

    public void setIcha(String icha) {
        this.icha = icha;
    }
}
