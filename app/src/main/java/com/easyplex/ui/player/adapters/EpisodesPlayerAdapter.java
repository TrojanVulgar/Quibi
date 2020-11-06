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
import com.easyplex.R;
import com.easyplex.data.model.episode.Episode;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.RowPlayerEpisodesBinding;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.settings.SettingsActivity;
import com.easyplex.util.DialogHelper;
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
import java.util.List;
import java.util.Random;


/**
 * Adapter for Serie Episodes.
 *
 * @author Yobex.
 */
public class EpisodesPlayerAdapter extends RecyclerView.Adapter<EpisodesPlayerAdapter.EpisodesPlayerViewHolder> {

    private List<Episode> castList;
    String currenserieid;
    String currentSeasons;
    String seasonsid;
    String currentSeasonsNumber;
    MediaModel mMediaModel;
    private EasyPlexMainPlayer player;
    ClickDetectListner clickDetectListner;
    private AuthManager authManager;
    private SettingsManager settingsManager;
    private Context context;
    private RewardedAd mRewardedAd;
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private StartAppAd startAppAd;
    private Random random;
    private TokenManager tokenManager;


    public EpisodesPlayerAdapter(Context context,String serieid , String seasonsid, String seasonsidpostion, String currentseason,
                                 ClickDetectListner clickDetectListner,
                                 EasyPlexMainPlayer player,AuthManager authManager,SettingsManager settingsManager,TokenManager tokenManager) {

        this.context = context;
        this.currenserieid = serieid;
        this.currentSeasons = seasonsid;
        this.seasonsid = seasonsidpostion;
        this.currentSeasonsNumber = currentseason;
        this.clickDetectListner = clickDetectListner;
        this.player = player;
        this.authManager = authManager;
        this.settingsManager = settingsManager;
        this.tokenManager =tokenManager;

    }

