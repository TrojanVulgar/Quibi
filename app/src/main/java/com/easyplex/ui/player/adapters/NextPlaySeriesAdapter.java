package com.easyplex.ui.player.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.R;
import com.easyplex.data.model.episode.Episode;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.ViewNextMediaItemBinding;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.settings.SettingsActivity;
import com.easyplex.util.DialogHelper;
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
import java.util.List;
import java.util.Random;

/**
 * Adapter for Next Play Episode Adapter.
 *
 * @author Yobex.
 */
public class NextPlaySeriesAdapter extends RecyclerView.Adapter<NextPlaySeriesAdapter.NextPlaySeriesViewHolder> {

    private List<Episode> castList;
    String currentSerieId;
    String currentSeasons;
    String seasonsid;
    private AuthManager authManager;
    private SettingsManager settingsManager;
    String currentSeasonsNumber;
    private EasyPlexMainPlayer player;
    private RewardedAd mRewardedAd;
    private MediaModel mMediaModel;
    ClickDetectListner clickDetectListner;
    private Context context;
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private StartAppAd startAppAd;
    private Random random;
    private TokenManager tokenManager;



    public NextPlaySeriesAdapter(Context context,String serieid , String seasonsid,
                                 String seasonsidpostion, String currentseason, ClickDetectListner
                                         clickDetectListner, EasyPlexMainPlayer player,AuthManager authManager
            ,SettingsManager settingsManager,TokenManager tokenManager) {

        this.context = context;
        this.currentSerieId = serieid;
        this.currentSeasons = seasonsid;
        this.seasonsid = seasonsidpostion;
        this.currentSeasonsNumber = currentseason;
        this.clickDetectListner = clickDetectListner;
        this.player = player;
        this.authManager = authManager;
        this.settingsManager = settingsManager;
        this.tokenManager = tokenManager;

    }

    public void addNextPlay(List<Episode> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NextPlaySeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewNextMediaItemBinding binding = ViewNextMediaItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new NextPlaySeriesAdapter.NextPlaySeriesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NextPlaySeriesViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }


    class NextPlaySeriesViewHolder extends RecyclerView.ViewHolder {

        private final ViewNextMediaItemBinding binding;

