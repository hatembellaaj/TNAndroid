package com.mdweb.tunnumerique.data.model;

import java.util.ArrayList;

/**
 * Created by Mdweb on 23/11/2018.
 */

public class DateSalat {
    private String dateMiladi;
    private String dateMiladiText;
    private String newsAr;
    private String dateHijriText;
    private ArrayList<Gouvernorat> mListeGouvernorat;

    public DateSalat(String dateMiladi, String dateMiladiText, String dateHijriText, String newsAr, ArrayList<Gouvernorat> mListeGouvernorat) {
        this.dateMiladi = dateMiladi;
        this.dateMiladiText = dateMiladiText;
        this.dateHijriText = dateHijriText;
        this.newsAr = newsAr;
        this.mListeGouvernorat = mListeGouvernorat;
    }

    public String getDateMiladi() {
        return dateMiladi;
    }

    public void setDateMiladi(String dateMiladi) {
        this.dateMiladi = dateMiladi;
    }

    public String getDateMiladiText() {
        return dateMiladiText;
    }

    public void setDateMiladiText(String dateMiladiText) {
        this.dateMiladiText = dateMiladiText;
    }

    public String getNewsAr() {
        return newsAr;
    }

    public void setNewsAr(String newsAr) {
        this.newsAr = newsAr;
    }

    public String getDateHijriText() {
        return dateHijriText;
    }

    public void setDateHijriText(String dateHijriText) {
        this.dateHijriText = dateHijriText;
    }

    public ArrayList<Gouvernorat> getmListeGouvernorat() {
        return mListeGouvernorat;
    }

    public void setmListeGouvernorat(ArrayList<Gouvernorat> mListeGouvernorat) {
        this.mListeGouvernorat = mListeGouvernorat;
    }

    public DateSalat() {
    }
}