package com.mdweb.tunnumerique.data.model;

public class SliderItem {
    private String title;
    private String imageUrl;

    public SliderItem(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
}

