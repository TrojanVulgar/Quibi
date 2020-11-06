package com.easyplex.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.RoomWarnings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings(RoomWarnings.DEFAULT_CONSTRUCTOR)
@Entity(primaryKeys = "id",
        tableName = "history",
        indices = {@Index(value = {"id"}, unique = true)}, inheritSuperIndices = true)


public class History extends Media {

    public History(@NonNull String id, @NonNull String tmdbId, String posterPath , String title,String backdropPath,String link) {
        this.id = id;
        this.tmdbId = tmdbId;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.link = link;
        this.title =title;
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


    @NonNull
    @SerializedName("id")
    @Expose
    @ColumnInfo(name = "history_id")
    private String id;


    @NonNull
    @SerializedName("tmdb_id")
    @Expose
    @ColumnInfo(name = "tmdbId_history")
    private String tmdbId;

    @Override
    public String getTitle() {
        return title;
    }



    @Override
    public void setTitle(String title) {
        this.title = title;
    }


    @ColumnInfo(name = "posterpath_download")
    @SerializedName("poster_path")
    @Expose
    private String posterPath;



    @ColumnInfo(name = "title_history")
    @SerializedName("title")
    @Expose
    private String title;

    @Override
    public String getBackdropPath() {
        return backdropPath;
    }

    @Override
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }


    @ColumnInfo(name = "backdrop_path_download")
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @ColumnInfo(name = "link_download")
    @Expose
    private String link;


    public String getTv() {
        return tv;
    }

    public void setTv(String tv) {
        this.tv = tv;
    }

    @ColumnInfo(name = "tv_download")
    @Expose
    private String tv;

    public String getSeasonsId() {
        return seasonsId;
    }

    public void setSeasonsId(String seasonsId) {
        this.seasonsId = seasonsId;
    }

    @ColumnInfo(name = "seasonId_download")
    @Expose
    private String seasonsId;


    @Override
    public String getPosterPath() {
        return posterPath;
    }

    @Override
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }





}
