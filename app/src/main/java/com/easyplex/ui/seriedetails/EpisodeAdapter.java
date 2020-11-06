package com.easyplex.ui.seriedetails;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;
import com.easyplex.R;
import com.easyplex.data.local.entity.Download;
import com.easyplex.data.local.entity.History;
import com.easyplex.data.model.episode.Episode;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.media.Resume;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.databinding.RowSeasonsBinding;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.ui.settings.SettingsActivity;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.NetworkUtils;
import com.easyplex.util.Tools;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdRequest;
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
import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.easyplex.util.Constants.WIFI_CHECK;

/**
 * Adapter for Series Episodes.
 *
 * @author Yobex.
 */
public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private List<Episode> episodeList;
    String currentSerieId;
    String currentSeasons;
    String seasonId;
    String currentSeasonsNumber;
    String currentTvShowName;
    int premuim;
    private SharedPreferences preferences;
    private AuthManager authManager;
    private SettingsManager settingsManager;
    private TokenManager tokenManager;
    private Context context;
    private RewardedAd mRewardedAd;
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private StartAppAd startAppAd;
    private Random random;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MediaRepository mediaRepository;
    private History historyModel;
    String serieCover;


    public EpisodeAdapter(Context context, String serieid , String seasonId, String seasonsidpostion, String currentseason , int premuim, SharedPreferences preferences
            , AuthManager authManager, String currentTvShowName, SettingsManager settingsManager, TokenManager tokenManager, MediaRepository mediaRepository,String serieCover) {

        this.currentSerieId = serieid;
        this.currentSeasons = seasonId;
        this.seasonId = seasonsidpostion;
        this.currentSeasonsNumber = currentseason;
        this.premuim = premuim;
        this.authManager = authManager;
        this.currentTvShowName = currentTvShowName;
        this.preferences = preferences;
        this.settingsManager = settingsManager;
        this.context = context;
        this.tokenManager = tokenManager;
        this.mediaRepository = mediaRepository;
        this.serieCover = serieCover;
    }

    public void addSeasons(List<Episode> episodeList) {
        this.episodeList = episodeList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowSeasonsBinding binding = RowSeasonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new EpisodeViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (episodeList != null) {
            return episodeList.size();
        } else {
            return 0;
        }
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder {

        private final RowSeasonsBinding binding;

        EpisodeViewHolder(@NonNull RowSeasonsBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        void onBind(final int position) {

            final Episode episode = EpisodeAdapter.this.episodeList.get(position);

            mRewardedAd = new RewardedAd(context, settingsManager.getSettings().getAdUnitIdRewarded());

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


            Download download = new Download(String.valueOf(episode.getId()),String.valueOf(episode.getTmdbId()),episode.getStillPath(),currentTvShowName + " : " + "S0" +
                    currentSeasons + "E" + episode.getEpisodeNumber() +
                    " : " + episode.getName(),episode.getLink());

            Tools.onLoadMediaCoverEpisode(binding.epcover,episode.getStillPath());

            binding.eptitle.setText(episode.getName());
            binding.epnumber.setText(episode.getEpisodeNumber());
            binding.epoverview.setText(episode.getOverview());



            mediaRepository.getResumeById(String.valueOf(episode.getTmdbId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Resume>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(Resume resume) {

                            if (resume !=null  && resume.getResumePosition() !=null && authManager.getUserInfo().getId() !=null && authManager.getUserInfo().getId() == resume.getUserResumeId()) {


                                double d = resume.getResumePosition();

                                double moveProgress = d * 100 / resume.getMovieDuration();


                                int inum= (int) Math.round(moveProgress);


                                try{

                                    Timber.i("%s", inum);

                                }
                                catch (ArithmeticException e) {
                                    Timber.i("Can't be divided by Zero %s", e);
                                }

                                binding.resumeProgressBar.setProgress((int) moveProgress);

                            }else {

                                binding.resumeProgressBar.setVisibility(View.GONE);

                            }

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



            binding.downloadEpisode.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {



                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((SerieDetailsActivity) context, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
                        return;
                    }



                    String[] charSequence = new String[episode.getVideos().size()];
                    for (int i = 0; i<episode.getVideos().size(); i++) {
                        charSequence[i] = String.valueOf(episode.getVideos().get(i).getServer());

                    }


                    if (episode.getVideos() !=null && !episode.getVideos().isEmpty()) {


                        if (authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken().getAccessToken() !=null) {


                        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                        builder.setTitle("Select Source Quality");
                        builder.setCancelable(true);
                        builder.setItems(charSequence, (dialogInterface, wich) -> {


                            String url = episode.getVideos().get(wich).getLink();
                            String fileName = url.substring(url.lastIndexOf('/') + 1);
                            fileName = fileName.substring(0,1).toUpperCase() + fileName.substring(1);

                            DownloadImpl.getInstance()
                                    .with(context)
                                    .target(new File(context.getCacheDir(), fileName))
                                    .setUniquePath(true)
                                    .setForceDownload(true)
                                    .setEnableIndicator(true)
                                    .setParallelDownload(true)
                                    .setIcon(R.drawable.notification_smal_size)
                                    .url(episode.getVideos().get(wich).getLink())
                                    .enqueue(new DownloadListenerAdapter() {
                                        @Override
                                        public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
                                            super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);

                                            Toast.makeText(context, "Download for " +
                                                            currentTvShowName + " : " + "S0" +
                                                            currentSeasons + "E" + episode.getEpisodeNumber() +
                                                            " : " + episode.getName() + " has been started...",
                                                    Toast.LENGTH_SHORT).show();


                                        }

                                        @Override
                                        public void onProgress(String url, long downloaded, long length, long usedTime) {
                                            super.onProgress(url, downloaded, length, usedTime);
                                            Timber.i(" progress:" + downloaded + " url:" + url);


                                        }

                                        @Override
                                        public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {

                                            download.setLink(String.valueOf(path));

                                            compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addFavorite1(download))
                                                    .subscribeOn(Schedulers.io())
                                                    .subscribe());

                                            return super.onResult(throwable, path, url, extra);


                                        }
                                    });



                        });

                        builder.show();


                        }


                    }else {

                        DialogHelper.showNoDownloadAvailableEpisode(context);

                    }

                }
            });

            binding.epLayout.setOnClickListener(v -> {




                if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                    DialogHelper.showWifiWarning(context);

                }else {


                    if (!episode.getVideos().isEmpty() && episode.getVideos() !=null) {



                        if (premuim == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {


                            onStartEpisode(episode,position);


                        } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && premuim != 1 && authManager.getUserInfo().getPremuim() == 0) {


                            onLoadSubscribeDialog(episode,position);

                        } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && premuim == 0) {


                            onStartEpisode(episode,position);


                        } else if (authManager.getUserInfo().getPremuim() == 1 && premuim == 0) {


                            onStartEpisode(episode,position);


                        } else {

                            DialogHelper.showPremuimWarning(context);

                        }

                    }else {


                        DialogHelper.showNoStreamEpisode(context);

                    }

                }


            });

        }





        private void onLoadSubscribeDialog(Episode media, int position) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_subscribe);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            lp.gravity = Gravity.BOTTOM;
            lp.width = MATCH_PARENT;
            lp.height = MATCH_PARENT;




            dialog.findViewById(R.id.view_watch_ads_to_play).setOnClickListener(v -> {


                String defaultRewardedNetworkAds = settingsManager.getSettings().getDefaultRewardedNetworkAds();
                if ("StartApp".equals(defaultRewardedNetworkAds)) {


                    onLoadStartAppAds(media,position);

                } else if ("UnityAds".equals(defaultRewardedNetworkAds)) {

                    onLoadUnityAds(media,position);


                } else if ("Admob".equals(defaultRewardedNetworkAds)) {


                    onLoadAdmobRewardAds(media,position);


                }else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                    onLoadAppOdealRewardAds(media,position);

                } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                    onLoadFaceBookRewardAds(media,position);

                }else if ("Auto".equals(defaultRewardedNetworkAds)) {

                    onLoadAutoRewardAds(media,position);

                }


                dialog.dismiss();


            });



            dialog.findViewById(R.id.text_view_go_pro).setOnClickListener(v -> {

                context.startActivity(new Intent(context, SettingsActivity.class));

                dialog.dismiss();


            });




            dialog.findViewById(R.id.bt_close).setOnClickListener(v ->

                    dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }

        private void onLoadAppOdealRewardAds(Episode episode, int position) {

            Appodeal.show((SerieDetailsActivity) context, Appodeal.REWARDED_VIDEO);

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

                    onStartEpisode(episode,position);

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


        private void onLoadAutoRewardAds(Episode media, int position) {

            random = new Random();
            int numberOfMethods = 4;

            switch(random.nextInt(numberOfMethods)) {
                case 0:
                    onLoadStartAppAds(media, position);
                    break;
                case 1:
                    onLoadUnityAds(media, position);
                    break;
                case 2:
                    onLoadAdmobRewardAds(media, position);
                    break;
                case 3:
                    onLoadFaceBookRewardAds(media, position);
                    break;
                default:
                    onLoadAdmobRewardAds(media, position);
            }

        }


    }

    private void onStartEpisode(Episode episode, int position) {

        String tvseasonid = seasonId;
        Integer currentep = Integer.parseInt(episode.getEpisodeNumber());
        String currentepname = episode.getName();
        String currenteptmdbnumber = String.valueOf(episode.getTmdbId());
        String currentseasons = currentSeasons;
        String currentseasonsNumber = currentSeasonsNumber;
        String currentepimdb = String.valueOf(episode.getTmdbId());
        String artwork = episode.getStillPath();
        String type = "1";
        String currentquality = episode.getVideos().get(0).getServer();
        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
        String videourl = episode.getVideos().get(0).getLink();
        Intent intent = new Intent(context, EasyPlexMainPlayer.class);

        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(currentSerieId, null, currentquality, type, name, videourl, artwork, null, currentep
                , currentseasons, currentepimdb, tvseasonid, currentepname, currentseasonsNumber, position, currenteptmdbnumber, premuim,currentTvShowName));
        context.startActivity(intent);

        historyModel = new History(currentSerieId, String.valueOf(episode.getTmdbId()),serieCover,name,episode.getStillPath(),videourl);
        historyModel.setSeasonsId(tvseasonid);
        historyModel.setTv("1");
        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addhistory(historyModel))
                .subscribeOn(Schedulers.io())
                .subscribe());

    }


    private void onLoadFaceBookRewardAds(Episode episode, int position) {

        mFacebookRewardedVideoAd.loadAd(mFacebookRewardedVideoAd.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoCompleted() {

                        onStartEpisode(episode,position);

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

                        DialogHelper.showAdsFailedWarning(context);

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

    private void onLoadAdmobRewardAds(Episode episode, int position) {


        if (mRewardedAd.isLoaded()) {

            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {

                    initLoadRewardedAd();
                }

                @Override
                public void onRewardedAdFailedToShow(com.google.android.gms.ads.AdError adError) {
                    super.onRewardedAdFailedToShow(adError);

                    DialogHelper.showAdsFailedWarning(context);

                }

                @Override
                public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {

                    onStartEpisode(episode,position);

                }
            };


            mRewardedAd.show((SerieDetailsActivity) context, adCallback);


        }

    }

    private void onLoadUnityAds(Episode episode, int position) {


        UnityAds.initialize ((SerieDetailsActivity) context, settingsManager.getSettings().getUnityGameId(), false);

        if (UnityAds.isReady()) {
            UnityAds.show ((SerieDetailsActivity) context, "rewardedVideo");
        }



        final IUnityAdsListener myAdsListener = new IUnityAdsListener() {
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


                onStartEpisode(episode,position);



            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

               DialogHelper.showAdsFailedWarning(context);

            }
        };

        // Add the listener to the SDK:
        UnityAds.addListener(myAdsListener);
    }

    private void onLoadStartAppAds(Episode episode, int position) {

        startAppAd = new StartAppAd(context);

        startAppAd.setVideoListener(() -> {

            String tvseasonid = seasonId;
            Integer currentep = Integer.parseInt(episode.getEpisodeNumber());
            String currentepname = episode.getName();
            String currenteptmdbnumber = String.valueOf(episode.getTmdbId());
            String currentseasons = currentSeasons;
            String currentseasonsNumber = currentSeasonsNumber;
            String currentepimdb = String.valueOf(episode.getTmdbId());
            String artwork = episode.getStillPath();
            String type = "1";
            String currentquality = episode.getVideos().get(0).getServer();
            String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
            String videourl = episode.getVideos().get(0).getLink();
            Intent intent = new Intent(context, EasyPlexMainPlayer.class);

            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(currentSerieId, null, currentquality, type, name, videourl, artwork, null, currentep
                    , currentseasons, currentepimdb, tvseasonid, currentepname, currentseasonsNumber, position, currenteptmdbnumber, premuim,currentTvShowName));
            context.startActivity(intent);


        });



        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {

                DialogHelper.showAdsFailedWarning(context);

            }
        });
    }



    private void createAndLoadRewardedAd() {

        if ("StartApp".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            startAppAd = new StartAppAd(context);


        } else if ("UnityAds".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            if (settingsManager.getSettings().getUnityGameId() !=null) {

                UnityAds.initialize ((SerieDetailsActivity) context, settingsManager.getSettings().getUnityGameId(), false);

            }

        } else if ("Facebook".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());

        }else if ("Appodeal".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            if (settingsManager.getSettings().getAdUnitIdAppodealRewarded() !=null) {

                Appodeal.initialize((SerieDetailsActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

            }

        }
        else if ("Auto".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());


            if (settingsManager.getSettings().getUnityGameId() !=null) {

                UnityAds.initialize ((SerieDetailsActivity) context, settingsManager.getSettings().getUnityGameId(), false);

            }
            startAppAd = new StartAppAd(context);


            if (settingsManager.getSettings().getAdUnitIdAppodealRewarded() !=null) {

                Appodeal.initialize((SerieDetailsActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

            }

        }
    }


    public RewardedAd initLoadRewardedAd() {
        mRewardedAd = new RewardedAd(context,
                settingsManager.getSettings().getAdUnitIdRewarded());
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
        return mRewardedAd;
    }

}
