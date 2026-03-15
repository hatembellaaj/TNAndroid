package com.mdweb.tunnumerique.data.model;


import java.io.Serializable;

public class Categories implements Serializable{
    private String idCategories;
    private String titleCategories;
    private String titleUrlCategories;
    private String iconUrl;
    private String iconTitle;
    private String iconEnabledUrl;
    private String iconEnabledTitle;
    private String bgImageFilterUrl;
    private String bgImageFilterName;
    private int colorR;
    private int colorG;
    private int colorB;


    public Categories() {
    }

    public String getIdCategories() {
        return idCategories;
    }

    public void setIdCategories(String idCategories) {
        this.idCategories = idCategories;
    }

    public String getTitleCategories() {
        return titleCategories;
    }

    public void setTitleCategories(String titleCategories) {
        this.titleCategories = titleCategories;
    }

    public String getTitleUrlCategories() {
        return titleUrlCategories;
    }

    public void setTitleUrlCategories(String titleUrlCategories) {
        this.titleUrlCategories = titleUrlCategories;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getIconTitle() {
        return iconTitle;
    }

    public void setIconTitle(String iconTitle) {
        this.iconTitle = iconTitle;
    }

    public String getIconEnabledUrl() {
        return iconEnabledUrl;
    }

    public void setIconEnabledUrl(String iconEnabledUrl) {
        this.iconEnabledUrl = iconEnabledUrl;
    }

    public String getIconEnabledTitle() {
        return iconEnabledTitle;
    }

    public void setIconEnabledTitle(String iconEnabledTitle) {
        this.iconEnabledTitle = iconEnabledTitle;
    }

    public String getBgImageFilterUrl() {
        return bgImageFilterUrl;
    }

    public void setBgImageFilterUrl(String bgImageFilterUrl) {
        this.bgImageFilterUrl = bgImageFilterUrl;
    }

    public String getBgImageFilterName() {
        return bgImageFilterName;
    }

    public void setBgImageFilterName(String bgImageFilterName) {
        this.bgImageFilterName = bgImageFilterName;
    }

    public int getColorR() {
        return colorR;
    }

    public void setColorR(int colorR) {
        this.colorR = colorR;
    }

    public int getColorG() {
        return colorG;
    }

    public void setColorG(int colorG) {
        this.colorG = colorG;
    }

    public int getColorB() {
        return colorB;
    }

    public void setColorB(int colorB) {
        this.colorB = colorB;
    }
}
