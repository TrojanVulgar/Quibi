package com.easyplex.data.local.entity;


import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.RoomWarnings;

import com.bumptech.glide.Glide;
import com.easyplex.data.model.serie.Season;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.data.model.substitles.MediaSubstitle;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.easyplex.data.model.genres.Genre;

import java.util.List;

import kotlin.jvm.Transient;


/**
 * @author Yobex.
 */
@Entity(primaryKeys = "tmdbId",
        tableName = "favorite",
        indices = {@Index(value = {"tmdbId"}, unique = true)}, inheritSuperIndices = true)

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
public class Media implements Parcelable {



    public Media(){

        //
    }

    @NonNull
    @SerializedName("id")
    @Expose
    private String id;

    @NonNull
    @SerializedName("tmdb_id")
    @Expose
    @Transient
    private String tmdbId;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("overview")
    @Expose
    private String overview;


    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("preview_path")
    @Expose
    private String previewPath;
    @SerializedName("vote_average")
    @Expose
    private float voteAverage;


    @SerializedName("vote_count")
    @Expose
    private String voteCount;


    @SerializedName("live")
    @Expose
    private int live;

    @SerializedName("premuim")
    @Expose
    private int premuim;

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    @SerializedName("vip")
    @Expose
    private int vip;


    @SerializedName("link")
    @Expose
    private String link;



    private int resumeWindow;

    private long resumePosition;


    @SerializedName("is_anime")
    @Expose
    private int isAnime;

    @SerializedName("popularity")
    @Expose
    private String popularity;


    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("substitles")
    @Expose
    private List<MediaSubstitle> substitles = null;


    @SerializedName("seasons")
    @Expose
    private List<Season> seasons = null;

    @SerializedName("runtime")
    @Expose
    private String runtime;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("genre")
    @Expose
    private String genre;

    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;

    @SerializedName("trailer_id")
    @Expose
    private String trailerId;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getLive() {
        return live;
    }

    public void setLive(int live) {
        this.live = live;
    }


    @SerializedName("hd")
    @Expose
    private Integer hd;


    @SerializedName("videos")
    @Expose
    private List<MediaStream> videos;

    @SerializedName("genres")
    @Expose
    private List<Genre> genres;


    protected Media(Parcel in) {
        id = in.readString();
        tmdbId = in.readString();
        title = in.readString();
        name = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        previewPath = in.readString();
        voteAverage = in.readFloat();
        voteCount = in.readString();
        premuim = in.readInt();
        isAnime = in.readInt();
        popularity = in.readString();
        if (in.readByte() == 0) {
            views = null;
        } else {
            views = in.readInt();
        }
        status = in.readString();
        runtime = in.readString();
        releaseDate = in.readString();
        genre = in.readString();
        firstAirDate = in.readString();
        trailerId = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        if (in.readByte() == 0) {
            hd = null;
        } else {
            hd = in.readInt();
        }
        genres = in.createTypedArrayList(Genre.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(tmdbId);
        dest.writeString(title);
        dest.writeString(name);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(previewPath);
        dest.writeFloat(voteAverage);
        dest.writeString(voteCount);
        dest.writeInt(premuim);
        dest.writeInt(isAnime);
        dest.writeString(popularity);
        if (views == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(views);
        }
        dest.writeString(status);
        dest.writeString(runtime);
        dest.writeString(releaseDate);
        dest.writeString(genre);
        dest.writeString(firstAirDate);
        dest.writeString(trailerId);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        if (hd == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(hd);
        }
        dest.writeTypedList(genres);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(String tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getPreviewPath() {
        return previewPath;
    }

    public void setPreviewPath(String previewPath) {
        this.previewPath = previewPath;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getHd() {
        return hd;
    }

    public void setHd(Integer hd) {
        this.hd = hd;
    }


    public List<MediaSubstitle> getSubstitles() {
        return substitles;
    }

    public void setSubstitles(List<MediaSubstitle> substitles) {
        this.substitles = substitles;
    }



    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


    public List<MediaStream> getVideos() {
        return videos;
    }

    public void setVideos(List<MediaStream> videos) {
        this.videos = videos;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }


    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }


    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getPremuim() {
        return premuim;
    }

    public void setPremuim(int premuim) {
        this.premuim = premuim;
    }


    public int getResumeWindow() {
        return resumeWindow;
    }

    public void setResumeWindow(int resumeWindow) {
        this.resumeWindow = resumeWindow;
    }

    public long getResumePosition() {
        return resumePosition;
    }

    public void setResumePosition(long resumePosition) {
        this.resumePosition = resumePosition;
    }


    public String getMediaExtension() {
        return null;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }


    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }



    public int getIsAnime() {
        return isAnime;
    }

    public void setIsAnime(int isAnime) {
        this.isAnime = isAnime;
    }


    @BindingAdapter("android:imageUrl")
    public static void loadImage (ImageView view, String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }

}