    public void addSeasons(List<Episode> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EpisodesPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        RowPlayerEpisodesBinding binding = RowPlayerEpisodesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new EpisodesPlayerAdapter.EpisodesPlayerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesPlayerViewHolder holder, int position) {
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


    class EpisodesPlayerViewHolder extends RecyclerView.ViewHolder {


        private final RowPlayerEpisodesBinding binding;

        EpisodesPlayerViewHolder (@NonNull RowPlayerEpisodesBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Episode episode = castList.get(position);



            Tools.onLoadMediaCoverEpisode(binding.epcover,episode.getStillPath());

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

                        clickDetectListner.onEpisodeClicked(true);

                        String artwork = episode.getStillPath();
                        String type = "1";
                        String currentquality = episode.getVideos().get(0).getServer();
                        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                        String videourl = episode.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(null, null, currentquality, type, name, videourl, artwork, null,
                                null, null, null, null, null,
                                null,null,null,null, player.getPlayerController().getCurrentTvShowName());
                        player.playNext(mMediaModel);


                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && player.getPlayerController().isMediaPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                        onLoadSubscribeDialog(episode);

                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && player.getPlayerController().isMediaPremuim() == 0) {


                        clickDetectListner.onEpisodeClicked(true);

                        String artwork = episode.getStillPath();
                        String type = "1";
                        String currentquality = episode.getVideos().get(0).getServer();
                        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                        String videourl = episode.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(null, null, currentquality, type, name, videourl, artwork, null,
                                null, null, null, null, null,
                                null,null,null,null, player.getPlayerController().getCurrentTvShowName());
                        player.playNext(mMediaModel);


                    } else if (authManager.getUserInfo().getPremuim() == 1 && player.getPlayerController().isMediaPremuim() == 0) {


                        clickDetectListner.onEpisodeClicked(true);

                        String artwork = episode.getStillPath();
                        String type = "1";
                        String currentquality = episode.getVideos().get(0).getServer();
                        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                        String videourl = episode.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(null, null, currentquality, type, name, videourl, artwork, null,
                                null, null, null, null, null,
                                null,null,null,null, player.getPlayerController().getCurrentTvShowName());
                        player.playNext(mMediaModel);


                    } else {

                        DialogHelper.showPremuimWarning(context);

                    }

                }

            });

        }



        private void onLoadSubscribeDialog(Episode media) {

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


                    onLoadStartAppAds(media);

                } else if ("UnityAds".equals(defaultRewardedNetworkAds)) {

                    onLoadUnityAds(media);


                } else if ("Admob".equals(defaultRewardedNetworkAds)) {


                    onLoadAdmobRewardAds(media);


                } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                    onLoadFaceBookRewardAds(media);

                }else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                    onLoadAppOdealRewardAds(media);

                }else if ("Auto".equals(defaultRewardedNetworkAds)) {

                    onLoadAutoRewardAds(media);

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




        private void onLoadStartAppAds(Episode episode) {

            startAppAd = new StartAppAd(context);

            startAppAd.setVideoListener(() -> {

                clickDetectListner.onEpisodeClicked(true);

                String artwork = episode.getStillPath();
                String type = "1";
                String currentquality = episode.getVideos().get(0).getServer();
                String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                String videourl = episode.getVideos().get(0).getLink();

                mMediaModel = MediaModel.media(null, null, currentquality, type, name, videourl, artwork, null,
                        null, null, null, null, null,
                        null,null,null,null, player.getPlayerController().getCurrentTvShowName());
                player.playNext(mMediaModel);


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


        private void onLoadUnityAds(Episode episode) {



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


                    clickDetectListner.onEpisodeClicked(true);

                    String artwork = episode.getStillPath();
                    String type = "1";
                    String currentquality = episode.getVideos().get(0).getServer();
                    String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                    String videourl = episode.getVideos().get(0).getLink();

                    mMediaModel = MediaModel.media(null, null, currentquality, type, name, videourl, artwork, null,
                            null, null, null, null, null,
                            null,null,null,null, player.getPlayerController().getCurrentTvShowName());
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



        private void onLoadAdmobRewardAds(Episode episode) {

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


                        clickDetectListner.onEpisodeClicked(true);

                        String artwork = episode.getStillPath();
                        String type = "1";
                        String currentquality = episode.getVideos().get(0).getServer();
                        String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                        String videourl = episode.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(null, null, currentquality, type, name, videourl, artwork, null,
                                null, null, null, null, null,
                                null,null,null,null, player.getPlayerController().getCurrentTvShowName());
                        player.playNext(mMediaModel);

                    }
                };


                mRewardedAd.show((EasyPlexMainPlayer) context, adCallback);


            }

        }


        private void onLoadFaceBookRewardAds(Episode episode) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());

            mFacebookRewardedVideoAd.loadAd(mFacebookRewardedVideoAd.buildLoadAdConfig()
                    .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                        @Override
                        public void onRewardedVideoCompleted() {

                            clickDetectListner.onEpisodeClicked(true);

                            String artwork = episode.getStillPath();
                            String type = "1";
                            String currentquality = episode.getVideos().get(0).getServer();
                            String name = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                            String videourl = episode.getVideos().get(0).getLink();

                            mMediaModel = MediaModel.media(null, null, currentquality, type, name, videourl, artwork, null,
                                    null, null, null, null, null,
                                    null,null,null,null, player.getPlayerController().getCurrentTvShowName());
                            player.playNext(mMediaModel);

                        }

                        @Override
                        public void onLoggingImpression(com.facebook.ads.Ad ad) {

                            //
                        }

                        @Override
                        public void onRewardedVideoClosed() {

                            createAndLoadRewardedAd();

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



        private void onLoadAppOdealRewardAds(Episode episode) {

            Appodeal.show((BaseActivity) context, Appodeal.REWARDED_VIDEO);

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

                    clickDetectListner.onEpisodeClicked(true);

                    String artwork = episode.getStillPath();
                    String type = "1";
                    String currentquality = episode.getVideos().get(0).getServer();
                    String ePisodeName = "S0" + currentSeasons + "E" + episode.getEpisodeNumber() + " : " + episode.getName();
                    String videourl = episode.getVideos().get(0).getLink();

                    mMediaModel = MediaModel.media(null, null, currentquality, type, ePisodeName, videourl, artwork, null,
                            null, null, null, null, null,
                            null,null,null,null, player.getPlayerController().getCurrentTvShowName());
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


        private void onLoadAutoRewardAds(Episode episode) {

            random = new Random();
            int numberOfMethods = 5;

            switch(random.nextInt(numberOfMethods)) {
                case 0:
                    onLoadStartAppAds(episode);
                    break;
                case 1:
                    onLoadUnityAds(episode);
                    break;
                case 2:
                    onLoadAdmobRewardAds(episode);
                    break;
                case 3:
                    onLoadFaceBookRewardAds(episode);
                    break;
                case 4:
                    onLoadAppOdealRewardAds(episode);
                    break;
                default:
                    onLoadAdmobRewardAds(episode);
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
