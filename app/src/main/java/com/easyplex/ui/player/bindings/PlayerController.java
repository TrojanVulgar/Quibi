package com.easyplex.ui.player.bindings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.FragmentManager;

import com.easyplex.R;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EmbedActivity;
import com.easyplex.ui.player.controller.PlayerUIController;
import com.easyplex.ui.player.enums.ScaleMode;
import com.easyplex.ui.player.interfaces.PlaybackActionCallback;
import com.easyplex.ui.player.interfaces.TubiPlaybackControlInterface;
import com.easyplex.ui.player.presenters.ScalePresenter;
import com.easyplex.ui.player.utilities.ExoPlayerLogger;
import com.easyplex.ui.player.utilities.PlayerDeviceUtils;
import com.easyplex.ui.player.utilities.TrackSelectionDialog;
import com.easyplex.ui.player.views.TubiExoPlayerView;
import com.easyplex.util.Constants;
import com.easyplex.util.Tools;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.video.VideoListener;
import javax.inject.Inject;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.easyplex.util.Constants.CUSTOM_SEEK_CONTROL_STATE;
import static com.easyplex.util.Constants.DEFAULT_FREQUENCY;
import static com.easyplex.util.Constants.EDIT_CUSTOM_SEEK_CONTROL_STATE;
import static com.easyplex.util.Constants.SERVER_BASE_URL;

/**
 * This class contains business logic of user interaction between user and player action. This class will be serving
 * as interface between Player UI and Business logic, such as seek, pause, UI logic for displaying ads vs movie.
 */
public class PlayerController extends AppCompatActivity implements TubiPlaybackControlInterface, Player.EventListener, SeekBar.OnSeekBarChangeListener {


    @Inject
    public PlayerController(){

        //

    }

    private static final String TAG = PlayerController.class.getSimpleName();


    private boolean isDraggingSeekBar;
    private static int controlstate = 1;


    @Inject
    MediaRepository repository;



    /**
     * Media action states
     */
    public final ObservableInt playerPlaybackState = new ObservableInt(Player.STATE_IDLE);
    public final ObservableBoolean isVideoPlayWhenReady = new ObservableBoolean(false);


    /**
     * Media informations
     */


    public final ObservableField<String> nextSeasonsID = new ObservableField<>("");


    // Return Media Name
    public final ObservableField<String> serieTvShowName = new ObservableField<>("");

    // Return Media Name
    public final ObservableField<String> videoName = new ObservableField<>("");


    // Return Current Media TMDB Number (EX : 168222)
    public final ObservableField<String> getCurrentMediaTmdbNumber = new ObservableField<>("");


    // Return Media Genre
    public final ObservableField<String> mediaGenre = new ObservableField<>("");


    // Return Episode Position ( Json )
    public final ObservableInt episodePosition = new ObservableInt();


    // Return Media Current Stream Link
    public final ObservableField<Uri> videoCurrentLink = new ObservableField<>();


    // Return Media Current Substitle Link
    public final ObservableField<String> videoCurrentSubs = new ObservableField<>("Substitle...");





    // Return Media Current Substitle Link
    public final ObservableField<String> addtomylist = new ObservableField<>("Add To MyList");


    // Return Media Current Quality Link
    public final ObservableField<String> videoCurrentQuality = new ObservableField<>("Select Subs..");


    // Return Media ID
    public final ObservableField<String> videoID = new ObservableField<>("");


    // Return Media ID
    public final ObservableField<String> currentEpisodeName = new ObservableField<>("");



    // Return Current Episode Season Number for a Serie or Anime
    public final ObservableField<String> currentSeasonsNumber = new ObservableField<>("");


    // Return Current Episode IMDB Number for a Serie or Anime
    public final ObservableField<String> currentEpisodeImdbNumber = new ObservableField<>("");


    // Return if media Has An ID
    public final ObservableField<Boolean> videoHasID = new ObservableField<>(false);


    // Return if media External Id (TMDB)
    public final ObservableField<String> videoExternalID = new ObservableField<>("");


    // Return Remaining Time for the current Media
    public final ObservableField<String> timeRemaining = new ObservableField<>();


    // Return Media Type
    public final ObservableField<String> mediaType = new ObservableField<>("");


    // Return Media Type
    public final ObservableField<Uri> mediaPoster = new ObservableField<>();



