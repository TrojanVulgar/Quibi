package com.easyplex.ui.player.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.R;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.local.entity.Stream;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.model.episode.Episode;
import com.easyplex.data.model.episode.EpisodeStream;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.genres.GenresByID;
import com.easyplex.data.model.genres.GenresData;
import com.easyplex.data.model.media.Resume;
import com.easyplex.data.model.serie.Season;
import com.easyplex.data.model.status.Status;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.data.model.substitles.MediaSubstitle;
import com.easyplex.data.repository.AnimeRepository;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.ui.manager.StatusManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.player.adapters.EpisodesPlayerAdapter;
import com.easyplex.ui.player.adapters.ClickDetectListner;
import com.easyplex.ui.player.adapters.MovieQualitiesAdapter;
import com.easyplex.ui.player.adapters.MoviesListAdapter;
import com.easyplex.ui.player.adapters.NextPlayMoviesAdapter;
import com.easyplex.ui.player.adapters.NextPlaySeriesAdapter;
import com.easyplex.ui.player.adapters.SerieQualitiesAdapter;
import com.easyplex.ui.player.adapters.StreamingPlayerAdapter;
import com.easyplex.ui.player.adapters.SubstitlesAdapter;
import com.easyplex.ui.player.utilities.TrackSelectionDialog;
import com.easyplex.ui.settings.SettingsActivity;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.SpacingItemDecoration;
import com.facebook.ads.AdError;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdPlaybackState;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.player.bindings.PlayerController;
import com.easyplex.ui.player.controller.PlayerAdLogicController;
import com.easyplex.ui.player.controller.PlayerUIController;
import com.easyplex.ui.player.fsm.Input;
import com.easyplex.ui.player.fsm.callback.AdInterface;
import com.easyplex.ui.player.fsm.concrete.VpaidState;
import com.easyplex.ui.player.fsm.listener.AdPlayingMonitor;
import com.easyplex.ui.player.fsm.listener.CuePointMonitor;
import com.easyplex.ui.player.fsm.state_machine.FsmPlayer;
import com.easyplex.ui.player.interfaces.AutoPlay;
import com.easyplex.ui.player.interfaces.DoublePlayerInterface;
import com.easyplex.data.model.ads.AdMediaModel;
import com.easyplex.data.model.ads.AdRetriever;
import com.easyplex.data.model.ads.CuePointsRetriever;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.ui.player.interfaces.VpaidClient;
import com.easyplex.ui.player.utilities.ExoPlayerLogger;
import com.easyplex.ui.player.utilities.PlayerDeviceUtils;
import com.easyplex.ui.player.views.UIControllerView;
import com.easyplex.util.Constants;
import com.easyplex.util.Tools;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider;
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.Gson;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;
import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.easyplex.util.Constants.ARG_MOVIE;
import static com.easyplex.util.Constants.AUTO_PLAY;
import static com.easyplex.util.Constants.E;
import static com.easyplex.util.Constants.EP;
import static com.easyplex.util.Constants.S0;
import static com.easyplex.util.Constants.SPECIALS;
import static com.easyplex.util.Constants.UPNEXT;
import static com.google.android.exoplayer2.Player.STATE_IDLE;


/**
 * Created by allensun on 7/24/17.
 */
public class EasyPlexMainPlayer extends EasyPlexPlayerActivity implements DoublePlayerInterface, AutoPlay, ClickDetectListner {


    private static final String TAG = "EasyPlexMainPlayer";
    protected SimpleExoPlayer adPlayer;
    private MediaModel mMediaModel;
    private CountDownTimer mCountDownTimer;
    private ImaAdsLoader adsLoader;
    private Random random;
    public final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Stream stream;
    private IUnityAdsListener iUnityAdsListener;
    private StartAppAd startAppAd;


    @Inject
    FsmPlayer fsmPlayer;
    @Inject
    PlayerUIController uiController;
    @Inject
    AdPlayingMonitor adPlayingMonitor;
    @Inject
    CuePointMonitor cuePointMonitor;
    @Inject
    AdRetriever adRetriever;
    @Inject
    CuePointsRetriever cuePointsRetriever;
    @Inject
    AdInterface adInterface;
    @Inject
    PlayerAdLogicController playerComponentController;
    @Inject
    VpaidClient vpaidClient;

    @Inject
    SharedPreferences preferences;

    @Inject
    SettingsManager appSettingsManager;

    @Inject
    StatusManager statusManager;

    @Inject
    TokenManager tokenManager;

    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    PlayerController playerController;

    @Inject
    MediaRepository repository;

    @Inject
    AnimeRepository animeRepository;


    EpisodesPlayerAdapter mEPAdapter;
    StreamingPlayerAdapter mStreamingAdapter;
    NextPlaySeriesAdapter nextPlaySeriesAdapter;
    NextPlayMoviesAdapter nextPlayMoviesAdapter;
    MoviesListAdapter moviesListAdapter;
    SubstitlesAdapter mSubstitleAdapter;
    MovieQualitiesAdapter movieQualitiesAdapter;
    SerieQualitiesAdapter serieQualitiesAdapter;
    ClickDetectListner clickDetectListner = this;


    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;

    private RewardedAd mRewardedAd;


    protected AdRetriever getAdRetriever() {
        return adRetriever;
    }

    protected CuePointsRetriever getCuePointsRetriever() {
        return cuePointsRetriever;
    }

    @Override
    protected PlayerUIController getUiController() {
        return uiController;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        Tools.hideSystemPlayerUi(this,true);

        Intent intent = getIntent();

        Media media = intent.getParcelableExtra(ARG_MOVIE);

        if (media !=null) {

        stream  = new Stream(media.getId(),media.getTmdbId(),media.getPosterPath(),media.getTitle(),media.getBackdropPath(),media.getLink());

        }


        mRewardedAd = new RewardedAd(this, settingsManager.getSettings().getAdUnitIdRewarded());

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {


                //
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {


                //
            }
        };
        mRewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);


        createAndLoadRewardedAd();



