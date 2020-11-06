package com.easyplex.data.model.media;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.source.MediaSource;
import java.io.Serializable;

import static com.easyplex.util.Constants.SERVER_BASE_URL;

/**
 * Created by stoyan on 6/5/17.
 */
public class MediaModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Episodes Info
     */

    @Nullable
    private final Integer epId;


    @Nullable
    private final String seasonId;


    @Nullable
    private final String epImdb;



    @Nullable
    private final String tvSeasonId;


    @Nullable
    private final String currentEpName;



    @Nullable
    private final String currentSeasonsNumber;


    @Nullable
    private final Integer episodePostionNumber;


    @Nullable
    private final String currentEpTmdbNumber;


    @Nullable
    private final String currentTvShowName;








    /**
     * The url of the media
     */
    @Nullable
    private final String videoid;




    /**
     * Return Media Genre
     */
    @Nullable
    private final String mediaGenre;




    /**
     * Return Current Quality
     */

    @Nullable
    private final String currentQuality;





    /**
     * Return if media Premuim
     */

    @Nullable
    private Integer isPremuim;


    /**
     * The url of the media
     */
    @NonNull
    private final String mediaUrl;

    /**
     * The title of the media to display
     */
    @Nullable
    private final String mediaName;

    public void setMediaCover(@Nullable String mediaCover) {
        this.mediaCover = mediaCover;
    }

    /**
     * The url of the artwork to display while loading
     */
    @Nullable
    private String mediaCover;

    /**
     * The nullable subtitles that we sideload for the main media source
     */
    @Nullable
    private String mediaSubstitleUrl;

    /**
     * The nullable click through url for this media if its an ad
     *
     * @see #isAd
     */
    @Nullable
    private final String clickThroughUrl;

    /**
     * The media source representation of this model
     */
    private transient MediaSource mediaSource;

    /**
     * Whether this media is an ad or not
     */
    private boolean isAd;


    private String type;

    /**
     * Whether this media is an ad or not
     */
    private boolean isVpaid;



    public MediaModel(@Nullable String currentTvShowName, @Nullable String videoid, @Nullable String mediaGenre,
                      @Nullable String currentQuality, String type, @Nullable
                              String mediaName, @NonNull String mediaUrl,
                      @Nullable String mediaCover,
                      @Nullable String mediaSubstitleUrl, @Nullable String clickThroughUrl,
                      boolean isAd, boolean isVpaid, @Nullable Integer epId,
                      @Nullable String seasonId, @Nullable String epImdb,
                      @Nullable String tvSeasonId, @Nullable String currentEpName,
                      @Nullable String currentSeasonsNumber, @Nullable Integer episodePostionNumber,
                      @Nullable String currentEpTmdbNumber
            , Integer isPremuim) {


        this.currentTvShowName = currentTvShowName;
        this.videoid = videoid;
        this.mediaGenre = mediaGenre;
        this.currentQuality = currentQuality;
        this.mediaName = mediaName;
        this.mediaUrl = mediaUrl;
        this.mediaCover = mediaCover;
        this.mediaSubstitleUrl = mediaSubstitleUrl;
        this.clickThroughUrl = clickThroughUrl;
        this.isAd = isAd;
        this.type = type;
        this.isVpaid = isVpaid;
        this.epId = epId;
        this.seasonId = seasonId;
        this.epImdb = epImdb;
        this.tvSeasonId = tvSeasonId;
        this.currentEpName = currentEpName;
        this.currentSeasonsNumber = currentSeasonsNumber;
        this.episodePostionNumber = episodePostionNumber;
        this.currentEpTmdbNumber = currentEpTmdbNumber;
        this.isPremuim = isPremuim;
    }



    public static MediaModel media(@Nullable String videoid, @Nullable String substitleLang, @Nullable String currentQuality, String type, @Nullable String mediaName, @NonNull String videoUrl, @Nullable String artworkUrl,
                                   @Nullable String subtitlesUrl, @Nullable Integer epId, @Nullable String seasonId, @Nullable String epImdb,
                                   @Nullable String tvSeasonId, @Nullable String currentEpName, @Nullable String currentSeasonsNumber, Integer episodePostionNumber, String currentEpTmdbNumber , Integer isPremuim, String currentTvShowName) {
        return new MediaModel(currentTvShowName, videoid, substitleLang, currentQuality ,type, mediaName, videoUrl, artworkUrl, subtitlesUrl, null,
                false, false,epId,seasonId,epImdb, tvSeasonId, currentEpName, currentSeasonsNumber, episodePostionNumber, currentEpTmdbNumber,isPremuim);
    }

    public static MediaModel ad(@NonNull String videoUrl, @Nullable String clickThroughUrl, boolean isVpaid) {
        return new MediaModel(null, null, null, null, null,null , videoUrl, null, null, clickThroughUrl,true,isVpaid,null,null

                ,null, null, null, null, null, null,null);
    }

    @Nullable
    public String getMediaName() {
        return mediaName;
    }

    @NonNull
    public Uri getMediaUrl() {
        return Uri.parse(mediaUrl);
    }

    @Nullable
    public Uri getMediaCover() {

        if (mediaCover == null) {

          mediaCover = (SERVER_BASE_URL +"image/logo");
        }

        return Uri.parse(mediaCover);
    }

    @Nullable
    public Uri getMediaSubstitleUrl() {
        return mediaSubstitleUrl != null ? Uri.parse(mediaSubstitleUrl) : null;
    }


    @Nullable
    public String getCurrentSeasonsNumber() {
        return currentSeasonsNumber;
    }

    @Nullable
    public String getClickThroughUrl() {
        return clickThroughUrl;
    }

    public boolean isAd() {
        return isAd;
    }

    @Nullable
    public String getCurrentEpTmdbNumber() {
        return currentEpTmdbNumber;
    }

    public String getMediaExtension() {
        return null;
    }

    public MediaSource getMediaSource() {
        return mediaSource;
    }

    public void setMediaSource(MediaSource mediaSource) {
        this.mediaSource = mediaSource;
    }

    @Nullable
    public String getTvSeasonId() {
        return tvSeasonId;
    }

    @Nullable
    public String getVideoid() {
        return videoid;
    }

    @Nullable
    public String getCurrentEpName() {
        return currentEpName;
    }

    public String getType() {
        return type;
    }


    @Nullable
    public String getMediaGenre() {
        return mediaGenre;
    }


    public boolean isVpaid() {
        return isVpaid;
    }


    @Nullable
    public Integer getEpisodePostionNumber() {
        return episodePostionNumber;
    }

    @Nullable
    public Integer getEpId() {
        return epId;
    }

    @Nullable
    public String getSeasonId() {
        return seasonId;
    }

    @Nullable
    public String getEpImdb() {
        return epImdb;
    }


    @Nullable
    public String getCurrentTvShowName() {
        return currentTvShowName;
    }


    @Nullable
    public String getCurrentQuality() {
        return currentQuality;
    }



    public Integer getIsPremuim() {
        return isPremuim;
    }

    public void setIsPremuim(Integer isPremuim) {
        this.isPremuim = isPremuim;
    }
}
