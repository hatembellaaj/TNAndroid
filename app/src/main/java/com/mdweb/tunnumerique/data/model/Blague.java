package com.mdweb.tunnumerique.data.model;

import java.io.Serializable;

public class Blague implements Serializable {

    private String idBlague;
    private int artOrPubOrVid;
    private String titleBlague;

    private String descriptionBlagues;
    private String contenuBlagues;
    private String dateBlague;
    private String noteBlagues;
    private String blagueUrl;

    public String getIdBlague() {
        return idBlague;
    }

    public void setIdBlague(String idBlague) {
        this.idBlague = idBlague;
    }

    public int getArtOrPubOrVid() {
        return artOrPubOrVid;
    }

    public void setArtOrPubOrVid(int artOrPubOrVid) {
        this.artOrPubOrVid = artOrPubOrVid;
    }

    public String getTitleBlague() {
        return titleBlague;
    }

    public void setTitleBlague(String titleBlague) {
        this.titleBlague = titleBlague;
    }

    public String getDescriptionBlagues() {
        return descriptionBlagues;
    }

    public void setDescriptionBlagues(String descriptionBlagues) {
        this.descriptionBlagues = descriptionBlagues;
    }

    public String getContenuBlagues() {
        return contenuBlagues;
    }

    public void setContenuBlagues(String contenuBlagues) {
        this.contenuBlagues = contenuBlagues;
    }

    public String getDateBlague() {
        return dateBlague;
    }

    public void setDateBlague(String dateBlague) {
        this.dateBlague = dateBlague;
    }

    public String getNoteBlagues() {
        return noteBlagues;
    }

    public void setNoteBlagues(String noteBlagues) {
        this.noteBlagues = noteBlagues;
    }

    public String getBlagueUrl() {
        return blagueUrl;
    }

    public void setBlagueUrl(String blagueUrl) {
        this.blagueUrl = blagueUrl;
    }

    public Blague() {
    }


}