    // Return Media Substile in Uri Format
    public final ObservableField<Uri> mediaSubstitleUri = new ObservableField<>();


    // Return Media Duration
    public final ObservableField<Long> mediaDuration = new ObservableField<>(0L);


    // Return Media Current Time ( For SeekBar )
    public final ObservableField<Long> mediaCurrentTime = new ObservableField<>(0L);


    // Return Media Current Buffred Position ( For SeekBar )
    public final ObservableField<Long> mediaBufferedPosition = new ObservableField<>(0L);

    // Return Media Current Remaining Time in String Format
    public final ObservableField<String> mediaRemainInString = new ObservableField<>("");


    // Return Media Media Position
    public final ObservableField<String> mediaPositionInString = new ObservableField<>("");


    // Return True if the media Has an Active Substitle
    public final ObservableField<Boolean> mediaHasSubstitle = new ObservableField<>(false);


    // Return Next Episode Cover
    public final ObservableField<String> nextSerieBackDrop = new ObservableField<>("");


    // Return Current Episode Cover
    public final ObservableField<String> currentMediaCover = new ObservableField<>("");


    // Return True if the media is Ended
    public final ObservableField<Boolean> mediaEnded = new ObservableField<>(false);


    // Return True if Current Media is a Live Streaming
    public final ObservableField<Boolean> isLive = new ObservableField<>(false);


    // Return True if Current User Has a Premuim Membership
    public final ObservableField<Boolean> isUserPremuim = new ObservableField<>(false);


    // Return Episode Id for a Serie
    public final ObservableField<String> episodeId = new ObservableField<>("4:3");


    // Return Seasons Id for a Serie
    public final ObservableField<String> episodeSeasonsId = new ObservableField<>("");


    // Return Seasons Id for a Serie
    public final ObservableField<String> episodeSeasonsNumber = new ObservableField<>("");


    // Return if Current Media is Premuim
    public final ObservableInt mediaPremuim = new ObservableInt();


    // Return True if the User has enabled the Substitle
    public final ObservableField<Boolean> isMediaSubstitleEnabled = new ObservableField<>(false);


    public final ObservableField<Boolean> isStreamOnFavorite = new ObservableField<>(false);



    // Return True if the User has enabled the Substitle
    public final ObservableField<Boolean> isMediaCastEnabled = new ObservableField<>(false);


    /**
     * Ad information
     */

    // Return ads Click Url
    public final ObservableField<String> adClickUrl = new ObservableField<>("");


    public final ObservableField<String> adMetaData = new ObservableField<>("");


    // Return Number of Ads Left
    public final ObservableInt numberOfAdsLeft = new ObservableInt(0);


    // Return True if Current Media is playing an ADS
    public final ObservableField<Boolean> isCurrentAd = new ObservableField<>(false);


    public final ObservableField<Boolean> isSkippable = new ObservableField<>(false);


    // Return True if Current Media has reached a CuePoint
    public final ObservableField<Boolean> isCuePointReached = new ObservableField<>(false);

    // Return AdvertiserName for ADS
    public final ObservableField<String> advertiserName = new ObservableField<>("");


    // Return Ads Remaining Time in String Format
    public final ObservableField<String> adsRemainInString = new ObservableField<>("");

    // Return Ads Skip Time in Seconds
    public final ObservableDouble adsSkipTimeOffset = new ObservableDouble();


    public final ObservableField<Boolean> adsSkipTimeOffsetFinished = new ObservableField<>(false);



    /**
     * user interaction attribute
     */

    private PlayerUIController controller;
    private float mInitVideoAspectRatio;
    private ScalePresenter mScalePresenter;
    private VideoListener mVideoListener = new VideoListener() {
        @Override
        public void onVideoSizeChanged(final int width, final int height, final int unappliedRotationDegrees,
                                       final float pixelWidthHeightRatio) {
            ExoPlayerLogger.d(TAG, "onVideoSizeChanged");
            mInitVideoAspectRatio = height == 0 ? 1 : (width * pixelWidthHeightRatio) / height;
        }

        @Override
        public void onRenderedFirstFrame() {
            ExoPlayerLogger.d(TAG, "onRenderedFirstFrame");
        }
    };
    private Handler mProgressUpdateHandler = new Handler(Looper.getMainLooper());
    private static  Runnable mOnControlStateChange;
    /**
     * the Exoplayer instance which this {@link PlayerController} is controlling.
     */
    private SimpleExoPlayer mPlayer;
    /**
     * this is the current mediaModel being played, it could be a ad or actually video
     */
    private MediaModel mMediaModel;
    private PlaybackActionCallback mPlaybackActionCallback;
    private Runnable updateProgressAction = this::updateProgress;
    private TubiExoPlayerView mTubiExoPlayerView;

