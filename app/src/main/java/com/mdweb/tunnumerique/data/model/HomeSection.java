package com.mdweb.tunnumerique.data.model;

import java.util.List;

public class HomeSection {
    private String title;
    private List<Article> articles;

    public HomeSection(String title, List<Article> list) {
        this.title = title;
        this.articles = list;
    }

    public String getTitle() { return title; }
    public List<Article> getArticles() { return articles; }
}
