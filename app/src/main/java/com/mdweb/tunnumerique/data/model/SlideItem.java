package com.mdweb.tunnumerique.data.model;

import java.io.Serializable;

public class SlideItem implements Serializable {

    private String imageUrl;
    private String title;
    private News newsObject; // Objet News complet pour avoir toutes les infos

    public SlideItem(String imageUrl, String title) {
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public News getNewsObject() {
        return newsObject;
    }

    public void setNewsObject(News newsObject) {
        this.newsObject = newsObject;
    }
}