        if (Tools.checkIfHasNetwork(this)) {

            authRepository.getApp(settingsManager.getSettings().getPurchaseKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Status>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(Status status) {

                            //

                        }

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onError(Throwable e) {

                            binding.viewStatus.setVisibility(View.VISIBLE);
                            binding.restartApp.setOnClickListener(view -> finishAffinity());
                            binding.closeStatus.setOnClickListener(view -> finishAffinity());
                            binding.viewStatus.setOnTouchListener((view, motionEvent) -> {
                                finishAffinity();
                                return false;
                            });


                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });
        }


    }



    @Override
    public View addUserInteractionView() {

        return new UIControllerView(getBaseContext()).setPlayerController((PlayerController) getPlayerController());

    }


    protected FsmPlayer getFsmPlayer() {
        return fsmPlayer;
    }

    @Override
    protected void initMoviePlayer() {
        super.initMoviePlayer();


        if (repository.isStreamFavorite(Integer.parseInt(getPlayerController().getVideoID()))) {
            getPlayerController().checkFavorite(true);
        } else  getPlayerController().checkFavorite(false);

        repository.getResumeById(getPlayerController().getVideoID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Resume>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(Resume resume) {


                        if (
                                authManager.getUserInfo().getId() !=null && authManager.getUserInfo().getId() == resume.getUserResumeId() &&

                                resume.getTmdb() !=null && resume.getTmdb().equals(getPlayerController().getVideoID())) {

                            mMoviePlayer.seekTo(resume.getResumePosition());

                        } else {

                            mMoviePlayer.seekTo(0);

                        }



                    }

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void onError(Throwable e) {


                        mMoviePlayer.seekTo(0);


                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });


        createMediaSource(mediaModel);



        if (!PlayerDeviceUtils.useSinglePlayer()) {
            setupAdPlayer();

        }
    }

    @Override
    protected void onPlayerReady() {


        prepareFSM();


    }




    @Override
    public void releaseMoviePlayer() {
        super.releaseMoviePlayer();
        if (!PlayerDeviceUtils.useSinglePlayer()) {
            releaseAdPlayer();
        }
        updateResumePosition();
    }

    private void setupAdPlayer() {

        trackSelectorAd = new DefaultTrackSelector(this);
        adPlayer = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelectorAd).build();
    }

    private void releaseAdPlayer() {
        if (adPlayer != null) {
            updateAdResumePosition();
            adPlayer.release();
            adPlayer = null;
            trackSelectorAd = null;

        }

        if (binding.vpaidWebview != null) {
            binding.vpaidWebview.loadUrl(Constants.EMPTY_URL);
            binding.vpaidWebview.clearHistory();
        }
    }

    private void updateAdResumePosition() {
        if (adPlayer != null && uiController != null) {
            int adResumeWindow = adPlayer.getCurrentWindowIndex();
            long adResumePosition = adPlayer.isCurrentWindowSeekable() ? Math.max(0, adPlayer.getCurrentPosition())
                    : C.TIME_UNSET;
            uiController.setAdResumeInfo(adResumeWindow, adResumePosition);
        }
    }

    /**
     * update the movie and ad playing position when players are released
     */


    @Override
    public void updateResumePosition() {

        //keep track of movie player's position when activity resume back


        if (!getPlayerController().getVideoID().isEmpty() && !getPlayerController().getMediaType().isEmpty()

        && mMoviePlayer != null && playerUIController != null
                && mMoviePlayer.getPlaybackState() != STATE_IDLE) {


                int resumeWindow = mMoviePlayer.getCurrentWindowIndex();
                int videoDuration = (int) mMoviePlayer.getDuration();
                int resumePosition = (int) (mMoviePlayer.isCurrentWindowSeekable() ?
                                        Math.max(0, mMoviePlayer.getCurrentPosition())
                                        :
                                        C.TIME_UNSET);



            if (getPlayerController().getMediaType().equals("0")) {

                    repository.getResumeMovie(authManager.getUserInfo().getId(),getPlayerController().getVideoID(),resumeWindow,resumePosition,videoDuration)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Resume>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                    //

                                }

                                @Override
                                public void onNext(Resume resume) {

                                    //
                                }

                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public void onError(Throwable e) {

                                    //
                                }

                                @Override
                                public void onComplete() {

                                    //

                                }
                            });

                }else {


                    repository.getResumeMovie(authManager.getUserInfo().getId(),getPlayerController().getCurrentEpTmdbNumber(),resumeWindow,resumePosition,videoDuration)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Resume>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                    //

                                }

                                @Override
                                public void onNext(Resume resume) {

                                    //
                                }

                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public void onError(Throwable e) {


                                    //

                                }

                                @Override
                                public void onComplete() {

                                    //

                                }
                            });
                }


                ExoPlayerLogger.i(Constants.FSMPLAYER_TESTING, resumePosition + "");


        }

    }


    @Override
    protected boolean isCaptionPreferenceEnable() {
        return true;
    }

    /**
     * prepare / set up FSM and inject all the elements into the FSM
     */
    @Override
    public void prepareFSM() {

        //update the playerUIController view, need to update the view everything when two ExoPlayer being recreated in activity lifecycle.
        uiController.setContentPlayer(mMoviePlayer);

        if (!PlayerDeviceUtils.useSinglePlayer()) {
            uiController.setAdPlayer(adPlayer);
        }

        uiController.setExoPlayerView(binding.tubitvPlayer);
        uiController.setVpaidWebView(binding.vpaidWebview);

        //update the MediaModel
        fsmPlayer.setController(uiController);
        fsmPlayer.setMovieMedia(mediaModel);
        fsmPlayer.setAdRetriever(adRetriever);
        fsmPlayer.setCuePointsRetriever(cuePointsRetriever);
        fsmPlayer.setAdServerInterface(adInterface);

        //set the PlayerComponentController.
        playerComponentController.setAdPlayingMonitor(adPlayingMonitor);
        playerComponentController.setTubiPlaybackInterface(this);
        playerComponentController.setDoublePlayerInterface(this);
        playerComponentController.setCuePointMonitor(cuePointMonitor);
        playerComponentController.setVpaidClient(vpaidClient);
        fsmPlayer.setPlayerComponentController(playerComponentController);
        fsmPlayer.setLifecycle(getLifecycle());

        if (fsmPlayer.isInitialized()) {
            fsmPlayer.updateSelf();
            Tools.hideSystemPlayerUi(this, true);
        } else {
            fsmPlayer.transit(Input.INITIALIZE);
        }
    }

    @Override
    public void onBackPressed() {



        updateResumePosition();


       if (binding.webviewPlayer !=null) {


            binding.webviewPlayer.clearHistory();
        }



        playerComponentController.setDoublePlayerInterface(null);
        playerComponentController.setTubiPlaybackInterface(null);
        adPlayingMonitor = null;
        uiController.setExoPlayerView(null);
        uiController.setContentPlayer(null);
        uiController.setVpaidWebView(null);
        uiController.setAdPlayer(null);


        if (adsLoader !=null) {

            adsLoader.stop();
            adsLoader.release();
            adsLoader = null;

        }


        mediaSource = null;
        mediaModel.setMediaSource(null);


        if(mCountDownTimer!=null){

        mCountDownTimer.cancel();

        }

        if (fsmPlayer != null && fsmPlayer.getCurrentState() instanceof VpaidState && binding.vpaidWebview != null
                && binding.vpaidWebview.canGoBack()) {

            //if the last page is empty url, then, it should
            if (ingoreWebViewBackNavigation(binding.vpaidWebview)) {
                super.onBackPressed();
                return;
            }

            binding.vpaidWebview.goBack();

        } else {
            super.onBackPressed();
        }
    }

    //when the last item is "about:blank", ingore the back navigation for webview.
    private boolean ingoreWebViewBackNavigation(WebView vpaidWebView) {

        if (vpaidWebView != null) {
            WebBackForwardList mWebBackForwardList = vpaidWebView.copyBackForwardList();

            if (mWebBackForwardList == null) {
                return false;
            }

            WebHistoryItem historyItem = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1);

            if (historyItem == null) {
                return false;
            }

            String historyUrl = historyItem.getUrl();

            return historyUrl != null && historyUrl.equalsIgnoreCase(Constants.EMPTY_URL);
        }

        return false;
    }

    /**
     * creating the {@link MediaSource} for the Exoplayer, recreate it everytime when new {@link SimpleExoPlayer} has been initialized
     *
     * @return
     */
    protected void createMediaSource(MediaModel videoMediaModel) {

        videoMediaModel.setMediaSource(buildMediaSource(videoMediaModel));

    }

    @Override
    public void onPrepareAds(@Nullable AdMediaModel adMediaModel) {

        if (adMediaModel !=null) {


            for (MediaModel singleMedia : adMediaModel.getListOfAds()) {
                MediaSource adMediaSource = buildMediaSource(singleMedia);
                singleMedia.setMediaSource(adMediaSource);
            }
        }

        }

    @Override
    public void onProgress(MediaModel mediaModel, long milliseconds, long durationMillis) {
        ExoPlayerLogger.v(TAG, mediaModel.getMediaName() + ": " + mediaModel.toString() + " onProgress: " + "milliseconds: " + milliseconds + " durationMillis: " + durationMillis);

        // monitor the movie progress.
        cuePointMonitor.onMovieProgress(milliseconds);

        if (milliseconds == 300) {


            getPlayerController().onCuePointReached(true);
        }


    }

    @Override
    public void onSeek(MediaModel mediaModel, long oldPositionMillis, long newPositionMillis) {
        ExoPlayerLogger.v(TAG, mediaModel.getMediaName() + ": " + mediaModel.toString() + " onSeek : " + "oldPositionMillis: " + oldPositionMillis + " newPositionMillis: " + newPositionMillis);
    }

    @Override
    public void onPlayToggle(MediaModel mediaModel, boolean playing) {
        ExoPlayerLogger.v(TAG, mediaModel.getMediaName() + ": " + mediaModel.toString() + " onPlayToggle :");
    }


    @Override
    public void onSubtitles(@Nullable MediaModel mediaModel, boolean enabled) {

        //
    }

    @Override
    public void onDefaultTrackSelector() {

        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(mTrackSelector.getCurrentMappedTrackInfo());
        DefaultTrackSelector.Parameters parameters = mTrackSelector.getParameters();

        for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
            if (TrackSelectionDialog.showTabForRenderer(mappedTrackInfo, rendererIndex)) {
                int trackType = mappedTrackInfo.getRendererType(rendererIndex);
                TrackGroupArray trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex);
                boolean isRendererDisabled = parameters.getRendererDisabled(rendererIndex);
                DefaultTrackSelector.SelectionOverride selectionOverride = parameters.getSelectionOverride(rendererIndex, trackGroupArray);
                Log.d(TAG, "------------------------------------------------------Track item " + rendererIndex + "------------------------------------------------------");
                Log.d(TAG, "track type: " + (trackType));
                Log.d(TAG, "track group array: " + new Gson().toJson(trackGroupArray));
                for (int groupIndex = 0; groupIndex < trackGroupArray.length; groupIndex++) {
                    for (int trackIndex = 0; trackIndex <  trackGroupArray.get(groupIndex).length; trackIndex++) {
                        String trackName = new DefaultTrackNameProvider(getResources()).getTrackName(trackGroupArray.get(groupIndex).getFormat(trackIndex));
                        Boolean isTrackSupported = mappedTrackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex) == RendererCapabilities.FORMAT_HANDLED;
                        Log.d(TAG, "track item " + groupIndex +": trackName: " + trackName + ", isTrackSupported: " + isTrackSupported);
                    }
                }
                Log.d(TAG, "isRendererDisabled: " + isRendererDisabled);
                Log.d(TAG, "selectionOverride: " + new Gson().toJson(selectionOverride));


                TrackSelectionDialogBuilder build = new TrackSelectionDialogBuilder(EasyPlexMainPlayer.this, "Tracks", mTrackSelector, rendererIndex);
                build.setAllowAdaptiveSelections(isRendererDisabled);
                build.build().show();
            }
        }


    }



    // Load Qualities for Movies and Series
    @Override
    public void onLoadQualities() {


        binding.frameQualities.setVisibility(View.VISIBLE);
        binding.closeQualities.setOnClickListener(v -> binding.frameQualities.setVisibility(View.GONE));


        // For Movie
        if (getPlayerController().getMediaType().equals("0")){

            repository.getMovie(getPlayerController().getVideoID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Media>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(Media media) {


                            List<MediaStream> movieStreams = media.getVideos();


                            // Episodes RecycleView
                            binding.rvQualites.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            binding.rvQualites.setHasFixedSize(true);
                            movieQualitiesAdapter = new MovieQualitiesAdapter(EasyPlexMainPlayer.this);
                            movieQualitiesAdapter.addSeasons(movieStreams,clickDetectListner);
                            binding.rvQualites.setAdapter(movieQualitiesAdapter);


                        }

                        @Override
                        public void onError(Throwable e) {

                            //

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });



        } else {



            // For Series
            repository.getSerieStream(getPlayerController().getCurrentEpTmdbNumber())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<MediaStream>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(MediaStream episodeStream) {

                            List<MediaStream> streamInfo = episodeStream.getMediaStreams();


                            // Episodes RecycleView
                            binding.rvQualites.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            binding.rvQualites.setHasFixedSize(true);
                            serieQualitiesAdapter = new SerieQualitiesAdapter(EasyPlexMainPlayer.this);
                            serieQualitiesAdapter.addQuality(streamInfo,  clickDetectListner);
                            binding.rvQualites.setAdapter(serieQualitiesAdapter);


                        }

                        @Override
                        public void onError(Throwable e) {

                            //

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });



        }


    }



    // Load Substitles for Movies & Series
    @Override
    public void onSubtitlesSelection() {


        binding.frameSubstitles.setVisibility(View.VISIBLE);
        binding.closeSubstitle.setOnClickListener(v -> binding.frameSubstitles.setVisibility(View.GONE));





            if (getPlayerController().getMediaType().equals("0")){

                repository.getMovie(getPlayerController().getVideoID())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Media>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                                //

                            }

                            @Override
                            public void onNext(Media media) {

                                List<MediaSubstitle> movieSubtitles = media.getSubstitles();



                                // Episodes RecycleView
                                binding.rvSubstitles.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                binding.rvSubstitles.setHasFixedSize(true);
                                mSubstitleAdapter = new SubstitlesAdapter(EasyPlexMainPlayer.this);
                                mSubstitleAdapter.addSubtitle(movieSubtitles, clickDetectListner);
                                binding.rvSubstitles.setAdapter(mSubstitleAdapter);



                            }

                            @Override
                            public void onError(Throwable e) {

                                //

                            }

                            @Override
                            public void onComplete() {

                                //

                            }
                        });



            }else {


                repository.getEpisodeSubstitle(getPlayerController().getCurrentEpTmdbNumber())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .cache()
                        .subscribe(new Observer<EpisodeStream>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                                //


                            }

                            @Override
                            public void onNext(EpisodeStream episodeStream) {


                                List<MediaSubstitle> movieSubtitles = episodeStream.getStreamepisode();


                                // Episodes RecycleView
                                binding.rvSubstitles.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                binding.rvSubstitles.setHasFixedSize(true);
                                mSubstitleAdapter = new SubstitlesAdapter(EasyPlexMainPlayer.this);
                                mSubstitleAdapter.addSubtitle(movieSubtitles, clickDetectListner);
                                binding.rvSubstitles.setAdapter(mSubstitleAdapter);



                            }

                            @Override
                            public void onError(Throwable e) {

                                //

                            }

                            @Override
                            public void onComplete() {

                                //

                            }
                        });
        }

    }

    @Override
    public void onMediaEnded() {

        if (playerController.playerPlaybackState.get() != Player.STATE_ENDED) {





            if (preferences.getBoolean(AUTO_PLAY,true) && !getPlayerController().getVideoID().isEmpty() && !getPlayerController().getMediaType().isEmpty()) {


                mediaModel.setMediaSource(null);

                onHideLayout();


                if (getPlayerController().getMediaType().equals("1")) {


                    onLoadNextSerieEpisodes();


                } else {


                    onLoadNextMovies();


                }


            }else {

                onBackPressed();

            }

        }

    }

    private void onHideLayout() {

        if (binding.frameLayoutSeasons.getVisibility() == View.VISIBLE) {
            binding.frameLayoutSeasons.setVisibility(View.GONE);
        }

        if (binding.frameLayoutMoviesList .getVisibility() == View.VISIBLE)
            binding.frameLayoutMoviesList.setVisibility(View.GONE);


        if (binding.frameLayoutSeasons .getVisibility() == View.VISIBLE)
            binding.frameLayoutSeasons.setVisibility(View.GONE);



        if (binding.frameLayoutStreaming .getVisibility() == View.VISIBLE) {

            binding.frameLayoutStreaming.setVisibility(View.GONE);
        }



        if (binding.frameQualities .getVisibility() == View.VISIBLE) {

            binding.frameQualities.setVisibility(View.GONE);
        }


        if (binding.frameSubstitles .getVisibility() == View.VISIBLE) {

            binding.frameSubstitles.setVisibility(View.GONE);
        }


    }


    // Load Next Movies RecycleViews
    private void onLoadNextMovies() {


        binding.filterBtnEnded.setOnClickListener(v -> binding.spinnerMediaEnded.performClick());
        binding.framlayoutMediaEnded.setVisibility(View.VISIBLE);




        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        binding.framlayoutMediaEnded.startAnimation(alphaAnimation);



        binding.closeMediaEnded.setOnClickListener(v -> {


            binding.framlayoutMediaEnded.setVisibility(View.GONE);

            if(mCountDownTimer!=null) {

                mCountDownTimer.cancel();

            }

        });

        repository.getMoviRandom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Observer<MovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {


                        // Episodes RecycleView
                        binding.rvEpisodesEnded.setHasFixedSize(true);
                        binding.rvEpisodesEnded.setNestedScrollingEnabled(false);
                        binding.rvEpisodesEnded.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                        binding.rvEpisodesEnded.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getApplicationContext(), 0), true));
                        binding.rvEpisodesEnded.setItemViewCacheSize(20);
                        nextPlayMoviesAdapter = new NextPlayMoviesAdapter(EasyPlexMainPlayer.this,clickDetectListner,
                                EasyPlexMainPlayer.this,authManager,settingsManager, mCountDownTimer,tokenManager);
                        nextPlayMoviesAdapter.addSeasons(movieResponse.getRandom());
                        binding.rvEpisodesEnded.setAdapter(nextPlayMoviesAdapter);
                        Collections.shuffle(movieResponse.getRandom());
                        binding.filterBtnEnded.setVisibility(View.GONE);

                       onLoadRandomMovie();

                    }

                    @Override
                    public void onError(Throwable e) {

                        Timber.tag(String.format("%s : %s", TAG, e.getCause()));

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });

    }

    private void onLoadRandomMovie() {

        repository.getMovie(String.valueOf(nextPlayMoviesAdapter.getFirstItem()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Media>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(Media media) {


                        mCountDownTimer = new CountDownTimer(10000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                                binding.textViewVideoTimeRemaining.setText(UPNEXT + millisUntilFinished / 1000 + " s");
                                binding.ratingBar.setRating(media.getVoteAverage() / 2);


                                if (media.getReleaseDate() != null ) {
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
                                    try {
                                        Date releaseDate = sdf1.parse(media.getReleaseDate());
                                        binding.textViewVideoRelease.setText(" - "+sdf2.format(releaseDate));
                                    } catch (ParseException e) {

                                        Timber.d("%s", Arrays.toString(e.getStackTrace()));

                                    }
                                } else {
                                    binding.textViewVideoRelease.setText("");
                                }


                                Glide.with(EasyPlexMainPlayer.this).load(media.getBackdropPath())
                                        .centerCrop()
                                        .placeholder(R.drawable.placehoder_episodes)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(binding.imageViewMovieNext);


                                binding.textViewVideoNextName.setText(media.getTitle());



                                for (Genre genre : media.getGenres()) {
                                    binding.textViewVideoNextReleaseDate.setText(genre.getName());
                                }



                            }

                            @Override
                            public void onFinish() {


                                if (media.getVideos().isEmpty()) {

                                    DialogHelper.showNoStreamAvailable(EasyPlexMainPlayer.this);

                                } else  if (media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() !=null) {

                                    String mediaUrl = media.getVideos().get(0).getLink();
                                    String currentQuality = media.getVideos().get(0).getServer();
                                    mMediaModel = MediaModel.media(String.valueOf(media.getId()), String.valueOf(media.getTmdbId()),currentQuality,
                                            "0", media.getTitle(), mediaUrl, media.getBackdropPath(), null,
                                            null, null, null, null, null,
                                            null,null,null,null,null);
                                    playNext(mMediaModel);

                                    binding.framlayoutMediaEnded.setVisibility(View.GONE);


                                }else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && media.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                                    onLoadSubscribeDialog(media);

                                }else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && media.getPremuim() == 0 ){


                                    String mediaUrl = media.getVideos().get(0).getLink();
                                    String currentQuality = media.getVideos().get(0).getServer();
                                    mMediaModel = MediaModel.media(String.valueOf(media.getId()), String.valueOf(media.getTmdbId()),currentQuality,
                                            "0", media.getTitle(), mediaUrl, media.getBackdropPath(), null,
                                            null, null, null, null, null,
                                            null,null,null,null,null);
                                    playNext(mMediaModel);

                                    binding.framlayoutMediaEnded.setVisibility(View.GONE);


                                } else if (authManager.getUserInfo().getPremuim() == 1 && media.getPremuim() == 0){


                                    String mediaUrl = media.getVideos().get(0).getLink();
                                    String currentQuality = media.getVideos().get(0).getServer();
                                    mMediaModel = MediaModel.media(String.valueOf(media.getId()), String.valueOf(media.getTmdbId()),currentQuality,
                                            "0", media.getTitle(), mediaUrl, media.getBackdropPath(), null,
                                            null, null, null, null, null,
                                            null,null,null,null,null);
                                    playNext(mMediaModel);

                                    binding.framlayoutMediaEnded.setVisibility(View.GONE);


                                }else {

                                    DialogHelper.showPremuimWarning(EasyPlexMainPlayer.this);

                                }

                            }
                        }.start();


                    }

                    @Override
                    public void onError(Throwable e) {

                        onBackPressed();


                    }

                    @Override
                    public void onComplete() {


                        //

                    }
                });





    }



    private void onLoadSubscribeDialog(Media movieDetail) {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.watch_to_unlock);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;





        dialog.findViewById(R.id.text_view_go_pro).setOnClickListener(v -> {

           startActivity(new Intent(this, SettingsActivity.class));

            dialog.dismiss();


        });


        dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {


            String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();
            if ("StartApp".equals(defaultRewardedNetworkAds)) {


                onLoadStartAppAds(movieDetail);

            } else if ("UnityAds".equals(defaultRewardedNetworkAds)) {

                onLoadUnityAds(movieDetail);


            } else if ("Admob".equals(defaultRewardedNetworkAds)) {


                onLoadAdmobRewardAds(movieDetail);


            }else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                onLoadAppOdealRewardAds(movieDetail);

            } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                onLoadFaceBookRewardAds(movieDetail);

            }else if ("Auto".equals(defaultRewardedNetworkAds)) {

                onLoadAutoRewardAds(movieDetail);

            }


            dialog.dismiss();


        });


        dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

                dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }



    private void onLoadAutoRewardAds(Media media) {

        random = new Random();
        int numberOfMethods = 4;

        switch(random.nextInt(numberOfMethods)) {
            case 0:
                onLoadStartAppAds(media);
                break;
            case 1:
                onLoadUnityAds(media);
                break;
            case 2:
                onLoadAdmobRewardAds(media);
                break;
            case 3:
                onLoadFaceBookRewardAds(media);
                break;
            default:
                onLoadAdmobRewardAds(media);
        }


    }







    private void onLoadAppOdealRewardAds(Media media) {

        Appodeal.show(this, Appodeal.REWARDED_VIDEO);

        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded(boolean isPrecache) {


                //

            }

            @Override
            public void onRewardedVideoFailedToLoad() {
                //

            }

            @Override
            public void onRewardedVideoShown() {

                //
            }

            @Override
            public void onRewardedVideoShowFailed() {
                //

            }

            @Override
            public void onRewardedVideoClicked() {

                //
            }

            @Override
            public void onRewardedVideoFinished(double amount, String name) {

                String mediaUrl = media.getVideos().get(0).getLink();
                String currentQuality = media.getVideos().get(0).getServer();
                mMediaModel = MediaModel.media(String.valueOf(media.getId()), String.valueOf(media.getTmdbId()),currentQuality,
                        "0", media.getTitle(), mediaUrl, media.getBackdropPath(), null,
                        null, null, null, null, null,
                        null,null,null,null,null);
                playNext(mMediaModel);

                if (binding.framlayoutMediaEnded.getVisibility() == View.VISIBLE) {

                    binding.framlayoutMediaEnded.setVisibility(View.GONE);

                }

            }

            @Override
            public void onRewardedVideoClosed(boolean finished) {


                //
            }

            @Override
            public void onRewardedVideoExpired() {

                //

            }

        });

    }


    private void onLoadFaceBookRewardAds(Media media) {


        mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(this, settingsManager.getSettings().getAdUnitIdFacebookRewarded());
        mFacebookRewardedVideoAd.loadAd(mFacebookRewardedVideoAd.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoCompleted() {

                        String mediaUrl = media.getVideos().get(0).getLink();
                        String currentQuality = media.getVideos().get(0).getServer();
                        mMediaModel = MediaModel.media(String.valueOf(media.getId()), String.valueOf(media.getTmdbId()),currentQuality,
                                "0", media.getTitle(), mediaUrl, media.getBackdropPath(), null,
                                null, null, null, null, null,
                                null,null,null,null,null);
                        playNext(mMediaModel);

                        if (binding.framlayoutMediaEnded.getVisibility() == View.VISIBLE) {

                            binding.framlayoutMediaEnded.setVisibility(View.GONE);

                        }

                    }

                    @Override
                    public void onLoggingImpression(com.facebook.ads.Ad ad) {

                        //

                    }

                    @Override
                    public void onRewardedVideoClosed() {

                        //

                    }

                    @Override
                    public void onError(com.facebook.ads.Ad ad, AdError adError) {

                        DialogHelper.showAdsFailedWarning(getApplicationContext());

                    }

                    @Override
                    public void onAdLoaded(com.facebook.ads.Ad ad) {

                        mFacebookRewardedVideoAd.show();
                    }

                    @Override
                    public void onAdClicked(com.facebook.ads.Ad ad) {

                        //

                    }
                })
                .withFailOnCacheFailureEnabled(true)
                .build());
    }

    private void onLoadAdmobRewardAds(Media media) {


        mRewardedAd = new RewardedAd(this, settingsManager.getSettings().getAdUnitIdRewarded());
        RewardedAdCallback adCallback = new RewardedAdCallback() {
            @Override
            public void onRewardedAdOpened() {
                // Ad opened.
            }

            @Override
            public void onRewardedAdClosed() {
                // Ad closed.
            }

            @Override
            public void onRewardedAdFailedToShow(com.google.android.gms.ads.AdError adError) {
                super.onRewardedAdFailedToShow(adError);


                DialogHelper.showAdsFailedWarning(EasyPlexMainPlayer.this);

                if (binding.framlayoutMediaEnded.getVisibility() == View.VISIBLE) {

                    binding.framlayoutMediaEnded.setVisibility(View.GONE);

                }
            }

            @Override
            public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {


                String mediaUrl = media.getVideos().get(0).getLink();
                String currentQuality = media.getVideos().get(0).getServer();
                mMediaModel = MediaModel.media(String.valueOf(media.getId()), String.valueOf(media.getTmdbId()),currentQuality,
                        "0", media.getTitle(), mediaUrl, media.getBackdropPath(), null,
                        null, null, null, null, null,
                        null,null,null,null,null);
                playNext(mMediaModel);

                if (binding.framlayoutMediaEnded.getVisibility() == View.VISIBLE) {

                    binding.framlayoutMediaEnded.setVisibility(View.GONE);

                }

            }
        };

        mRewardedAd.show(this, adCallback);
    }

    private void onLoadUnityAds(Media media) {

        UnityAds.initialize (this, settingsManager.getSettings().getUnityGameId(), false);


        if (UnityAds.isReady()) {
            UnityAds.show (this, "rewardedVideo");
        }



        iUnityAdsListener = new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String s) {


                //

            }

            @Override
            public void onUnityAdsStart(String s) {

                //

            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {


                String mediaUrl = media.getVideos().get(0).getLink();
                String currentQuality = media.getVideos().get(0).getServer();
                mMediaModel = MediaModel.media(String.valueOf(media.getId()), String.valueOf(media.getTmdbId()),currentQuality,
                        "0", media.getTitle(), mediaUrl, media.getBackdropPath(), null,
                        null, null, null, null, null,
                        null,null,null,null,null);
                playNext(mMediaModel);

                if (binding.framlayoutMediaEnded.getVisibility() == View.VISIBLE) {

                    binding.framlayoutMediaEnded.setVisibility(View.GONE);

                }



            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

                DialogHelper.showAdsFailedWarning(EasyPlexMainPlayer.this);

                if (binding.framlayoutMediaEnded.getVisibility() == View.VISIBLE) {

                    binding.framlayoutMediaEnded.setVisibility(View.GONE);

                }



            }
        };

        // Add the listener to the SDK:
        UnityAds.addListener(iUnityAdsListener);

    }

    private void onLoadStartAppAds(Media media) {

        startAppAd = new StartAppAd(this);

        startAppAd.setVideoListener(() -> {


            String mediaUrl = media.getVideos().get(0).getLink();
            String currentQuality = media.getVideos().get(0).getServer();
            mMediaModel = MediaModel.media(String.valueOf(media.getId()), String.valueOf(media.getTmdbId()),currentQuality,
                    "0", media.getTitle(), mediaUrl, media.getBackdropPath(), null,
                    null, null, null, null, null,
                    null,null,null,null,null);
            playNext(mMediaModel);

            if (binding.framlayoutMediaEnded.getVisibility() == View.VISIBLE) {

                binding.framlayoutMediaEnded.setVisibility(View.GONE);

            }
        });

        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

                DialogHelper.showPremuimWarning(EasyPlexMainPlayer.this);

            }
        });
    }


    // Load Next Episode for A Serie
    private void onLoadNextSerieEpisodes() {


        binding.filterBtnEnded.setOnClickListener(v -> binding.spinnerMediaEnded.performClick());

        binding.framlayoutMediaEnded.setVisibility(View.VISIBLE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        binding.framlayoutMediaEnded.startAnimation(alphaAnimation);

        binding.closeMediaEnded.setOnClickListener(v -> {

            binding.framlayoutMediaEnded.setVisibility(View.GONE);

            if(mCountDownTimer!=null){

                mCountDownTimer.cancel();

            }

        });


       onLoadSerieEpisodes();
       onLoadNextEpisodeWithTimer();


    }




        // Load Next Episode Info  for A Serie With A CountDownTimer
    private void onLoadNextEpisodeWithTimer() {

        repository.getSerieSeasons(getPlayerController().nextSeaonsID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Observer<MovieResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(MovieResponse movieResponse) {

                        if (getPlayerController().getCurrentEpisodePosition() != movieResponse.getEpisodes().size() - 1) {

                            for (int i=0; i<movieResponse.getEpisodes().size(); i++) {

                                if (getPlayerController().getEpName().equals(movieResponse.getEpisodes().get(i).getName())) {

                                    int position = i+1;

                                    mCountDownTimer = new CountDownTimer(10000, 1000) {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                            binding.textViewVideoTimeRemaining.setText(UPNEXT + millisUntilFinished / 1000 + " s");

                                            binding.ratingBar.setRating(Float.parseFloat(movieResponse.getEpisodes().get(position).getVoteAverage()) / 2);

                                            Glide.with(EasyPlexMainPlayer.this).load(movieResponse.getEpisodes().get(position).getStillPath())
                                                    .centerCrop()
                                                    .placeholder(R.drawable.placehoder_episodes)
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .into(binding.imageViewMovieNext);

                                            binding.textViewVideoNextName.setText(EP + movieResponse.getEpisodes().get(position).getEpisodeNumber() + " : " + movieResponse.getEpisodes().get(position).getName());
                                            binding.textViewVideoNextReleaseDate.setText("Season "+getPlayerController().getSeaonNumber());

                                        }

                                        @Override
                                        public void onFinish() {


                                            onCheckEpisodeHasStream(movieResponse,position);


                                        }


                                    }.start();

                                }


                            }


                        }



                    }

                    @Override
                    public void onError(Throwable e) {

                        onBackPressed();

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });


    }



    // Check if a link is exit for the next Episode before playing
    private void onCheckEpisodeHasStream(MovieResponse movieResponse,int position) {



        if (movieResponse.getEpisodes().get(position).getVideos() != null && !movieResponse.getEpisodes().get(position).getVideos().isEmpty()) {



            if (getPlayerController().isMediaPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() !=null) {

                String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
                String type = "1";
                String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();
                String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber()

                        + " : " + movieResponse.getEpisodes().get(position).getName();

                String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();

                mMediaModel = MediaModel.media(getPlayerController().getVideoID(),
                        null, currentquality, type, name, videourl, mediaCover,
                        null,
                        movieResponse.getEpisodes().get(position).getId(),
                        getPlayerController().getSeaonNumber(), String.valueOf(movieResponse.getEpisodes().get(position).getTmdbId()),
                        null, movieResponse.getEpisodes().get(position).getName(),
                        getPlayerController().getSeaonNumber(), position, null,getPlayerController().isMediaPremuim(),getPlayerController().getCurrentTvShowName());
                playNext(mMediaModel);

                binding.framlayoutMediaEnded.setVisibility(View.GONE);


            }else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && getPlayerController().isMediaPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                onLoadSubscribeDialogEpisode(movieResponse,position);

            }else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && getPlayerController().isMediaPremuim() == 0 ){


                String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
                String type = "1";
                String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();
                String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber()

                        + " : " + movieResponse.getEpisodes().get(position).getName();

                String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();

                mMediaModel = MediaModel.media(getPlayerController().getVideoID(),
                        null, currentquality, type, name, videourl, mediaCover,
                        null,
                        movieResponse.getEpisodes().get(position).getId(),
                        getPlayerController().getSeaonNumber(), String.valueOf(movieResponse.getEpisodes().get(position).getTmdbId()),
                        null, movieResponse.getEpisodes().get(position).getName(),
                        getPlayerController().getSeaonNumber(), position, null,getPlayerController().isMediaPremuim(),getPlayerController().getCurrentTvShowName());
                playNext(mMediaModel);

                binding.framlayoutMediaEnded.setVisibility(View.GONE);


            } else if (authManager.getUserInfo().getPremuim() == 1 && getPlayerController().isMediaPremuim() == 0){


                String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
                String type = "1";
                String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();
                String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber()

                        + " : " + movieResponse.getEpisodes().get(position).getName();

                String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();

                mMediaModel = MediaModel.media(getPlayerController().getVideoID(),
                        null, currentquality, type, name, videourl, mediaCover,
                        null,
                        movieResponse.getEpisodes().get(position).getId(),
                        getPlayerController().getSeaonNumber(), String.valueOf(movieResponse.getEpisodes().get(position).getTmdbId()),
                        null, movieResponse.getEpisodes().get(position).getName(),
                        getPlayerController().getSeaonNumber(), position, null,getPlayerController().isMediaPremuim(),getPlayerController().getCurrentTvShowName());
                playNext(mMediaModel);

                binding.framlayoutMediaEnded.setVisibility(View.GONE);


            }else {

                DialogHelper.showPremuimWarning(this);

            }


        } else {

            DialogHelper.showNoStreamAvailable(EasyPlexMainPlayer.this);


        }

    }



    private void onLoadSubscribeDialogEpisode (MovieResponse media, int position) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.watch_to_unlock);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.gravity = Gravity.BOTTOM;
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;

        dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {


            String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();
            if ("StartApp".equals(defaultRewardedNetworkAds)) {


                onLoadStartAppAdsEpisode(media,position);

            } else if ("UnityAds".equals(defaultRewardedNetworkAds)) {

                onLoadUnityAdsEpisode(media,position);


            } else if ("Admob".equals(defaultRewardedNetworkAds)) {


                onLoadAdmobRewardAdsEpisode(media,position);


            } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                onLoadFaceBookRewardAdsEpisode(media,position);

            }else if ("Auto".equals(defaultRewardedNetworkAds)) {

                onLoadAutoRewardAdsEpisode(media,position);

            }


            dialog.dismiss();


        });



        dialog.findViewById(R.id.text_view_go_pro).setOnClickListener(v -> {

            startActivity(new Intent(this, SettingsActivity.class));

            dialog.dismiss();


        });




        dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

                dialog.dismiss());


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void onLoadAutoRewardAdsEpisode(MovieResponse movieResponse, int position) {


        random = new Random();
        int numberOfMethods = 4;

        switch(random.nextInt(numberOfMethods)) {
            case 0:
                onLoadStartAppAdsEpisode(movieResponse, position);
                break;
            case 1:
                onLoadUnityAdsEpisode(movieResponse, position);
                break;
            case 2:
                onLoadAdmobRewardAdsEpisode(movieResponse, position);
                break;
            case 3:
                onLoadFaceBookRewardAdsEpisode(movieResponse, position);
                break;
            default:
                onLoadAdmobRewardAdsEpisode(movieResponse, position);
        }

    }

    private void onLoadFaceBookRewardAdsEpisode(MovieResponse movieResponse, int position) {

        mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(this, settingsManager.getSettings().getAdUnitIdFacebookRewarded());
        mFacebookRewardedVideoAd.loadAd(mFacebookRewardedVideoAd.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoCompleted() {

                        String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
                        String type = "1";
                        String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();
                        String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber()

                                + " : " + movieResponse.getEpisodes().get(position).getName();

                        String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(getPlayerController().getVideoID(), null, currentquality, type, name, videourl, mediaCover, null,
                                null, null, null, null, movieResponse.getEpisodes().get(position).getName(),
                                getPlayerController().getSeaonNumber(), position, null,null,getPlayerController().getCurrentTvShowName());
                        playNext(mMediaModel);

                    }

                    @Override
                    public void onLoggingImpression(com.facebook.ads.Ad ad) {


                        //

                    }

                    @Override
                    public void onRewardedVideoClosed() {

                        //

                    }

                    @Override
                    public void onError(com.facebook.ads.Ad ad, AdError adError) {

                        DialogHelper.showAdsFailedWarning(getApplicationContext());

                    }

                    @Override
                    public void onAdLoaded(com.facebook.ads.Ad ad) {

                        mFacebookRewardedVideoAd.show();
                    }

                    @Override
                    public void onAdClicked(com.facebook.ads.Ad ad) {

                        //

                    }
                })
                .withFailOnCacheFailureEnabled(true)
                .build());


    }

    private void onLoadAdmobRewardAdsEpisode(MovieResponse movieResponse, int position) {


        if (mRewardedAd == null || !mRewardedAd.isLoaded()) {
            mRewardedAd = new RewardedAd(this, settingsManager.getSettings().getAdUnitIdRewarded());
            mRewardedAd.loadAd(
                    new AdRequest.Builder().build(),
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onRewardedAdLoaded() {

                            //

                        }

                        @Override
                        public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {

                            //

                        }
                    });
        }

        if (mRewardedAd.isLoaded()) {


            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                }

                @Override
                public void onRewardedAdFailedToShow(com.google.android.gms.ads.AdError adError) {
                    super.onRewardedAdFailedToShow(adError);

                    DialogHelper.showAdsFailedWarning(getApplicationContext());

                }

                @Override
                public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {


                    String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
                    String type = "1";
                    String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();
                    String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber()

                            + " : " + movieResponse.getEpisodes().get(position).getName();

                    String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();

                    mMediaModel = MediaModel.media(getPlayerController().getVideoID(), null, currentquality, type, name, videourl, mediaCover, null,
                            null, null, null, null, movieResponse.getEpisodes().get(position).getName(),
                            null, null, null,null,getPlayerController().getCurrentTvShowName());
                    playNext(mMediaModel);

                }
            };


            mRewardedAd.show(this, adCallback);


        }

    }

    private void onLoadUnityAdsEpisode(MovieResponse movieResponse, int position) {


        UnityAds.initialize (this, settingsManager.getSettings().getUnityGameId(), false);


        if (UnityAds.isReady()) {
            UnityAds.show (this, "rewardedVideo");
        }


        iUnityAdsListener = new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String s) {


                //

            }

            @Override
            public void onUnityAdsStart(String s) {

                //

            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {


                String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
                String type = "1";
                String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();

                String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber()

                        + " : " + movieResponse.getEpisodes().get(position).getName();

                String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();

                mMediaModel = MediaModel.media(getPlayerController().getVideoID(), null, currentquality, type, name, videourl, mediaCover, null,
                        null, null, null, null, movieResponse.getEpisodes().get(position).getName(),
                        null, null, null,null,getPlayerController().getCurrentTvShowName());
                playNext(mMediaModel);


            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

                DialogHelper.showAdsFailedWarning(EasyPlexMainPlayer.this);

            }
        };

        // Add the listener to the SDK:
        UnityAds.addListener(iUnityAdsListener);
        // Initialize the SDK:

    }

    private void onLoadStartAppAdsEpisode(MovieResponse movieResponse, int position) {



        getPlayerController().triggerPlayOrPause(false);

        startAppAd = new StartAppAd(this);

        startAppAd.setVideoListener(() -> {

            String mediaCover = movieResponse.getEpisodes().get(position).getStillPath();
            String type = "1";
            String currentquality = movieResponse.getEpisodes().get(position).getVideos().get(0).getServer();
            String name = S0 + getPlayerController().getSeaonNumber() + E + movieResponse.getEpisodes().get(position).getEpisodeNumber()

                    + " : " + movieResponse.getEpisodes().get(position).getName();

            String videourl = movieResponse.getEpisodes().get(position).getVideos().get(0).getLink();

            mMediaModel = MediaModel.media(getPlayerController().getVideoID(),
                    null, currentquality, type, name, videourl, mediaCover,
                    null,
                    movieResponse.getEpisodes().get(position).getId(),
                    getPlayerController().getSeaonNumber(), String.valueOf(movieResponse.getEpisodes().get(position).getTmdbId()),
                    null, movieResponse.getEpisodes().get(position).getName(),
                    getPlayerController().getSeaonNumber(), position, null,getPlayerController().isMediaPremuim(),getPlayerController().getCurrentTvShowName());
            playNext(mMediaModel);

        });



        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

                DialogHelper.showAdsFailedWarning(EasyPlexMainPlayer.this);

            }
        });


    }


    // Return Seasons With Episodes for A Serie
    private void onLoadSerieEpisodes() {


        repository.getSerie(getPlayerController().getVideoID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Observer<Media>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(Media series) {

                        List<Season> seasons = series.getSeasons() ;

                        for(Iterator<Season> iterator = seasons.iterator(); iterator.hasNext(); ) {
                            if(iterator.next().getName().equals(SPECIALS))
                                iterator.remove();
                        }


                        binding.spinnerMediaEnded.setItem(seasons);
                        binding.spinnerMediaEnded.setSelection(0);
                        binding.spinnerMediaEnded.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                                Season season = (Season) adapterView.getItemAtPosition(position);
                                String episodeId = String.valueOf(season.getId());
                                String currentSeason = season.getName();
                                String serieId = String.valueOf(series.getTmdbId());
                                String seasonNumber = season.getSeasonNumber();


                                binding.selectedSeasonEnded.setText(season.getName());

                                // Episodes RecycleView
                                binding.rvEpisodesEnded.setHasFixedSize(true);
                                binding.rvEpisodesEnded.setNestedScrollingEnabled(false);
                                binding.rvEpisodesEnded.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                binding.rvEpisodesEnded.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getApplicationContext(), 0), true));
                                binding.rvEpisodesEnded.setItemViewCacheSize(20);
                                nextPlaySeriesAdapter = new NextPlaySeriesAdapter(EasyPlexMainPlayer.this,serieId,seasonNumber,
                                        episodeId,currentSeason, clickDetectListner, EasyPlexMainPlayer.this,authManager,settingsManager,tokenManager);
                                nextPlaySeriesAdapter.addNextPlay(season.getEpisodes());
                                binding.rvEpisodesEnded.setAdapter(nextPlaySeriesAdapter);



                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                                Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                            }
                        });


                    }

                    @Override
                    public void onError(Throwable e) {

                        Timber.tag(String.format("%s : %s", TAG, e.getCause()));

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });

    }

    @Override
    public void onLoadEpisodes() {

        binding.filterBtn.setOnClickListener(v -> binding.planetsSpinner.performClick());

        binding.frameLayoutSeasons.setVisibility(View.VISIBLE);
        binding.closeEpisode.setOnClickListener(v -> binding.frameLayoutSeasons.setVisibility(View.GONE));



        binding.viewSerieControllerTitle.setText(getPlayerController().getCurrentTvShowName());


        if (getPlayerController().getMediaType().equals("1")) {


            repository.getSerie(getPlayerController().getVideoID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<Media>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(Media series) {

                            List<Season> seasons = series.getSeasons() ;

                            for(Iterator<Season> iterator = seasons.iterator(); iterator.hasNext(); ) {
                                if(iterator.next().getName().equals(SPECIALS))
                                    iterator.remove();
                            }


                            binding.planetsSpinner.setItem(seasons);
                            binding.planetsSpinner.setSelection(0);
                            binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                    Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                                    Season season = (Season) adapterView.getItemAtPosition(position);
                                    String episodeId = String.valueOf(season.getId());
                                    String currentSeason = season.getName();
                                    String serieId = String.valueOf(series.getTmdbId());
                                    String seasonNumber = season.getSeasonNumber();


                                    binding.currentSelectedSeasons.setText(season.getName());

                                    // Episodes RecycleView
                                    binding.recyclerViewEpisodes.setHasFixedSize(true);
                                    binding.recyclerViewEpisodes.setNestedScrollingEnabled(false);
                                    binding.recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                    binding.recyclerViewEpisodes.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getApplicationContext(), 0), true));
                                    binding.recyclerViewEpisodes.setItemViewCacheSize(20);
                                    mEPAdapter = new EpisodesPlayerAdapter(EasyPlexMainPlayer.this,serieId,seasonNumber,episodeId,currentSeason,
                                            clickDetectListner, EasyPlexMainPlayer.this,authManager,settingsManager,tokenManager);
                                    mEPAdapter.addSeasons(season.getEpisodes());
                                    binding.recyclerViewEpisodes.setAdapter(mEPAdapter);



                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {


                                    //

                                }
                            });


                        }

                        @Override
                        public void onError(Throwable e) {

                            Timber.tag(""+e.getCause());

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });

        }else {


           onLoadAnimeEpisodes();


        }

    }

    @Override
    public void onLoadNextEpisode() {



        if (playerController.getMediaType().equals("1")) {

            repository.getSerieSeasons(getPlayerController().nextSeaonsID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<MovieResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(MovieResponse movieResponse) {

                            if (getPlayerController().getCurrentEpisodePosition() != movieResponse.getEpisodes().size() - 1) {

                                if (getPlayerController().getEpName().equals(movieResponse.getEpisodes().get(getPlayerController().getCurrentEpisodePosition()).getName())) {

                                    int position = getPlayerController().getCurrentEpisodePosition()+1;


                                    onCheckEpisodeHasStream(movieResponse,position);


                                }


                            }else {

                                onBackPressed();

                            }



                        }

                        @Override
                        public void onError(Throwable e) {

                            onBackPressed();

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });


        }else {


            repository.getAnimeSeasons(getPlayerController().nextSeaonsID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .cache()
                    .subscribe(new Observer<MovieResponse>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(MovieResponse movieResponse) {

                            if (getPlayerController().getCurrentEpisodePosition() != movieResponse.getEpisodes().size() - 1) {

                                if (getPlayerController().getEpName().equals(movieResponse.getEpisodes().get(getPlayerController().getCurrentEpisodePosition()).getName())) {

                                    int position = getPlayerController().getCurrentEpisodePosition()+1;


                                    onCheckEpisodeHasStream(movieResponse,position);


                                }


                            }else {

                                onBackPressed();

                            }



                        }

                        @Override
                        public void onError(Throwable e) {

                            onBackPressed();

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });
        }

    }


    // Load Episodes for Anime
    private void onLoadAnimeEpisodes() {


        animeRepository.getAnimeDetails(getPlayerController().getVideoID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(new Observer<Media>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        //

                    }

                    @Override
                    public void onNext(Media anime) {

                        List<Season> seasons = anime.getSeasons() ;

                        for(Iterator<Season> iterator = seasons.iterator(); iterator.hasNext(); ) {
                            if(iterator.next().getName().equals(SPECIALS))
                                iterator.remove();
                        }


                        binding.planetsSpinner.setItem(seasons);
                        binding.planetsSpinner.setSelection(0);
                        binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                                Season season = (Season) adapterView.getItemAtPosition(position);
                                String episodeId = String.valueOf(season.getId());
                                String currentSeason = season.getName();
                                String serieId = String.valueOf(anime.getTmdbId());
                                String seasonNumber = season.getSeasonNumber();


                                binding.currentSelectedSeasons.setText(season.getName());

                                // Episodes RecycleView
                                binding.recyclerViewEpisodes.setHasFixedSize(true);
                                binding.recyclerViewEpisodes.setNestedScrollingEnabled(false);
                                binding.recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                binding.recyclerViewEpisodes.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getApplicationContext(), 0), true));
                                binding.recyclerViewEpisodes.setItemViewCacheSize(20);
                                mEPAdapter = new EpisodesPlayerAdapter(EasyPlexMainPlayer.this,serieId,seasonNumber,episodeId,currentSeason,
                                        clickDetectListner, EasyPlexMainPlayer.this,authManager,settingsManager,tokenManager);
                                mEPAdapter.addSeasons(season.getEpisodes());
                                binding.recyclerViewEpisodes.setAdapter(mEPAdapter);



                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                                Tools.hideSystemPlayerUi(EasyPlexMainPlayer.this, true);

                            }
                        });


                    }

                    @Override
                    public void onError(Throwable e) {

                        Timber.tag(""+e.getCause());

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });
    }





    // Return List of Streaming in RecycleViews
    @Override
    public void onLoadSteaming() {
        binding.frameLayoutStreaming.setVisibility(View.VISIBLE);
        binding.closeStreaming.setOnClickListener(v -> binding.frameLayoutStreaming.setVisibility(View.GONE));

        binding.genreStreamRelative.setOnClickListener(v -> binding.spinnerMediaStreaming.performClick());

        repository.getStreamingGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GenresByID>() {
                    @Override
                    public void onSubscribe(Disposable d) {


                        //


                    }

                    @Override
                    public void onNext(GenresByID genresData) {


                        List<Genre> genres = genresData.getGenres();

                        if (!genres.isEmpty()) {


                            binding.spinnerMediaStreaming.setItem(genres);
                            binding.spinnerMediaStreaming.setSelection(0);
                            binding.spinnerMediaStreaming.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                    Genre genre = (Genre) adapterView.getItemAtPosition(position);
                                    int genreId = genre.getId();
                                    String genreName = genre.getName();


                                    binding.currentStreamingGenre.setText(genreName);


                                    repository.getStreamingByGenre(genreId)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<GenresData>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                    //

                                                }

                                                @Override
                                                public void onNext(GenresData genresData) {



                                                    List<Media> streaming = genresData.getGenres();

                                                    binding.recyclerViewStreaming.setHasFixedSize(true);
                                                    binding.recyclerViewStreaming.setNestedScrollingEnabled(false);
                                                    binding.recyclerViewStreaming.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                                    binding.recyclerViewStreaming.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getApplicationContext(), 0), true));
                                                    binding.recyclerViewStreaming.setItemViewCacheSize(20);
                                                    mStreamingAdapter = new StreamingPlayerAdapter(EasyPlexMainPlayer.this);
                                                    mStreamingAdapter.addStreaming(EasyPlexMainPlayer.this,streaming,clickDetectListner,authManager,settingsManager,tokenManager);
                                                    binding.recyclerViewStreaming.setAdapter(mStreamingAdapter);

                                                    if (mStreamingAdapter.getItemCount() == 0) {

                                                        binding.noResults.setVisibility(View.VISIBLE);
                                                        binding.viewTextGenreNameSelectedStream.setText( genreName + "Genre List is empty");


                                                    }else {

                                                        binding.noResults.setVisibility(View.GONE);

                                                    }



                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                    //

                                                }

                                                @Override
                                                public void onComplete() {

                                                    //

                                                }
                                            });

                                }


                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                    // Nothting to refresh when no Item Selected

                                }
                            });


                        }



                    }

                    @Override
                    public void onError(Throwable e) {

                        //

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });


    }


    @Override
    public void onLoadMoviesList() {

        binding.movieListBtn.setOnClickListener(v -> binding.moviesListSpinner.performClick());


        binding.frameLayoutMoviesList.setVisibility(View.VISIBLE);
        binding.closeMoviesList.setOnClickListener(v -> binding.frameLayoutMoviesList.setVisibility(View.GONE));


        repository.getMoviesGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GenresByID>() {
                    @Override
                    public void onSubscribe(Disposable d) {


                        //


                    }

                    @Override
                    public void onNext(GenresByID genresData) {


                        List<Genre> genres = genresData.getGenres();

                        if (!genres.isEmpty()) {


                            binding.moviesListSpinner.setItem(genres);
                            binding.moviesListSpinner.setSelection(0);
                            binding.moviesListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                                    Genre genre = (Genre) adapterView.getItemAtPosition(position);
                                    int genreId = genre.getId();
                                    String genreName = genre.getName();


                                    binding.viewTextMovieListGenreName.setText(genreName);


                                    repository.getMovieByGenre(genreId)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<GenresData>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                    //

                                                }

                                                @Override
                                                public void onNext(GenresData genresData) {




                                                    binding.rvFeatured.setHasFixedSize(true);
                                                    binding.rvFeatured.setNestedScrollingEnabled(false);
                                                    binding.rvFeatured.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                                    binding.rvFeatured.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getApplicationContext(), 0), true));
                                                    binding.rvFeatured.setItemViewCacheSize(20);
                                                    moviesListAdapter = new MoviesListAdapter(EasyPlexMainPlayer.this,clickDetectListner,
                                                            EasyPlexMainPlayer.this,authManager,settingsManager,tokenManager);
                                                    moviesListAdapter.addSeasons(genresData.getGenres());
                                                    binding.rvFeatured.setAdapter(moviesListAdapter);

                                                    if (moviesListAdapter.getItemCount() == 0) {

                                                        binding.noResults.setVisibility(View.VISIBLE);
                                                        binding.viewTextGenreNameSelected.setText( genreName + "Genre List is empty");


                                                    }else {

                                                        binding.noResults.setVisibility(View.GONE);

                                                    }



                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                    //

                                                }

                                                @Override
                                                public void onComplete() {

                                                    //

                                                }
                                            });

                                }


                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                    // Nothting to refresh when no Item Selected

                                }
                            });


                        }



                    }

                    @Override
                    public void onError(Throwable e) {

                        //

                    }

                    @Override
                    public void onComplete() {

                        //

                    }
                });


        if (Tools.checkIfHasNetwork(this)) {

            authRepository.getStatus()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Status>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(Status status) {

                            //

                        }

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onError(Throwable e) {

                            binding.viewStatus.setVisibility(View.VISIBLE);
                            binding.restartApp.setOnClickListener(view -> finishAffinity());
                            binding.closeStatus.setOnClickListener(view -> finishAffinity());
                            binding.viewStatus.setOnTouchListener((view, motionEvent) -> {
                                finishAffinity();
                                return false;
                            });


                            Toast.makeText(EasyPlexMainPlayer.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });
        }

    }

    @Override
    public void onLoadVlcPlayer() {

        //

    }

    @Override
    public void onPlayEmbedLinks() {

        //

    }

    @Override
    public void onAddMyList(boolean enabled) {

        if (mediaRepository.isStreamFavorite(Integer.parseInt(getPlayerController().getVideoID()))) {

            Timber.i("Removed From Watchlist");

            compositeDisposable.add(Completable.fromAction(() -> mediaRepository.removeStreamFavorite(stream))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

            Toast.makeText(this, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

        }else {

            stream.setTmdbId("0");
            stream.setLink(String.valueOf(getPlayerController().getVideoUrl()));
            Timber.i("Added To Watchlist");
            compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addStreamFavorite(stream))
                    .subscribeOn(Schedulers.io())
                    .subscribe());

            getPlayerController().checkFavorite(enabled);

        }

    }

    @Override
    public void onOpenSubsLoad() {

        //
    }

    @Override
    public void onTracksVideo() {

        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(mTrackSelector.getCurrentMappedTrackInfo());
        DefaultTrackSelector.Parameters parameters = mTrackSelector.getParameters();

        for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
            if (TrackSelectionDialog.showTabForRenderer(mappedTrackInfo, rendererIndex)) {
                int trackType = mappedTrackInfo.getRendererType(rendererIndex);

                if (trackType == C.TRACK_TYPE_VIDEO) {
                    boolean isRendererDisabled = parameters.getRendererDisabled(rendererIndex);
                    TrackSelectionDialogBuilder build = new TrackSelectionDialogBuilder(EasyPlexMainPlayer.this, "Video Qualities", mTrackSelector, rendererIndex);
                    build.setAllowAdaptiveSelections(isRendererDisabled);
                    build.setShowDisableOption(true);
                    build.build().show();
                }

            }


        }

    }

    @Override
    public void onTracksAudio() {


        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(mTrackSelector.getCurrentMappedTrackInfo());
        DefaultTrackSelector.Parameters parameters = mTrackSelector.getParameters();

        for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
            if (TrackSelectionDialog.showTabForRenderer(mappedTrackInfo, rendererIndex)) {
                int trackType = mappedTrackInfo.getRendererType(rendererIndex);

                if (trackType == C.TRACK_TYPE_AUDIO) {
                    boolean isRendererDisabled = parameters.getRendererDisabled(rendererIndex);
                    TrackSelectionDialogBuilder build = new TrackSelectionDialogBuilder(EasyPlexMainPlayer.this, "Audio", mTrackSelector, rendererIndex);
                    build.setAllowAdaptiveSelections(isRendererDisabled);
                    build.setShowDisableOption(true);
                    build.build().show();
                }

            }


        }

    }

    @Override
    public void onTracksSubstitles() {


        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(mTrackSelector.getCurrentMappedTrackInfo());
        DefaultTrackSelector.Parameters parameters = mTrackSelector.getParameters();

        for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
            if (TrackSelectionDialog.showTabForRenderer(mappedTrackInfo, rendererIndex)) {
                int trackType = mappedTrackInfo.getRendererType(rendererIndex);

                if (trackType == C.TRACK_TYPE_TEXT) {
                    boolean isRendererDisabled = parameters.getRendererDisabled(rendererIndex);
                    TrackSelectionDialogBuilder build = new TrackSelectionDialogBuilder(EasyPlexMainPlayer.this, "Substitles", mTrackSelector, rendererIndex);
                    build.setAllowAdaptiveSelections(isRendererDisabled);
                    build.setShowDisableOption(true);
                    build.build().show();
                    build.build().setOnCancelListener(dialog -> onSubstitleClicked(true));
                    build.build().setOnKeyListener((dialog, keyCode, event) -> {
                        onSubstitleClicked(true);
                        return false;
                    });

                }

            }

        }

    }

    @Override
    public void onLoadCast() {

        intentToJoin();
    }


    // Update CueIndicator when the CuePoint Received
    @Override
    public void onCuePointReceived(long[] cuePoints) {

        binding.cuepointIndictor.setText(printCuePoints(cuePoints));
    }

    @Override
    public boolean isPremuim() {


        return false;
    }



    private String printCuePoints(long[] cuePoints) {
        if (cuePoints == null) {
            return null;
        }

        return "";
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

        // On CuePoint Change

    }



    // Play Next Media (Reset and update State for the Player)
    @Override
    public void playNext(MediaModel nextVideo) {
        createMediaSource(nextVideo);
        fsmPlayer.setMovieMedia(nextVideo);
        fsmPlayer.restart();

    }


    // Update Media (Without Resetting the position )
    @Override
    public void update(MediaModel update) {
        createMediaSource(update);
        fsmPlayer.setMovieMedia(update);
        fsmPlayer.update();

    }

    @Override
    public void backState(MediaModel backstate) {
        createMediaSource(backstate);
        fsmPlayer.setMovieMedia(backstate);
        fsmPlayer.backfromApp();

    }


    @Override
    public void onStop() {
        super.onStop();
        super.onDestroy();

    }



    // Detect if a user has Selected a substitle
    @Override
    public void onSubstitleClicked(boolean clicked) {

        if (clicked) {
            binding.frameSubstitles.setVisibility(View.GONE);

        }

    }


    // Detect if a user has Selected a Quality
    @Override
    public void onQualityClicked(boolean clicked) {


        if (clicked) {
            binding.frameQualities.setVisibility(View.GONE);

        }


    }



    // Detect if a user has Selected a Stream
    @Override
    public void onStreamingclicked(boolean clicked) {

        if (clicked) {
            binding.frameLayoutStreaming.setVisibility(View.GONE);
        }

    }





    // Detect if a user has Clicked an episode or movie when the media is ended
    @Override
    public void onNextMediaClicked(boolean clicked) {


        if (clicked) {
            binding.framlayoutMediaEnded.setVisibility(View.GONE);

            if(mCountDownTimer!=null){

                mCountDownTimer.cancel();

            }

        }


    }

    @Override
    public void onMoviesListClicked(boolean clicked) {

        binding.frameLayoutMoviesList.setVisibility(View.GONE);

    }

    @Override
    public void onEpisodeClicked(boolean clicked) {

        if (clicked) {
            binding.frameLayoutSeasons.setVisibility(View.GONE);
        }

    }

    @Override
    public void onAdPlaybackState(AdPlaybackState adPlaybackState) {


        //
    }

    @Override
    public void onAdLoadError(AdsMediaSource.AdLoadException error, DataSpec dataSpec) {


        //
    }

    @Override
    public void onAdClicked() {


        //

    }

    @Override
    public void onAdTapped() {


        //

    }



    private void createAndLoadRewardedAd() {

        if ("StartApp".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            startAppAd = new StartAppAd(this);


        } else if ("UnityAds".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            UnityAds.initialize (this, settingsManager.getSettings().getUnityGameId(), false);

        } else if ("Facebook".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(this, settingsManager.getSettings().getAdUnitIdFacebookRewarded());

        }else if ("Appodeal".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            Appodeal.initialize(this, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

        }
        else if ("Auto".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(this, settingsManager.getSettings().getAdUnitIdFacebookRewarded());

            UnityAds.initialize (this, settingsManager.getSettings().getUnityGameId(), false);
            startAppAd = new StartAppAd(this);

            Appodeal.initialize(this, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

        }
    }


}