    FragmentManager manager;



    /**
     * Every time the FsmPlayer change states between
     * AdPlayingState and MoviePlayingState,
     * current controller instance need to update the video instance.
     *
     * @param mediaModel the current video that will be played by the {@link PlayerController#mPlayer} instance.
     */
    public void setMediaModel(MediaModel mediaModel, Context context) {
        if (mediaModel == null) {
            ExoPlayerLogger.e(TAG, "setMediaModel is null");
        } else {

            this.mMediaModel = mediaModel;

            //mark flag for ads to movie
            isCurrentAd.set(mMediaModel.isAd());


            mScalePresenter = new ScalePresenter(mTubiExoPlayerView.getContext(), this);


            if (mMediaModel.isAd()) {

                if (!PlayerDeviceUtils.isTVDevice(context)
                        && !TextUtils.isEmpty(mMediaModel.getClickThroughUrl())) {
                    adClickUrl.set(mMediaModel.getClickThroughUrl());
                }

                videoName.set(context.getString(R.string.commercial));

                mediaHasSubstitle.set(false);

            } else {

                if (mediaType.get().equals("streaming")) {

                    isLive.set(true);

                }

                setModelMediaInfo(mediaModel, context);

            }
        }




    }






    private void setModelMediaInfo(@NonNull MediaModel mediaModel, Context context) {


        if (mediaModel.getMediaCover() == null) {

            mediaModel.setMediaCover(SERVER_BASE_URL +"image/logo");

        }


        if (isStreamOnFavorite.get()) {

            addtomylist.set("Added");

        }else {

            addtomylist.set("Add To MyList");

        }


        if (!TextUtils.isEmpty(mMediaModel.getCurrentTvShowName())) {
            serieTvShowName.set(mMediaModel.getCurrentTvShowName());
        }

        if (!TextUtils.isEmpty(mMediaModel.getMediaName())) {
            videoName.set(mMediaModel.getMediaName());
        }

        if (!TextUtils.isEmpty(mMediaModel.getVideoid())) {
            videoID.set(mMediaModel.getVideoid());
            videoHasID.set(true);

        }



        if (mMediaModel.getIsPremuim() != null) {

            mediaPremuim.set(mMediaModel.getIsPremuim());

        }


        if (!TextUtils.isEmpty(mMediaModel.getMediaGenre())) {
            videoExternalID.set(mMediaModel.getMediaGenre());
        }

        if (!PlayerDeviceUtils.isTVDevice(context) // Disable artwork for TV
                && mMediaModel.getMediaCover() != null) {
            mediaPoster.set(mMediaModel.getMediaCover());
        }


        if (!TextUtils.isEmpty(mMediaModel.getType())) {
            mediaType.set(mMediaModel.getType());
        }


        if (!TextUtils.isEmpty(mMediaModel.getCurrentQuality())) {
            videoCurrentQuality.set(mMediaModel.getCurrentQuality());
        }



        videoCurrentLink.set(mediaModel.getMediaUrl());
        if (mediaModel.getMediaCover() !=null) {

            currentMediaCover.set(String.valueOf(mediaModel.getMediaCover()));


        }else {

            currentMediaCover.set(SERVER_BASE_URL+"public.png");

        }





        setMediaInfo2(context);

    }

