package com.easyplex.ui.moviedetails;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;
import com.downloader.PRDownloader;
import com.easyplex.R;
import com.easyplex.data.local.entity.Download;
import com.easyplex.data.local.entity.History;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.data.model.substitles.MediaSubstitle;
import com.easyplex.data.repository.SettingsRepository;
import com.easyplex.databinding.ItemMovieDetailBinding;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.moviedetails.adapters.CastAdapter;
import com.easyplex.ui.moviedetails.adapters.RelatedsAdapter;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.ui.settings.SettingsActivity;
import com.easyplex.ui.viewmodels.MovieDetailViewModel;
import com.easyplex.util.Constants;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.NetworkUtils;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;
import dagger.android.AndroidInjection;
import timber.log.Timber;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.easyplex.util.Constants.ARG_MOVIE;
import static com.easyplex.util.Constants.WIFI_CHECK;
import static java.lang.System.*;


/**
 * EasyPlex - Movies - Live Streaming - TV Series, Anime
 *
 * @author @Y0bEX
 * @package  EasyPlex - Movies - Live Streaming - TV Series, Anime
 * @copyright Copyright (c) 2020 Y0bEX,
 * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile https://codecanyon.net/user/yobex
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/


public class MovieDetailsActivity extends AppCompatActivity{


    ItemMovieDetailBinding binding;
    AdView mAdView;
    Random random;
    private com.facebook.ads.AdView facebookBanner;

    @Inject ViewModelProvider.Factory viewModelFactory;
    private MovieDetailViewModel movieDetailViewModel;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    SettingsManager settingsManager;

    @Inject
    SettingsRepository authRepository;


    @Inject
    AuthManager authManager;

    @Inject
   TokenManager tokenManager;


    CastAdapter mCastAdapter;
    RelatedsAdapter mRelatedsAdapter;
    private boolean mMovie;
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private StartAppAd startAppAd;
    private RewardedAd mRewardedAd;
    private IUnityAdsListener iUnityAdsListener;
    private Download download;
    private History history;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.item_movie_detail);

        mMovie = false;
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.itemDetailContainer.setVisibility(View.GONE);
        binding.mPlay.setVisibility(View.GONE);

        Intent intent = getIntent();

        Media media = intent.getParcelableExtra(ARG_MOVIE);


        mRewardedAd = new RewardedAd(this, settingsManager.getSettings().getAdUnitIdRewarded());

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        mRewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        Appodeal.initialize(this, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

        UnityAds.initialize (this, settingsManager.getSettings().getUnityGameId(), false);

        startAppAd = new StartAppAd(this);

        mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(this, settingsManager.getSettings().getAdUnitIdFacebookRewarded());


        download = new Download(media.getId(),media.getTmdbId(),media.getBackdropPath(),media.getTitle(),media.getLink());

        history  = new History(media.getId(),media.getTmdbId(),media.getPosterPath(),media.getTitle(),media.getBackdropPath(),media.getLink());

        Tools.hideSystemPlayerUi(this,true,0);

        Tools.setSystemBarTransparent(this);

        PRDownloader.initialize(getApplicationContext());

        // ViewModel to cache, retrieve data for MovieDetailsActivity
        movieDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel.class);

        movieDetailViewModel.getMovieDetails(media.getTmdbId());
        movieDetailViewModel.movieDetailMutableLiveData.observe(this, movieDetail -> {
                onLoadImage(movieDetail.getPosterPath());
                onLoadTitle(movieDetail.getTitle());
                onLoadDate(movieDetail.getReleaseDate());
                onLoadAppBar(movieDetail.getTitle());
                onLoadSynopsis(movieDetail.getOverview());
                onLoadGenres(movieDetail.getGenres());
                onLoadBackButton();
                onLoadRelatedsMovies(Integer.parseInt(movieDetail.getId()));
                onLoadCast(Integer.parseInt(movieDetail.getTmdbId()));
                onLoadRating(movieDetail.getVoteAverage());
                onLoadPogressResume(movieDetail.getTmdbId());

                if (authManager.getUserInfo().getPremuim() != 1 ) {

                    onLoadBanner();
                    onLoadFacebookBanner();

                }else {

                    binding.bannerContainer.setVisibility(View.GONE);
                    binding.adViewContainer.setVisibility(View.GONE);
                }



                binding.downloadMovie.setOnClickListener(v -> onDownloadMovie(download,movieDetail,movieDetail.getTitle()));

                binding.report.setOnClickListener(v -> onLoadReport(movieDetail.getTitle(),movieDetail.getPosterPath()));
                binding.ButtonPlayTrailer.setOnClickListener(v -> onLoadTrailer(movieDetail.getPreviewPath(),movieDetail.getTitle(),movieDetail.getBackdropPath()));
                binding.shareIcon.setOnClickListener(v -> onLoadShare(movieDetail.getTitle(),Integer.parseInt(movieDetail.getTmdbId())));

                binding.favoriteIcon.setOnClickListener(view -> {
                    binding.favoriteIcon.toggleWishlisted();
                    onFavoriteClick(movieDetail);
                });

                movieDetailViewModel.isFavorite(Integer.parseInt(movieDetail.getTmdbId())).observe(this, favMovieDetail1 -> {
                    binding.favoriteIcon.setActivated(favMovieDetail1 != null);
                    binding.favoriteIcon.setVisibility(View.VISIBLE);
                });



                binding.PlayButtonIcon.setOnClickListener(v -> {

                    if (sharedPreferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(this)) {

                        DialogHelper.showWifiWarning(this);

                    }else {


                        if (movieDetail.getVideos() !=null && !movieDetail.getVideos().isEmpty()) {


                            onCheckStream(movieDetail);


                        }else {


                            DialogHelper.showNoStreamAvailable(this);

                        }

                        }

                });




                mMovie = true;
                checkAllDataLoaded();


            });




        // Load Facebook Audience Interstitial ads
        if (settingsManager.getSettings().getAdFaceAudienceInterstitial() == 1 && authManager.getUserInfo().getPremuim() != 1 ) {

            Tools.onLoadFacebookAudience(this,settingsManager.getSettings().getAdFaceAudienceInterstitial()

                    ,settingsManager.getSettings().getFacebookShowInterstitial()

                    ,settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience());
        }




        // Load  Admob Interstitial Ads
        if (settingsManager.getSettings().getAdInterstitial() == 1  && authManager.getUserInfo().getPremuim() != 1) {


            Tools.onLoadAdmobInterstitialAds(this, settingsManager.getSettings().getAdInterstitial(),
                    settingsManager.getSettings().getAdShowInterstitial(),
                    settingsManager.getSettings().getAdUnitIdInterstitial());

        }


        onLoadFacebookBanner();

    }



    private void onLoadFacebookBanner() {


        if (authManager.getUserInfo().getPremuim() !=1){

            facebookBanner = new com.facebook.ads.AdView(this, settingsManager.getSettings().getAdUnitIdFacebookBannerAudience(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            binding.bannerContainer.addView(facebookBanner);
            facebookBanner.loadAd();

        }else {

            binding.bannerContainer.setVisibility(View.GONE);
        }


    }

    public RewardedAd createAndLoadRewardedAd() {
        mRewardedAd = new RewardedAd(this,
                settingsManager.getSettings().getAdUnitIdRewarded());
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        mRewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return mRewardedAd;
    }


    private void onCheckStream(Media movieDetail) {



        if (movieDetail.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() !=null) {

            onLoadStream(movieDetail.getVideos(), movieDetail.getTitle(), movieDetail.getBackdropPath(), Integer.parseInt(movieDetail.getTmdbId()),movieDetail.getPremuim());


        }else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && movieDetail.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


            onLoadSubscribeDialog(movieDetail);

        }else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && movieDetail.getPremuim() == 0 ){


            onLoadStream(movieDetail.getVideos(), movieDetail.getTitle(), movieDetail.getBackdropPath(), Integer.parseInt(movieDetail.getTmdbId()),movieDetail.getPremuim());


        } else if (authManager.getUserInfo().getPremuim() == 1 && movieDetail.getPremuim() == 0){


            onLoadStream(movieDetail.getVideos(), movieDetail.getTitle(), movieDetail.getBackdropPath(), Integer.parseInt(movieDetail.getTmdbId()),movieDetail.getPremuim());


        }else {

            DialogHelper.showPremuimWarning(this);

        }

    }



    private void onLoadPogressResume(String tmdbId) {


        movieDetailViewModel.getResumeMovie(tmdbId);
        movieDetailViewModel.resumeMutableLiveData.observe(this, resume -> {


            if (resume !=null  && resume.getResumePosition() !=null &&

                    authManager.getUserInfo().getId() !=null && authManager.getUserInfo().getId() == resume.getUserResumeId()) {


                double d = resume.getResumePosition();

                double moveProgress = d * 100 / resume.getMovieDuration();


                int inum= (int) Math.round(moveProgress);


                binding.timeRemaning.setText(100 - inum + " min remaining");

                try{
                    out.println(inum);

                }
                catch (ArithmeticException e) {
                    out.println ("Can't be divided by Zero " + e);
                }

                binding.resumeProgressBar.setProgress((int) moveProgress);

            }else {

                binding.resumeProgressBar.setProgress(0);

            }


            if (resume.getResumePosition() !=null && authManager.getUserInfo().getId() == resume.getUserResumeId()) {


                binding.resumeProgressBar.setVisibility(View.VISIBLE);
                binding.timeRemaning.setVisibility(View.VISIBLE);

            }else {

                binding.resumeProgressBar.setVisibility(View.GONE);
                binding.timeRemaning.setVisibility(View.GONE);
            }

        });



    }


    @Override
    protected void onResume() {
        super.onResume();

        createAndLoadRewardedAd();
    }

    private void onDownloadMovie(Download movieDetail, Media media, String movieName) {


        if (ContextCompat.checkSelfPermission(MovieDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MovieDetailsActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
            return;
        }



        String[] charSequence = new String[media.getVideos().size()];
        for (int i = 0; i<media.getVideos().size(); i++) {
            charSequence[i] = String.valueOf(media.getVideos().get(i).getServer());

        }


        if (media.getVideos() !=null && !media.getVideos().isEmpty()) {


            if (authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken().getAccessToken() !=null) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
                builder.setTitle("Select Source Quality");
                builder.setCancelable(true);
                builder.setItems(charSequence, (dialogInterface, wich) -> {


                    String url = media.getVideos().get(wich).getLink();
                    String fileName = url.substring(url.lastIndexOf('/') + 1);
                    fileName = fileName.substring(0,1).toUpperCase() + fileName.substring(1);

                    DownloadImpl.getInstance()
                            .with(getApplicationContext())
                            .target(new File(getCacheDir(), fileName))
                            .setUniquePath(true)
                            .setForceDownload(true)
                            .setEnableIndicator(true)
                            .setParallelDownload(true)
                            .setIcon(R.drawable.notification_smal_size)
                            .url(media.getVideos().get(wich).getLink())
                            .enqueue(new DownloadListenerAdapter() {
                                @Override
                                public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
                                    super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);

                                    Toast.makeText(MovieDetailsActivity.this, "Download for " + movieName + " has been started...", Toast.LENGTH_SHORT).show();


                                }

                                @Override
                                public void onProgress(String url, long downloaded, long length, long usedTime) {
                                    super.onProgress(url, downloaded, length, usedTime);
                                    Timber.i(" progress:" + downloaded + " url:" + url);


                                }

                                @Override
                                public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {

                                    movieDetail.setLink(String.valueOf(path));

                                    movieDetailViewModel.addFavorite1(movieDetail);

                                    extra.getMimetype();


                                    return super.onResult(throwable, path, url, extra);


                                }
                            });



                });

                builder.show();

            }else {



                DialogHelper.showPremuimWarning(this);


            }



        }else {

            DialogHelper.showNoDownloadAvailable(this);

        }

    }



    private void onLoadSubscribeDialog(Media movieDetail) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_subscribe);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.gravity = Gravity.BOTTOM;
        lp.width = MATCH_PARENT;
        lp.height = MATCH_PARENT;


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


            } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                onLoadFaceBookRewardAds(movieDetail);

            }else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                onLoadAppOdealRewardAds(movieDetail);

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

    private void onLoadFaceBookRewardAds(Media movieDetail) {

        mFacebookRewardedVideoAd.loadAd(mFacebookRewardedVideoAd.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoCompleted() {

                        onLoadStream(movieDetail.getVideos(), movieDetail.getTitle(), movieDetail.getBackdropPath(),

                                Integer.parseInt(movieDetail.getTmdbId()),movieDetail.getPremuim());


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

    private void onLoadAdmobRewardAds(Media movieDetail) {

        if (mRewardedAd.isLoaded()) {

            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {

                    createAndLoadRewardedAd();

                }

                @Override
                public void onRewardedAdFailedToShow(com.google.android.gms.ads.AdError adError) {
                    super.onRewardedAdFailedToShow(adError);

                    DialogHelper.showAdsFailedWarning(getApplicationContext());


                }

                @Override
                public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {


                    onLoadStream(movieDetail.getVideos(),
                            movieDetail.getTitle(),
                            movieDetail.getBackdropPath(),
                            Integer.parseInt(movieDetail.getTmdbId()),movieDetail.getPremuim());


                }
            };


            mRewardedAd.show(this, adCallback);


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

                onLoadStream(media.getVideos(),
                        media.getTitle(),
                        media.getBackdropPath(),
                        Integer.parseInt(media.getTmdbId()),media.getPremuim());

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

    private void onLoadUnityAds(Media movieDetail) {

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


                onLoadStream(movieDetail.getVideos(), movieDetail.getTitle(), movieDetail.getBackdropPath(),
                        Integer.parseInt(movieDetail.getTmdbId()),movieDetail.getPremuim());
                UnityAds.removeListener(iUnityAdsListener);


            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

                DialogHelper.showAdsFailedWarning(MovieDetailsActivity.this);

            }
        };

        // Add the listener to the SDK:
        UnityAds.addListener(iUnityAdsListener);

    }

    private void onLoadStartAppAds(Media movieDetail) {

        startAppAd = new StartAppAd(this);

        startAppAd.setVideoListener(() -> onLoadStream(movieDetail.getVideos(), movieDetail.getTitle(), movieDetail.getBackdropPath(),
                Integer.parseInt(movieDetail.getTmdbId()),movieDetail.getPremuim()));

        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {

                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

                DialogHelper.showAdsFailedWarning(MovieDetailsActivity.this);

            }
        });
    }

    private void onLoadBanner() {

        if (settingsManager.getSettings().getAdUnitIdBanner() !=null) {

            AdSize adSize = getAdSize();
            // Create an ad request.
            mAdView = new AdView(this);
            mAdView.setAdUnitId(settingsManager.getSettings().getAdUnitIdBanner());
            binding.adViewContainer.removeAllViews();
            binding.adViewContainer.addView(mAdView);
            mAdView.setAdSize(adSize);

            AdRequest adRequest = new AdRequest.Builder().build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);

        }

    }


    // Determine the screen width (less decorations) to use for the ad width.
    private AdSize getAdSize() {

        Display display = this.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = binding.adViewContainer.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);


    }


    // Load the Movie Rating
    private void onLoadRating(float rating) {

        binding.ratingBar.setRating(rating / 2);
        binding.viewMovieRating.setText(String.valueOf(rating));


    }



    // Animate the AppBar
    private void onLoadAppBar(final String title) {

        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (appBarLayout.getTotalScrollRange() + verticalOffset == 0) {
                if (title != null){


                    binding.collapsingToolbar.setTitle(title);
                    binding.collapsingToolbar.setCollapsedTitleTextColor(Color.parseColor("#FFFFFF"));
                    binding.collapsingToolbar.setContentScrimColor(Color.parseColor("#E6070707"));

                }

                else
                    binding.collapsingToolbar.setTitle("");
                binding.toolbar.setVisibility(View.VISIBLE);


            } else {
                binding.collapsingToolbar.setTitle("");
                binding.toolbar.setVisibility(View.INVISIBLE);
            }
        });



    }



    // Send report for this Movie
    private void onLoadReport(String title,String posterpath) {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_report);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WRAP_CONTENT;
        lp.height = WRAP_CONTENT;

        EditText editTextMessage = dialog.findViewById(R.id.et_post);
        TextView reportMovieName = dialog.findViewById(R.id.movietitle);
        ImageView imageView = dialog.findViewById(R.id.item_movie_image);


        reportMovieName.setText(title);


        Tools.onLoadMediaCover(imageView,posterpath);


        dialog.findViewById(R.id.bt_cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.bt_submit).setOnClickListener(v -> {


            editTextMessage.getText().toString().trim();


            if (editTextMessage.getText() !=null) {

                movieDetailViewModel.sendReport(title,editTextMessage.getText().toString());
                movieDetailViewModel.reportMutableLiveData.observe(MovieDetailsActivity.this, report -> {


                    if (report !=null) {


                        dialog.dismiss();


                        Toast.makeText(this, "Your report has been submitted successfully", Toast.LENGTH_SHORT).show();

                    }


                });

            }


        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);




    }


    // Handle Favorite Button Click to add or remove the from MyList
    public void onFavoriteClick(Media mediaDetail) {
        if (binding.favoriteIcon.isActivated()) {
            Toast.makeText(getApplicationContext(), "Added: " + mediaDetail.getTitle(),
                    Toast.LENGTH_SHORT).show();
            movieDetailViewModel.addFavorite(mediaDetail);
        } else {
            Toast.makeText(getApplicationContext(), "Removed: " + mediaDetail.getTitle(),
                    Toast.LENGTH_SHORT).show();
            movieDetailViewModel.removeFavorite(mediaDetail);
        }
    }



    // Get Movie Cast
    private void onLoadCast(int imdb) {

        movieDetailViewModel.getMovieCast(imdb);
        movieDetailViewModel.movieCreditsMutableLiveData.observe(this, credits -> {
            mCastAdapter = new CastAdapter(settingsManager);
            mCastAdapter.addCasts(credits.getCasts());

            // Starring RecycleView
            binding.recyclerViewCastMovieDetail.setAdapter(mCastAdapter);
            binding.recyclerViewCastMovieDetail.setHasFixedSize(true);
            binding.recyclerViewCastMovieDetail.setNestedScrollingEnabled(false);
            binding.recyclerViewCastMovieDetail.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerViewCastMovieDetail.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));



        });


    }


    // Load Relateds Movies
    private void onLoadRelatedsMovies(int id) {

        movieDetailViewModel.getRelatedsMovies(id);
        movieDetailViewModel.movieRelatedsMutableLiveData.observe(this, relateds -> {
            mRelatedsAdapter = new RelatedsAdapter();
            mRelatedsAdapter.addToContent(relateds.getRelateds());

            // Relateds Movies RecycleView

            binding.rvMylike.setAdapter(mRelatedsAdapter);
            binding.rvMylike.setHasFixedSize(true);
            binding.rvMylike.setNestedScrollingEnabled(false);
            binding.rvMylike.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvMylike.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));


            if (mRelatedsAdapter.getItemCount() == 0) {

                binding.relatedNotFound.setVisibility(View.VISIBLE);

            }else {

                binding.relatedNotFound.setVisibility(View.GONE);

            }



        });
    }






    // Load Stream if Added
    private void onLoadStream(List<MediaStream> videos, String movieTitle, String backdropPath, int moveId,int premuim) {

            Intent intent = new Intent(this, EasyPlexMainPlayer.class);
            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(String.valueOf(moveId),null,videos.get(0).getServer(),"0", movieTitle,
                    videos.get(0).getLink(), backdropPath, null, null
                    , null,null,null,null,null,null,null,null,null));
            startActivity(intent);


            history.setLink(videos.get(0).getLink());
            history.setTv("0");
            history.setPremuim(premuim);
            movieDetailViewModel.addhistory(history);


    }


    // Share the Movie
    private void onLoadShare(String title, int imdb) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg)+ " " + title + " For more information please check"+("https://www.imdb.com/title/tt" + imdb));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

    private void onLoadBackButton() {

        binding.backbutton.setOnClickListener(v -> {
            onBackPressed();
            Animatoo.animateSplit(MovieDetailsActivity.this);

        });
    }


    // Load The Trailer
    private void onLoadTrailer(String previewPath,String title,String backdrop) {


        if (sharedPreferences.getBoolean(Constants.WIFI_CHECK, false) &&
                NetworkUtils.isWifiConnected(this)) {

            DialogHelper.showWifiWarning(MovieDetailsActivity.this);

        }else {

            Tools.startTrailer(this,previewPath,title,backdrop);

        }


    }


    // Display Movie Poster
    private void onLoadImage(String imageURL){

        Tools.onLoadMediaCover(binding.imageMoviePoster,imageURL);

    }

    // Display Movie Title
    private void onLoadTitle(String title){

        binding.textMovieTitle.setText(title);
    }


    // Display Movie Release Date
    private void onLoadDate(String date){

        if (date != null && !date.trim().isEmpty()) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
            try {
                Date releaseDate = sdf1.parse(date);
                binding.textMovieRelease.setText(sdf2.format(releaseDate));
            } catch (ParseException e) {

                Timber.d("%s", Arrays.toString(e.getStackTrace()));

            }
        } else {
            binding.textMovieRelease.setText("");
        }
    }


    // Display Movie Synopsis or Overview
    private void onLoadSynopsis(String synopsis){
        binding.textOverviewLabel.setText(synopsis);
    }



    // Movie Genres
    private void onLoadGenres(List<Genre> genresList) {
        String genres = "";
        if (genresList != null) {
            for (int i = 0; i < genresList.size(); i++) {
                if (genresList.get(i) == null) continue;
                if (i == genresList.size() - 1) {
                    genres = genres.concat(genresList.get(i).getName());
                } else {
                    genres = genres.concat(genresList.get(i).getName() + ", ");
                }
            }
        }
        binding.mgenres.setText(genres);
    }


    private void checkAllDataLoaded() {
        if (mMovie ) {
            binding.progressBar.setVisibility(View.GONE);
            binding.itemDetailContainer.setVisibility(View.VISIBLE);
            binding.mPlay.setVisibility(View.VISIBLE);

        }
    }



    // Destroy the views on exit
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        binding.adViewContainer.removeAllViews();

        if (mAdView !=null) {

            mAdView.removeAllViews();
            mAdView.destroy();

        }
        binding = null;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), BaseActivity.class);
        startActivity(intent);
        finish();
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Tools.hideSystemPlayerUi(this,true,0);
        }
    }

}