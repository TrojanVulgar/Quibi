package com.easyplex.data.model.substitles;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MediaSubstitle {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("server")
    @Expose
    private String server;
    @SerializedName("link")
    @Expose
    private String link;

    @SerializedName("lang")
    @Expose
    private String lang;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return lang;
    }

}