    private void setMediaInfo2(Context context) {

        if (!PlayerDeviceUtils.isTVDevice(context) // Disable artwork for TV
                && mMediaModel.getMediaCover() != null) {
            mediaPoster.set(mMediaModel.getMediaCover());
        }


        if (mMediaModel.getMediaSubstitleUrl() != null) {
            mediaHasSubstitle.set(true);
            mediaSubstitleUri.set(mMediaModel.getMediaSubstitleUrl());
            triggerSubtitlesToggle(true);
        }







        if (!TextUtils.isEmpty(mMediaModel.getSeasonId())) {
            currentSeasonsNumber.set(mMediaModel.getSeasonId());
        }


        if (!TextUtils.isEmpty(mMediaModel.getEpImdb())) {
            currentEpisodeImdbNumber.set(mMediaModel.getEpImdb());
        }


        if (!TextUtils.isEmpty(mMediaModel.getTvSeasonId())) {
            nextSeasonsID.set(mMediaModel.getTvSeasonId());
        }


        if (!TextUtils.isEmpty(mMediaModel.getCurrentEpName())) {
            currentEpisodeName.set(mMediaModel.getCurrentEpName());
        }




        if (mMediaModel.getEpId() != null) {

            episodeId.set(String.valueOf(mMediaModel.getEpId()));
        }


        if (!TextUtils.isEmpty(mMediaModel.getCurrentSeasonsNumber())) {
            episodeSeasonsId.set(mMediaModel.getCurrentSeasonsNumber());
        }





        if (mMediaModel.getEpisodePostionNumber() != null) {

            episodePosition.set(mMediaModel.getEpisodePostionNumber());

        }


        if (mMediaModel.getCurrentEpTmdbNumber() != null) {

            getCurrentMediaTmdbNumber.set(mMediaModel.getCurrentEpTmdbNumber());

        }
    }


    /**
     * Every time the FsmPlayer change states between
     * AdPlayingState and MoviePlayingState,
     * {@link PlayerController#mPlayer} instance need to update .
     *
     * @param player the current player that is playing the video
     */
    public void setPlayer(@NonNull SimpleExoPlayer player, @NonNull PlaybackActionCallback playbackActionCallback,
                          @NonNull TubiExoPlayerView tubiExoPlayerView) {


        if (this.mPlayer == player) {
            return;
        }


        mTubiExoPlayerView = tubiExoPlayerView;

        //remove the old listener
        if (mPlayer != null) {
            this.mPlayer.removeListener(this);
        }


        this.mPlayer = player;
        mPlayer.addListener(this);
        mPlayer.addVideoListener(mVideoListener);
        playerPlaybackState.set(mPlayer.getPlaybackState());
        mPlaybackActionCallback = playbackActionCallback;
        updateProgress();


    }


    public void setAvailableAdLeft(int count) {
        numberOfAdsLeft.set(count);
    }

    public void updateTimeTextViews(long position, long duration) {
        //translate the movie remaining time number into display string, and update the UI
        mediaRemainInString.set(Tools.getProgressTime((duration - position), true));
        adsRemainInString.set("Up Next in : " + Tools.getProgressTime((duration - position), true));
        mediaPositionInString.set(Tools.getProgressTime(position, false));

    }


    /**
     * Get current player control state
     *
     * @return Current control state
     */
    public int getState() {
        return controlstate;
    }


    /**
     * Set current player state
     */
    public static  void setState(final int state) {
        controlstate = state;

        if (mOnControlStateChange != null) {
            mOnControlStateChange.run();
        }
    }

    /**
     * Check if it is during custom seek
     *
     * @return True if custom seek is performing
     */
    public boolean isDuringCustomSeek() {

        return controlstate == CUSTOM_SEEK_CONTROL_STATE || controlstate == EDIT_CUSTOM_SEEK_CONTROL_STATE;

    }



    @Override
    public void setPremuim(boolean premuim) {

        if (premuim) {

            isUserPremuim.set(true);

        }


    }

    @Override
    public void SelectTracks() {


       // mPlaybackActionCallback.onTrackSelection();

    }

    @Override
    public void onAdsPlay(boolean playing,boolean isAdsSkippable) {

       isCurrentAd.set(playing);
       isSkippable.set(isAdsSkippable);



    }



    @Override
    public void triggerSubtitlesToggle(final boolean enabled) {

        if (mTubiExoPlayerView == null) {
            ExoPlayerLogger.e(TAG, "triggerSubtitlesToggle() --> tubiExoPlayerView is null");
            return;
        }

        //trigger the hide or show subtitles.
        View subtitles = mTubiExoPlayerView.getSubtitleView();
        if (subtitles != null) {
            subtitles.setVisibility(enabled ? VISIBLE : View.INVISIBLE);
        }

        if (mPlaybackActionCallback != null && mPlaybackActionCallback.isActive()) {
            mPlaybackActionCallback.onSubtitles(mMediaModel, enabled);
        }

        isMediaSubstitleEnabled.set(enabled);
    }

