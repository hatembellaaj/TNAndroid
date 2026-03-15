package com.mdweb.tunnumerique.data.model;

public class ItemMenu {
    private int id;
    private String text;
    private int icon;
    private String urlIcon;
    private String urlIconEnable;


    /**
     * Create Item menu with specific type and with specific icon
     *
     * @param text the value of item menu
     * @param icon the icon of item menu
     * @param id   the id of created item menu
     */
    public ItemMenu(String text, int icon, int id) {

        this.text = text;
        this.icon = icon;
        this.id = id;
    }

    /**
     *
     * @param text the value of item menu
     * @param id the id of created item menu
     */
    public ItemMenu(String text, int id) {
        this.text = text;
        this.id = id;
    }

    public ItemMenu(String text, String urlIcon, String urlIconEnable, int id) {
        this.text = text;
        this.id = id;
        this.urlIcon = urlIcon;
        this.urlIconEnable = urlIconEnable;
    }


    /**
     * Get the ID of item
     *
     * @return A integer representing the ID of item
     */
    public int getId() {
        return id;
    }

    /**
     * Get the ID of item
     *
     * @param id the ID of item
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Gets the text value of item menu
     *
     * @return A String representing the text value of item menu
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text value of item menu
     *
     * @param text the text value of item men
     */
    public void setText(String text) {
        this.text = text;
    }


    /**
     * Gets Icon resource ID of item menu
     *
     * @return A integer representing the id of icon
     */
    public int getIcon() {
        return icon;
    }

    /**
     * Sets Icon of item menu
     *
     * @param icon the id icon of item menu
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getUrlIcon() {
        return urlIcon;
    }

    public void setUrlIcon(String urlIcon) {
        this.urlIcon = urlIcon;
    }

    public String getUrlIconEnable() {
        return urlIconEnable;
    }

    public void setUrlIconEnable(String urlIconEnable) {
        this.urlIconEnable = urlIconEnable;
    }
}
