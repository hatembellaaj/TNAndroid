package com.mdweb.tunnumerique.data.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lenovo-pc on 04/09/2017.
 */

public class ListNews implements Serializable{

ArrayList<News> news = new ArrayList<>();

    public ListNews(ArrayList<News> news) {
        this.news = news;
    }

    public ArrayList<News> getNews() {
        return news;
    }

    public void setNews(ArrayList<News> news) {
        this.news = news;
    }
}