    @Override
    public void seekBy(final long millisecond) {
        if (mPlayer == null) {
            ExoPlayerLogger.e(TAG, "seekBy() ---> player is empty");
            return;
        }

        long currentPosition = mPlayer.getCurrentPosition();
        long seekPosition = currentPosition + millisecond;

        //lower bound
        seekPosition = seekPosition < 0 ? 0 : seekPosition;
        //upper bound
        seekPosition = Math.min(seekPosition, mPlayer.getDuration());

        if (mPlaybackActionCallback != null && mPlaybackActionCallback.isActive()) {

            mPlaybackActionCallback.onSeek(mMediaModel, currentPosition, seekPosition);
        }

        seekToPosition(seekPosition);
    }

    @Override
    public void onCuePointReached(boolean reached) {

        if (reached){

            isCuePointReached.set(true);

        }

    }


    @Override
    public void loadPreview(long millisecond, long max) {

        //

    }

    @Override
    public void seekTo(final long millisecond) {
        if (mPlaybackActionCallback != null && mPlaybackActionCallback.isActive()) {
            long currentProgress = mPlayer != null ? mPlayer.getCurrentPosition() : 0;
            mPlaybackActionCallback.onSeek(mMediaModel, currentProgress, millisecond);
        }

        seekToPosition(millisecond);


        loadPreview(millisecond, millisecond);


    }

    @Override
    public void isSubtitleEnabled(boolean enabled) {

        isMediaSubstitleEnabled.get();


    }


    @Override
    public Integer isMediaPremuim(){


       return mediaPremuim.get();

    }


    @Override
    public boolean hasSubsActive() {

        return mediaHasSubstitle.get();
    }

    @Override
    public void triggerPlayOrPause(final boolean setPlay) {

        if (mPlayer != null) {
            mPlayer.setPlayWhenReady(setPlay);
            isVideoPlayWhenReady.set(setPlay);
        }

        if (mPlaybackActionCallback != null && mPlaybackActionCallback.isActive()) {
            mPlaybackActionCallback.onPlayToggle(mMediaModel, setPlay);


        }
    }



