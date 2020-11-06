
package com.easyplex.data.model.substitles;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Opensub  {
    public List<Opensub> getOpensubs() {
        return opensubs;
    }

    public void setOpensubs(List<Opensub> opensubs) {
        this.opensubs = opensubs;
    }

    private List<Opensub> opensubs;

    @SerializedName("SubFileName")
    @Expose
    private String subFileName;


    @SerializedName("InfoFormat")
    @Expose
    private String infoFormat;


    @SerializedName("SubFormat")
    @Expose
    private String subFormat;


    @SerializedName("MovieReleaseName")
    @Expose
    private String movieReleaseName;


    @SerializedName("IDMovieImdb")
    @Expose
    private String iDMovieImdb;


    @SerializedName("LanguageName")
    @Expose
    private String languageName;



    @SerializedName("SubDownloadLink")
    @Expose
    private String subDownloadLink;




    @SerializedName("ZipDownloadLink")
    @Expose
    private String zipDownloadLink;


    @SerializedName("SubHD")
    @Expose
    private String subHD;


    @SerializedName("SubEncoding")
    @Expose
    private String subEncoding;


    public Opensub(String subfile, String movieReleaseName, String languageName, String zipDownloadLink) {
        this.subFileName = subfile;
        this.movieReleaseName = movieReleaseName;
        this.languageName = languageName;
        this.zipDownloadLink = zipDownloadLink;
    }

    public String getSubFileName() {
        return subFileName;
    }

    public void setSubFileName(String subFileName) {
        this.subFileName = subFileName;
    }

    public String getInfoFormat() {
        return infoFormat;
    }

    public void setInfoFormat(String infoFormat) {
        this.infoFormat = infoFormat;
    }

    public String getSubFormat() {
        return subFormat;
    }

    public void setSubFormat(String subFormat) {
        this.subFormat = subFormat;
    }

    public String getMovieReleaseName() {
        return movieReleaseName;
    }

    public void setMovieReleaseName(String movieReleaseName) {
        this.movieReleaseName = movieReleaseName;
    }

    public String getiDMovieImdb() {
        return iDMovieImdb;
    }

    public void setiDMovieImdb(String iDMovieImdb) {
        this.iDMovieImdb = iDMovieImdb;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getSubDownloadLink() {
        return subDownloadLink;
    }

    public void setSubDownloadLink(String subDownloadLink) {
        this.subDownloadLink = subDownloadLink;
    }

    public String getZipDownloadLink() {
        return zipDownloadLink;
    }

    public void setZipDownloadLink(String zipDownloadLink) {
        this.zipDownloadLink = zipDownloadLink;
    }


    public String getSubHD() {
        return subHD;
    }

    public void setSubHD(String subHD) {
        this.subHD = subHD;
    }



    public String getSubEncoding() {
        return subEncoding;
    }

    public void setSubEncoding(String subEncoding) {
        this.subEncoding = subEncoding;
    }


    @Override
    public String toString() {
        return getLanguageName();
    }

}
