package com.easyplex.data.model;

import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.plans.Plan;
import com.easyplex.data.model.upcoming.Upcoming;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.easyplex.data.model.episode.Episode;
import com.easyplex.data.model.episode.EpisodeStream;

import java.util.List;

/**
 * @author Yobex.
 */
public class MovieResponse {


    public List<Media> getAll() {
        return all;
    }

    public void setAll(List<Media> all) {
        this.all = all;
    }

    @SerializedName("all")
    @Expose
    private List<Media> all = null;


    @SerializedName("latest")
    @Expose
    private List<Media> latest = null;

    @SerializedName("featured")
    @Expose
    private List<Media> featured = null;


    @SerializedName("recommended")
    @Expose
    private List<Media> recommended = null;



    @SerializedName("suggested")
    @Expose
    private List<Media> suggested = null;



    @SerializedName("random")
    @Expose
    private List<Media> random = null;


    @SerializedName("trending")
    @Expose
    private List<Media> trending = null;


    @SerializedName("relateds")
    @Expose
    private List<Media> relateds = null;


    @SerializedName("popularSeries")
    @Expose
    private List<Media> popular = null;


    @SerializedName("livetv")
    @Expose
    private List<Media> streaming = null;

    public List<Media> getWatched() {
        return watched;
    }

    public void setWatched(List<Media> watched) {
        this.watched = watched;
    }

    @SerializedName("watched")
    @Expose
    private List<Media> watched = null;

    @SerializedName("recents")
    @Expose
    private List<Media> latestSeries = null;


    @SerializedName("upcoming")
    @Expose
    private List<Upcoming> upcoming = null;


    @SerializedName("anime")
    @Expose
    private List<Media> animes = null;



    @SerializedName("episodes")
    @Expose
    private List<Episode> episodes = null;


    @SerializedName("thisweek")
    @Expose
    private List<Media> thisweek = null;


    @SerializedName("plans")
    @Expose
    private List<Plan> plans = null;


    @SerializedName("popular")
    @Expose
    private List<Media> popularMedia = null;



    @SerializedName("videos")
    @Expose
    private List<EpisodeStream> videos = null;


    public List<Media> getLatest() {
        return latest;
    }

    public void setLatest(List<Media> latest) {
        this.latest = latest;
    }


    public List<Media> getFeatured() {
        return featured;
    }


    public void setFeatured(List<Media> featured) {
        this.featured = featured;
    }


    public List<Media> getRecommended() {
        return recommended;
    }

    public void setRecommended(List<Media> recommended) {
        this.recommended = recommended;
    }


    public List<Media> getSuggested() {
        return suggested;
    }

    public void setSuggested(List<Media> suggested) {
        this.suggested = suggested;
    }



    public List<Media> getRandom() {
        return random;
    }

    public void setRandom(List<Media> random) {
        this.random = random;
    }


    public List<Media> getTrending() {
        return trending;
    }

    public void setTrending(List<Media> trending) {
        this.trending = trending;
    }


    public List<Media> getRelateds() {
        return relateds;
    }

    public void setRelateds(List<Media> relateds) {
        this.relateds = relateds;
    }


    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }


    public List<EpisodeStream> getVideos() {
        return videos;
    }

    public void setVideos(List<EpisodeStream> videos) {
        this.videos = videos;
    }


    public List<Media> getPopular() {
        return popular;
    }

    public void setPopular(List<Media> popular) {
        this.popular = popular;
    }


    public List<Media> getStreaming() {
        return streaming;
    }

    public void setStreaming(List<Media> streaming) {
        this.streaming = streaming;
    }


    public List<Media> getLatestSeries() {
        return latestSeries;
    }

    public void setLatestSeries(List<Media> latestSeries) {
        this.latestSeries = latestSeries;
    }


    public List<Media> getAnimes() {
        return animes;
    }

    public void setAnimes(List<Media> animes) {
        this.animes = animes;
    }


    public List<Upcoming> getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(List<Upcoming> upcoming) {
        this.upcoming = upcoming;
    }



    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }


    public List<Media> getThisweek() {
        return thisweek;
    }

    public void setThisweek(List<Media> thisweek) {
        this.thisweek = thisweek;
    }



    public List<Media> getPopularMedia() {
        return popularMedia;
    }

    public void setPopularMedia(List<Media> popularMedia) {
        this.popularMedia = popularMedia;
    }

}
