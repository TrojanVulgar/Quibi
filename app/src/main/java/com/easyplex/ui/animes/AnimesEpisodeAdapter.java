package com.easyplex.ui.animes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.appodeal.ads.Appodeal;
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
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.ui.seriedetails.SerieDetailsActivity;
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
import static com.easyplex.util.Constants.Anime;
import static com.easyplex.util.Constants.WIFI_CHECK;

/**
 * Adapter for Anime Episodes.
 *
 * @author Yobex.
 */
public class AnimesEpisodeAdapter extends RecyclerView.Adapter<AnimesEpisodeAdapter.AnimeEpisodeViewHolder> {

    private List<Episode> episodeList;

    String serieCover;
    String currenserieid;
    String currentSeasons;
    String currentTvShowName;
    String seasonsid;
    String currentSeasonsNumber;
    int premuim;
    private SharedPreferences preferences;
    private AuthManager authManager;
    private SettingsManager settingsManager;
    private Context context;
    private RewardedAd mRewardedAd;
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private StartAppAd startAppAd;
    private Random random;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MediaRepository mediaRepository;
    private TokenManager tokenManager;
    private History history;

    public AnimesEpisodeAdapter (String serieid , String seasonsid, String seasonsidpostion, String currentseason, SharedPreferences preferences,AuthManager authManager

    ,SettingsManager settingsManager,MediaRepository mediaRepository,String currentTvShowName,int premuim,TokenManager tokenManager,Context context,String serieCover) {

        this.currenserieid = serieid;
        this.currentSeasons = seasonsid;
        this.seasonsid = seasonsidpostion;
        this.preferences = preferences;
        this.authManager = authManager;
        this.settingsManager = settingsManager;
        this.currentSeasonsNumber = currentseason;
        this.currentTvShowName = currentTvShowName;
        this.premuim = premuim;
        this.tokenManager = tokenManager;
        this.mediaRepository = mediaRepository;
        this.serieCover = serieCover;
        this.context = context;
    }


    public void addSeasons(List<Episode> castList) {
        this.episodeList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimeEpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        RowSeasonsBinding binding = RowSeasonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new AnimesEpisodeAdapter.AnimeEpisodeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeEpisodeViewHolder holder, int position) {
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

    class AnimeEpisodeViewHolder extends RecyclerView.ViewHolder {


        private final RowSeasonsBinding binding;


        AnimeEpisodeViewHolder (@NonNull RowSeasonsBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Episode episode = episodeList.get(position);

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


                        }else {


                            DialogHelper.showPremuimWarning(context);
                        }



                    }else {

                        DialogHelper.showNoDownloadAvailableEpisode(context);

                    }

                }
            });

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

                                    Timber.i("Can't be divided by Zero"+ e);
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


            binding.eptitle.setText(episode.getName());
            binding.epnumber.setText(episode.getEpisodeNumber());
            binding.epoverview.setText(episode.getOverview());

            binding.epLayout.setOnClickListener(v -> {


                if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                    DialogHelper.showWifiWarning(context);

                }else {


                    if (!episode.getVideos().isEmpty() && episode.getVideos() !=null) {



                        if (premuim == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                           onStartEpisodeStream(episode,position);


                        } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && premuim != 1 && authManager.getUserInfo().getPremuim() == 0) {


                            onLoadSubscribeDialog(episode,position);

                        } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && premuim == 0) {


                            onStartEpisodeStream(episode,position);


                        } else if (authManager.getUserInfo().getPremuim() == 1 && premuim == 0) {


                            onStartEpisodeStream(episode,position);

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


    }

    private void onStartEpisodeStream(Episode episode, int position) {

        Integer episodepostionnumber = position;
        String tvseasonid = seasonsid;
        Integer currentep = Integer.parseInt(episode.getEpisodeNumber());
        String currentepname = episode.getName();
        String currenteptmdbnumber = String.valueOf(episode.getTmdbId());
        String currentseasons = currentSeasons;
        String currentseasonsNumber = currentSeasonsNumber;
        String currentepimdb = String.valueOf(episode.getTmdbId());
        String artwork = episode.getStillPath();
        String type = Anime;
        String currentquality = episode.getVideos().get(0).getServer();
        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
        String videourl = episode.getVideos().get(0).getLink();
        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(currenserieid, null,currentquality, type, name, videourl, artwork, null, currentep
                , currentseasons, currentepimdb,tvseasonid,
                currentepname,currentseasonsNumber,episodepostionnumber,
                currenteptmdbnumber,null,null));
        context.startActivity(intent);



        history  = new History(currenserieid, String.valueOf(episode.getTmdbId()),serieCover,name,episode.getStillPath(),videourl);

        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addhistory(history))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    private void createAndLoadRewardedAd() {

        if ("StartApp".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            startAppAd = new StartAppAd(context);


        } else if ("UnityAds".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            if (settingsManager.getSettings().getUnityGameId() !=null) {

                UnityAds.initialize ((AnimeDetailsActivity) context, settingsManager.getSettings().getUnityGameId(), false);

            }

        } else if ("Facebook".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());

        }else if ("Appodeal".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            if (settingsManager.getSettings().getAdUnitIdAppodealRewarded() !=null) {

                Appodeal.initialize((AnimeDetailsActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

            }

        }
        else if ("Auto".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());


            if (settingsManager.getSettings().getUnityGameId() !=null) {

                UnityAds.initialize ((AnimeDetailsActivity) context, settingsManager.getSettings().getUnityGameId(), false);

            }
            startAppAd = new StartAppAd(context);


            if (settingsManager.getSettings().getAdUnitIdAppodealRewarded() !=null) {

                Appodeal.initialize((AnimeDetailsActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

            }

        }
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


    private void onLoadFaceBookRewardAds(Episode episode, int position) {

        mFacebookRewardedVideoAd.loadAd(mFacebookRewardedVideoAd.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoCompleted() {

                        onStartEpisodeStream(episode,position);

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

                    onStartEpisodeStream(episode,position);

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


                onStartEpisodeStream(episode,position);



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

        startAppAd.setVideoListener(() -> onStartEpisodeStream(episode,position));


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
