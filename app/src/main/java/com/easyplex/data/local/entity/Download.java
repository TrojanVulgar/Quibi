package com.easyplex.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.RoomWarnings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



@Entity(primaryKeys = "tmdbId",
        tableName = "download",
        indices = {@Index(value = {"tmdbId"}, unique = true)})

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
public class Download extends Media {


    public Download(@NonNull String id,@NonNull String tmdbId,String backdropPath , String title , String link) {
        this.id = id;
        this.tmdbId = tmdbId;
        this.backdropPath = backdropPath;
        this.title =title;
        this.link = link;
    }

    @Override
    @NonNull
    public String getId() {
        return id;
    }

    @Override
    public void setId(@NonNull String id) {
        this.id = id;
    }


    @Override
    @NonNull
    public String getTmdbId() {
        return tmdbId;
    }

    @Override
    public void setTmdbId(@NonNull String tmdbId) {
        this.tmdbId = tmdbId;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @NonNull
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "download_id")
    private String id;


    @NonNull
    @SerializedName("tmdb_id")
    @Expose
    @ColumnInfo(name = "tmdbId_download",index = true)
    private String tmdbId;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getBackdropPath() {
        return backdropPath;
    }

    @Override
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }



    @ColumnInfo(name = "title_download")
    @SerializedName("title")
    @Expose
    private String title;


    @ColumnInfo(name = "backdropPath_download")
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;


    @ColumnInfo(name = "link_download")
    @Expose
    private String link;

}