        NextPlaySeriesViewHolder (@NonNull ViewNextMediaItemBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Episode episode = castList.get(position);


            Glide.with(context).load(episode.getStillPath())
                    .centerCrop()
                    .placeholder(R.drawable.placehoder_episodes)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(1280,720)
                    .into(binding.epcover);


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


            binding.eptitle.setText(episode.getName());
            binding.epnumber.setText(episode.getEpisodeNumber()+" -");
            binding.epoverview.setText(episode.getOverview());

            binding.epLayout.setOnClickListener(v -> {

                if (episode.getVideos().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                }else {



                    if (player.getPlayerController().isMediaPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                        clickDetectListner.onNextMediaClicked(true);

                        String tvSeasonsId = seasonsid;
                        Integer currentEpisode = Integer.parseInt(episode.getEpisodeNumber());
                        String currentEpisodeName = episode.getName();
                        String currentEpisodeNumber = String.valueOf(episode.getTmdbId());
                        String currentSeason = currentSeasons;
                        String currentTvSeasonsNumber = NextPlaySeriesAdapter.this.currentSeasonsNumber;
                        String currentEpisodeTmdb = String.valueOf(episode.getTmdbId());
                        String artwork = episode.getStillPath();
                        String type = "1";
                        String currentQuality = episode.getVideos().get(0).getServer();
                        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                        String episodeStreamLink = episode.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(currentSerieId, null, currentQuality, type, name, episodeStreamLink, artwork, null, currentEpisode
                                , currentSeason, currentEpisodeTmdb,
                                tvSeasonsId, currentEpisodeName, currentTvSeasonsNumber,position,
                                currentEpisodeNumber,null, player.getPlayerController().getCurrentTvShowName());

                        player.playNext(mMediaModel);


                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && player.getPlayerController().isMediaPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                        onLoadSubscribeDialog(episode,position);

                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && player.getPlayerController().isMediaPremuim() == 0) {


                        clickDetectListner.onNextMediaClicked(true);

                        String tvSeasonsId = seasonsid;
                        Integer currentEpisode = Integer.parseInt(episode.getEpisodeNumber());
                        String currentEpisodeName = episode.getName();
                        String currentEpisodeNumber = String.valueOf(episode.getTmdbId());
                        String currentSeason = currentSeasons;
                        String currentTvSeasonsNumber = NextPlaySeriesAdapter.this.currentSeasonsNumber;
                        String currentEpisodeTmdb = String.valueOf(episode.getTmdbId());
                        String artwork = episode.getStillPath();
                        String type = "1";
                        String currentQuality = episode.getVideos().get(0).getServer();
                        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                        String episodeStreamLink = episode.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(currentSerieId, null, currentQuality, type, name, episodeStreamLink, artwork, null, currentEpisode
                                , currentSeason, currentEpisodeTmdb,
                                tvSeasonsId, currentEpisodeName, currentTvSeasonsNumber,position,
                                currentEpisodeNumber,null, player.getPlayerController().getCurrentTvShowName());

                        player.playNext(mMediaModel);


                    } else if (authManager.getUserInfo().getPremuim() == 1 && player.getPlayerController().isMediaPremuim() == 0) {

                        clickDetectListner.onNextMediaClicked(true);

                        String tvSeasonsId = seasonsid;
                        Integer currentEpisode = Integer.parseInt(episode.getEpisodeNumber());
                        String currentEpisodeName = episode.getName();
                        String currentEpisodeNumber = String.valueOf(episode.getTmdbId());
                        String currentSeason = currentSeasons;
                        String currentTvSeasonsNumber = NextPlaySeriesAdapter.this.currentSeasonsNumber;
                        String currentEpisodeTmdb = String.valueOf(episode.getTmdbId());
                        String artwork = episode.getStillPath();
                        String type = "1";
                        String currentQuality = episode.getVideos().get(0).getServer();
                        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                        String episodeStreamLink = episode.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(currentSerieId, null, currentQuality, type, name, episodeStreamLink, artwork, null, currentEpisode
                                , currentSeason, currentEpisodeTmdb,
                                tvSeasonsId, currentEpisodeName, currentTvSeasonsNumber,position,
                                currentEpisodeNumber,null, player.getPlayerController().getCurrentTvShowName());

                        player.playNext(mMediaModel);


                    } else {

                        DialogHelper.showPremuimWarning(context);

                    }


                }

            });

        }