    /**
     * Change Video Scale
     */
    @Override
    public void scale() {

        mScalePresenter.doScale();
        ScaleMode scaleMode = mScalePresenter.getCurrentScaleMode();
        Toast.makeText(mTubiExoPlayerView.getContext(), "" + scaleMode.getDescription(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLoadEpisodes() {

        mPlaybackActionCallback.onLoadEpisodes();

    }

    @Override
    public void onLoadStreaming() {

        mPlaybackActionCallback.onLoadSteaming();

    }

    /**
     * Return Movie or SERIE  Name
     */
    @Override
    public String getCurrentVideoName() {
        return videoName.get();
    }


    @Override
    public Integer getCurrentEpisodePosition() {
        return episodePosition.get();
    }


    /**
     * Get Next Season ID for TV-SERIE
     */

    @Override
    public String nextSeaonsID() {

        return nextSeasonsID.get();

    }


    /**
     * Get Current Season
     */
    @Override
    public String getCurrentSeason() {
        return episodeSeasonsId.get();
    }

    @Override
    public String getCurrentSeasonNumber() {
        return episodeSeasonsNumber.get();
    }


    /**
     * Get Current Video Quality (Servers)
     */
    @Override
    public String getVideoCurrentQuality() {
        return videoCurrentQuality.get();
    }


    /**
     * Get Episode Name
     */

    @Override
    public String getEpName() {
        return currentEpisodeName.get();
    }


    @Override
    public String getSeaonNumber() {
        return currentSeasonsNumber.get();
    }

    @Override
    public String getCurrentTvShowName() {
        return serieTvShowName.get();
    }


    /**
     * Get Movie or TV ID
     */

    @Override
    public String getVideoID() {
        return videoID.get();
    }



    @Override
    public String getMediaSubstitleName() {


       return  videoExternalID.get();

    }


    /**
     * Get Media Stream Link
     */
    @Override
    public Uri getVideoUrl() {
        return videoCurrentLink.get();
    }

    @Override
    public Uri getMediaSubstitleUrl() {

        return mediaSubstitleUri.get();
    }


    /**
     * Get Media Poster
     */
    @Override
    public Uri getMediaPoster() {
        return mediaPoster.get();
    }


    /**
     * Get Media Type
     */
    @Override
    public String getMediaType() {
        return mediaType.get();
    }

    @Override
    public String getCurrentEpTmdbNumber() {

        return getCurrentMediaTmdbNumber.get();
    }




    /**
     * return Media or ad
     */

    @Override
    public boolean isCurrentVideoAd() {
        return isCurrentAd.get();
    }

    @Override
    public void vlcPlayer() {


     mPlaybackActionCallback.onDefaultTrackSelector();

    }

    @Override
    public void castLaunch() {


        mPlaybackActionCallback.onLoadCast();

    }

    @Override
    public void onCastFound() {

        isMediaSubstitleEnabled.get();
    }

    @Override
    public void onTracksVideo() {

        mPlaybackActionCallback.onTracksVideo();

    }

    @Override
    public void onTracksAudio() {


        mPlaybackActionCallback.onTracksAudio();

    }

    @Override
    public void onTracksSubstitles() {

        mPlaybackActionCallback.onTracksSubstitles();
    }

    @Override
    public void onOpenSubsLoad() {

        mPlaybackActionCallback.onOpenSubsLoad();
    }


    /**
     * Get Media Qualities (480p,720p,1080p,4k)
     */

    @Override
    public void clickQualitySettings() {

        if (getVideoID().isEmpty()) {

            Toast.makeText(mTubiExoPlayerView.getContext(), "there is no quantity available", Toast.LENGTH_SHORT).show();

        }else {

            mPlaybackActionCallback.onLoadQualities();

        }
    }

    /**
     * Release Player
     */

    @Override
    public void closePlayer() {

        ((EasyPlexMainPlayer) (mTubiExoPlayerView.getContext())).onBackPressed();


    }




    // Return Movies List
    @Override
    public void loadMoviesList() {

        mPlaybackActionCallback.onLoadMoviesList();

    }




    // Return Next Episode for TV-Serie
    @Override
    public void nextEpisode() {

        mPlaybackActionCallback.onLoadNextEpisode();

    }




    @Override
    public void addToMyList(final boolean enabled) {

        mPlaybackActionCallback.onAddMyList(enabled);



    }

    @Override
    public void checkFavorite( boolean favorite) {

        isStreamOnFavorite.set(favorite);

    }


    // Substitles
    @Override
    public void clickOnSubs() {

        mPlaybackActionCallback.onSubtitlesSelection();

    }

    public PlayerUIController getController() {
        return controller;

    }

    public void setController(@NonNull PlayerUIController controller) {
        this.controller = controller;

    }

    //------------------------------player playback listener-------------------------------------------//

    @Override
    public void onTimelineChanged(Timeline timeline, @Player.TimelineChangeReason int reason) {
        setPlaybackState();
        updateProgress();
    }

    @Override
    public void onPositionDiscontinuity(final int reason) {

        setPlaybackState();
        updateProgress();
    }


    @Override
    public void onPlayerStateChanged(final boolean playWhenReady, final int playbackState) {
        playerPlaybackState.set(playbackState);
        isVideoPlayWhenReady.set(playWhenReady);
        updateProgress();


        if (playbackState == Player.STATE_ENDED) {

            mPlaybackActionCallback.onMediaEnded();

            mMediaModel.setMediaSource(null);


        }


    }

    @Override
    public void onRepeatModeChanged(final int repeatMode) {


        //

    }

    @Override
    public void onShuffleModeEnabledChanged(final boolean shuffleModeEnabled) {


        //

    }

    @Override
    @SuppressWarnings("ReferenceEquality")
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {





    }

    @Override
    public void onLoadingChanged(final boolean isLoading) {

        ExoPlayerLogger.i(TAG, "onLoadingChanged");


    }

    @Override
    public void onPlayerError(final ExoPlaybackException error) {



        final Dialog dialog = new Dialog(mTubiExoPlayerView.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_error_player);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;


        dialog.findViewById(R.id.play_embed).setOnClickListener(v -> {

            Intent intent = new Intent(mTubiExoPlayerView.getContext(), EmbedActivity.class);
            intent.putExtra(Constants.MOVIE_LINK, String.valueOf(getVideoUrl()));
            mTubiExoPlayerView.getContext().startActivity(intent);

            dialog.dismiss();



        });

        dialog.findViewById(R.id.btn_retry_media).setOnClickListener(v ->{

                    mTubiExoPlayerView.getPlayer().retry();
                    dialog.dismiss();

                });


        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> {

            ((EasyPlexMainPlayer) (mTubiExoPlayerView.getContext())).onBackPressed();


                });


        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }



    @Override
    public void onPlaybackParametersChanged(final PlaybackParameters playbackParameters) {


        ExoPlayerLogger.d(TAG, "onPlaybackParametersChanged");

    }

    @Override
    public void onSeekProcessed() {

        ExoPlayerLogger.d(TAG, "onSeekProcessed");


    }

    //-----------------------------------------SeekBar listener--------------------------------------------------------------//


    @Override
    public void setVideoAspectRatio(float widthHeightRatio) {

        if (mTubiExoPlayerView != null) {
            mTubiExoPlayerView.setAspectRatio(widthHeightRatio);
        }
        ExoPlayerLogger.i(TAG, "setVideoAspectRatio " + widthHeightRatio);

    }


    @Override
    public float getInitVideoAspectRatio() {
        ExoPlayerLogger.i(TAG, "getInitVideoAspectRatio " + mInitVideoAspectRatio);
        return mInitVideoAspectRatio;
    }

    @Override
    public void setResizeMode(final int resizeMode) {
        if (mTubiExoPlayerView != null) {
            mTubiExoPlayerView.setResizeMode(resizeMode);
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {

        if (fromUser) {
            long position = Tools.progressToMilli(mPlayer.getDuration(), seekBar);
            long duration;

            duration = mPlayer.getDuration();
            updateTimeTextViews(position, duration);

        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
        isDraggingSeekBar = true;
        ExoPlayerLogger.i(TAG, "onStartTrackingTouch");
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {

        if (mPlayer != null) {
            seekTo(Tools.progressToMilli(mPlayer.getDuration(), seekBar));
        }

        isDraggingSeekBar = false;
        ExoPlayerLogger.i(TAG, "onStopTrackingTouch");
    }

    //---------------------------------------private method---------------------------------------------------------------------------//

    private void setPlaybackState() {
        int playBackState = mPlayer == null ? Player.STATE_IDLE : mPlayer.getPlaybackState();
        playerPlaybackState.set(playBackState);
    }

    private void seekToPosition(long positionMs) {
        if (mPlayer != null) {
            mPlayer.seekTo(mPlayer.getCurrentWindowIndex(), positionMs);
        }
    }

    private void updateProgress() {

        long position = mPlayer == null ? 0 : mPlayer.getCurrentPosition();
        long duration = mPlayer == null ? 0 : mPlayer.getDuration();
        long bufferedPosition = mPlayer == null ? 0 : mPlayer.getBufferedPosition();

        //only update the seekBar UI when user are not interacting, to prevent UI interference
        if (!isDraggingSeekBar && !isDuringCustomSeek()) {
            updateSeekBar(position, duration, bufferedPosition);
            updateTimeTextViews(position, duration);
        }

        ExoPlayerLogger.i(TAG, "updateProgress:----->" + mediaCurrentTime.get());



        if (mPlaybackActionCallback != null && mPlaybackActionCallback.isActive()) {
            mPlaybackActionCallback.onProgress(mMediaModel, position, duration);
        }else {

            return;
        }

        mProgressUpdateHandler.removeCallbacks(updateProgressAction);



        // Schedule an update if necessary.
        if (!(playerPlaybackState.get() == Player.STATE_IDLE || playerPlaybackState.get() == Player.STATE_ENDED || !mPlaybackActionCallback
                .isActive())) {

            //don't post the updateProgress event when user pause the video
            if (mPlayer != null && !mPlayer.getPlayWhenReady()) {
                return;
            }

            long delayMs;
            delayMs = DEFAULT_FREQUENCY;
            mProgressUpdateHandler.postDelayed(updateProgressAction, delayMs);
        }
    }

    private void updateSeekBar(long position, long duration, long bufferedPosition) {
        //update progressBar.
        mediaCurrentTime.set(position);
        mediaDuration.set(duration);
        mediaBufferedPosition.set(bufferedPosition);
    }


}