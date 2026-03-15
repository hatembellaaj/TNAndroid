package com.mdweb.tunnumerique.data.model;

import java.util.List;

public class CategorySlider {
    private String title;
    private List<SlideItem> slides;
    private String categoryId;

    public CategorySlider(String title, List<SlideItem> slides) {
        this.title = title;
        this.slides = slides;
    }

    public CategorySlider(String title, List<SlideItem> slides, String categoryId) {
        this.title = title;
        this.slides = slides;
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SlideItem> getSlides() {
        return slides;
    }

    public void setSlides(List<SlideItem> slides) {
        this.slides = slides;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