        private void onLoadSubscribeDialog(Episode media, int position) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.watch_to_unlock);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


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

                }else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                    onLoadAppOdealRewardAds(media,position);

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





        private void onLoadFaceBookRewardAds(Episode episode, int position) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());
            mFacebookRewardedVideoAd.loadAd(mFacebookRewardedVideoAd.buildLoadAdConfig()
                    .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                        @Override
                        public void onRewardedVideoCompleted() {

                            clickDetectListner.onNextMediaClicked(true);

                            String tvSeasonsId = seasonsid;
                            Integer currentEpisode = Integer.parseInt(episode.getEpisodeNumber());
                            String currentEpisodeName = episode.getName();
                            String currentEpisodeNumber = String.valueOf(episode.getTmdbId());
                            String currentSeason = currentSeasons;
                            String currentTvSeasonsNumber = NextPlaySeriesAdapter.this.currentSeasonsNumber;
                            String currentEpisodeTmdb = String.valueOf(episode.getTmdbId());
                            String artwork = episode.getStillPath();
                            String type = "1";
                            String currentQuality = episode.getVideos().get(0).getServer();
                            String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                            String episodeStreamLink = episode.getVideos().get(0).getLink();

                            mMediaModel = MediaModel.media(currentSerieId, null, currentQuality, type, name, episodeStreamLink, artwork, null, currentEpisode
                                    , currentSeason, currentEpisodeTmdb,
                                    tvSeasonsId, currentEpisodeName, currentTvSeasonsNumber,position,
                                    currentEpisodeNumber,null, player.getPlayerController().getCurrentTvShowName());

                            player.playNext(mMediaModel);

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



        private void onLoadAppOdealRewardAds(Episode episode, int position) {

            Appodeal.show((EasyPlexMainPlayer) context, Appodeal.REWARDED_VIDEO);

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

                    clickDetectListner.onNextMediaClicked(true);

                    String tvSeasonsId = seasonsid;
                    Integer currentEpisode = Integer.parseInt(episode.getEpisodeNumber());
                    String currentEpisodeName = episode.getName();
                    String currentEpisodeNumber = String.valueOf(episode.getTmdbId());
                    String currentSeason = currentSeasons;
                    String currentTvSeasonsNumber = NextPlaySeriesAdapter.this.currentSeasonsNumber;
                    String currentEpisodeTmdb = String.valueOf(episode.getTmdbId());
                    String artwork = episode.getStillPath();
                    String type = "1";
                    String currentQuality = episode.getVideos().get(0).getServer();
                    String epName = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                    String episodeStreamLink = episode.getVideos().get(0).getLink();

                    mMediaModel = MediaModel.media(currentSerieId, null, currentQuality, type, epName, episodeStreamLink, artwork, null, currentEpisode
                            , currentSeason, currentEpisodeTmdb,
                            tvSeasonsId, currentEpisodeName, currentTvSeasonsNumber,position,
                            currentEpisodeNumber,null, player.getPlayerController().getCurrentTvShowName());

                    player.playNext(mMediaModel);

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

                        clickDetectListner.onNextMediaClicked(true);

                        String tvSeasonsId = seasonsid;
                        Integer currentEpisode = Integer.parseInt(episode.getEpisodeNumber());
                        String currentEpisodeName = episode.getName();
                        String currentEpisodeNumber = String.valueOf(episode.getTmdbId());
                        String currentSeason = currentSeasons;
                        String currentTvSeasonsNumber = NextPlaySeriesAdapter.this.currentSeasonsNumber;
                        String currentEpisodeTmdb = String.valueOf(episode.getTmdbId());
                        String artwork = episode.getStillPath();
                        String type = "1";
                        String currentQuality = episode.getVideos().get(0).getServer();
                        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                        String episodeStreamLink = episode.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(currentSerieId, null, currentQuality, type, name, episodeStreamLink, artwork, null, currentEpisode
                                , currentSeason, currentEpisodeTmdb,
                                tvSeasonsId, currentEpisodeName, currentTvSeasonsNumber,position,
                                currentEpisodeNumber,null, player.getPlayerController().getCurrentTvShowName());

                        player.playNext(mMediaModel);

                    }
                };


                mRewardedAd.show((EasyPlexMainPlayer) context, adCallback);


            }

        }

        private void onLoadUnityAds(Episode episode, int position) {


            UnityAds.initialize ((EasyPlexMainPlayer) context, settingsManager.getSettings().getUnityGameId(), false);


            if (UnityAds.isReady()) {
                UnityAds.show ((EasyPlexMainPlayer) context, "rewardedVideo");
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


                    clickDetectListner.onNextMediaClicked(true);

                    String tvSeasonsId = seasonsid;
                    Integer currentEpisode = Integer.parseInt(episode.getEpisodeNumber());
                    String currentEpisodeName = episode.getName();
                    String currentEpisodeNumber = String.valueOf(episode.getTmdbId());
                    String currentSeason = currentSeasons;
                    String currentTvSeasonsNumber = NextPlaySeriesAdapter.this.currentSeasonsNumber;
                    String currentEpisodeTmdb = String.valueOf(episode.getTmdbId());
                    String artwork = episode.getStillPath();
                    String type = "1";
                    String currentQuality = episode.getVideos().get(0).getServer();
                    String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                    String episodeStreamLink = episode.getVideos().get(0).getLink();

                    mMediaModel = MediaModel.media(currentSerieId, null, currentQuality, type, name, episodeStreamLink, artwork, null, currentEpisode
                            , currentSeason, currentEpisodeTmdb,
                            tvSeasonsId, currentEpisodeName, currentTvSeasonsNumber,position,
                            currentEpisodeNumber,null, player.getPlayerController().getCurrentTvShowName());

                    player.playNext(mMediaModel);



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

                clickDetectListner.onNextMediaClicked(true);

                clickDetectListner.onNextMediaClicked(true);

                String tvSeasonsId = seasonsid;
                Integer currentEpisode = Integer.parseInt(episode.getEpisodeNumber());
                String currentEpisodeName = episode.getName();
                String currentEpisodeNumber = String.valueOf(episode.getTmdbId());
                String currentSeason = currentSeasons;
                String currentTvSeasonsNumber = NextPlaySeriesAdapter.this.currentSeasonsNumber;
                String currentEpisodeTmdb = String.valueOf(episode.getTmdbId());
                String artwork = episode.getStillPath();
                String type = "1";
                String currentQuality = episode.getVideos().get(0).getServer();
                String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                String episodeStreamLink = episode.getVideos().get(0).getLink();

                mMediaModel = MediaModel.media(currentSerieId, null, currentQuality, type, name, episodeStreamLink, artwork, null, currentEpisode
                        , currentSeason, currentEpisodeTmdb,
                        tvSeasonsId, currentEpisodeName, currentTvSeasonsNumber, position,
                        currentEpisodeNumber, null, player.getPlayerController().getCurrentTvShowName());

                player.playNext(mMediaModel);


            });


            startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                @Override
                public void onReceiveAd(Ad ad) {
                    startAppAd.showAd();
                }

                @Override
                public void onFailedToReceiveAd(Ad ad) {

                    DialogHelper.showPremuimWarning(context);

                }
            });


        }



        private void onLoadAutoRewardAds(Episode episode, int position) {

            random = new Random();
            int numberOfMethods = 5;

            switch(random.nextInt(numberOfMethods)) {
                case 0:
                    onLoadStartAppAds(episode, position);
                    break;
                case 1:
                    onLoadUnityAds(episode, position);
                    break;
                case 2:
                    onLoadAdmobRewardAds(episode, position);
                    break;
                case 3:
                    onLoadFaceBookRewardAds(episode, position);
                    break;
                case 4:
                    onLoadAppOdealRewardAds(episode, position);
                    break;
                default:
                    onLoadAdmobRewardAds(episode, position);
            }

        }


    }



    private void createAndLoadRewardedAd() {

        if ("StartApp".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            startAppAd = new StartAppAd(context);


        } else if ("UnityAds".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            if (settingsManager.getSettings().getUnityGameId() !=null) {

                UnityAds.initialize ((EasyPlexMainPlayer) context, settingsManager.getSettings().getUnityGameId(), false);

            }

        } else if ("Facebook".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());

        }else if ("Appodeal".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            if (settingsManager.getSettings().getAdUnitIdAppodealRewarded() !=null) {

                Appodeal.initialize((EasyPlexMainPlayer) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

            }

        }
        else if ("Auto".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());


            if (settingsManager.getSettings().getUnityGameId() !=null) {

                UnityAds.initialize ((EasyPlexMainPlayer) context, settingsManager.getSettings().getUnityGameId(), false);

            }
            startAppAd = new StartAppAd(context);


            if (settingsManager.getSettings().getAdUnitIdAppodealRewarded() !=null) {

                Appodeal.initialize((EasyPlexMainPlayer) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

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
