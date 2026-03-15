package com.mdweb.tunnumerique.data.model;

import java.util.List;

public class SectionHome {
    public String sectionName;
    public List<Article> articles;

    public SectionHome(String name, List<Article> articles) {
        this.sectionName = name;
        this.articles = articles;
    }
}
