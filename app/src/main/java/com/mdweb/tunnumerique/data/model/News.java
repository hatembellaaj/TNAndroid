package com.mdweb.tunnumerique.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class News implements Serializable {

    private String idNews;
    private int artOrPubOrVid;
    private String titleNews;
    @SerializedName("News_list_category")
    private String typeNews;
    private String descriptionNews;
    private String Dark_Mode;
    private String contenuNews;
    private String News_commentaire_android;
    private String dateNews;
    private String imageNameNews;
    private String imageUrlNews;
    private String imageNameDetailsNews;
    private String imageUrlDetailsNews;
    private String audioUrlNews;
    private String authorNameNews;
    private String shareUrlNews;
    private List<String> newsKeyWords;
    private String keyWordsNews;
    private String newsLng;

    // ✅ NOUVEAU CHAMP
    @SerializedName("is_paywall")
    private boolean is_paywall;


    // ==============================
    // CONSTRUCTEUR PRINCIPAL
    // ==============================
    public News(String idNews, int artOrPubOrVid, String titleNews, String typeNews,
                String descriptionNews, String contenuNews, String dark,
                String News_commentaire_android, String dateNews, String imageNameNews,
                String imageUrlNews, String imageNameDetailsNews, String imageUrlDetailsNews,
                String urlAudioNews, String authorNameNews, String shareUrlNews,
                String keyWordsNews, String lng, boolean is_paywall) {

        this.idNews = idNews;
        this.artOrPubOrVid = artOrPubOrVid;
        this.titleNews = titleNews;
        this.typeNews = typeNews;
        this.descriptionNews = descriptionNews;
        this.contenuNews = contenuNews;
        this.News_commentaire_android = News_commentaire_android;
        this.dateNews = dateNews;
        this.imageNameNews = imageNameNews;
        this.imageUrlNews = imageUrlNews;
        this.audioUrlNews = urlAudioNews;
        this.Dark_Mode = dark;
        this.imageNameDetailsNews = imageNameDetailsNews;
        this.imageUrlDetailsNews = imageUrlDetailsNews;
        this.authorNameNews = authorNameNews;
        this.shareUrlNews = shareUrlNews;
        this.keyWordsNews = keyWordsNews;
        this.newsLng = lng;
        this.is_paywall = is_paywall; // ✅
    }

    // ==============================
    // CONSTRUCTEUR SIMPLIFIÉ
    // ==============================
    public News(int artOrPubOrVid) {
        this.idNews = "";
        this.artOrPubOrVid = artOrPubOrVid;
        this.titleNews = "";
        this.typeNews = "";
        this.descriptionNews = "";
        this.contenuNews = "";
        this.Dark_Mode = "";
        this.News_commentaire_android = "";
        this.dateNews = "";
        this.imageNameNews = "";
        this.imageUrlNews = "";
        this.imageNameDetailsNews = "";
        this.imageUrlDetailsNews = "";
        this.audioUrlNews = "";
        this.authorNameNews = "";
        this.shareUrlNews = "";
        this.is_paywall = false; // ✅ false par défaut
    }

    // ==============================
    // CONSTRUCTEUR VIDE
    // ==============================
    public News() {
    }


    // ==============================
    // GETTER & SETTER is_paywall ✅
    // ==============================
    public boolean isPaywall() {
        return is_paywall;
    }

    public void setPaywall(boolean paywall) {
        is_paywall = paywall;
    }


    // ==============================
    // GETTERS & SETTERS EXISTANTS
    // ==============================
    public String getKeyWordsNews() { return keyWordsNews; }
    public void setKeyWordsNews(String keyWordsNews) { this.keyWordsNews = keyWordsNews; }

    public int getArtOrPubOrVid() { return artOrPubOrVid; }
    public void setArtOrPubOrVid(int artOrPubOrVid) { this.artOrPubOrVid = artOrPubOrVid; }

    public String getIdNews() { return idNews; }
    public void setIdNews(String idNews) { this.idNews = idNews; }

    public void setUrlAudioNews(String urlAudioNews) { this.audioUrlNews = urlAudioNews; }
    public String getUrlAudioNews() { return audioUrlNews; }

    public String getTitleNews() { return titleNews; }
    public void setTitleNews(String titleNews) { this.titleNews = titleNews; }

    public String getTypeNews() { return typeNews; }
    public void setTypeNews(String typeNews) { this.typeNews = typeNews; }

    public String getDescriptionNews() { return descriptionNews; }
    public void setDescriptionNews(String descriptionNews) { this.descriptionNews = descriptionNews; }

    public String getContenuNews() { return contenuNews; }
    public void setContenuNews(String contenuNews) { this.contenuNews = contenuNews; }

    public String getDateNews() { return dateNews; }
    public void setDateNews(String dateNews) { this.dateNews = dateNews; }

    public String getImageNameNews() { return imageNameNews; }
    public void setImageNameNews(String imageNameNews) { this.imageNameNews = imageNameNews; }

    public String getImageUrlNews() { return imageUrlNews; }
    public void setImageUrlNews(String imageUrlNews) { this.imageUrlNews = imageUrlNews; }

    public String getImageNameDetailsNews() { return imageNameDetailsNews; }
    public void setImageNameDetailsNews(String imageNameDetailsNews) { this.imageNameDetailsNews = imageNameDetailsNews; }

    public String getImageUrlDetailsNews() { return imageUrlDetailsNews; }
    public void setImageUrlDetailsNews(String imageUrlDetailsNews) { this.imageUrlDetailsNews = imageUrlDetailsNews; }

    public String getAuthorNameNews() { return authorNameNews; }
    public void setAuthorNameNews(String authorNameNews) { this.authorNameNews = authorNameNews; }

    public String getShareUrlNews() { return shareUrlNews; }
    public void setShareUrlNews(String shareUrlNews) { this.shareUrlNews = shareUrlNews; }

    public List<String> getNewsKeyWords() { return newsKeyWords; }
    public void setNewsKeyWords(List<String> newsKeyWords) { this.newsKeyWords = newsKeyWords; }

    public String getNews_commentaire_android() { return News_commentaire_android; }
    public void setNews_commentaire_android(String news_commentaire_android) { News_commentaire_android = news_commentaire_android; }

    public String getDark_Mode() { return Dark_Mode; }
    public void setDark_Mode(String dark_Mode) { Dark_Mode = dark_Mode; }

    public String getNewsLng() { return newsLng; }
    public void setNewsLng(String newsLng) { this.newsLng = newsLng; }
}