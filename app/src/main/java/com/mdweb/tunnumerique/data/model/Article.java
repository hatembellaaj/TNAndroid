package com.mdweb.tunnumerique.data.model;

public class Article {
    private String title;
    private String imageUrl;

    public Article(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
}


