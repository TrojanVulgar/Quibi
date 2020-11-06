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
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.episode.Episode;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.RowPlayerLivetvBinding;
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

import static com.easyplex.util.Constants.STREAMING;


/**
 * Adapter for Streaming.
 *
 * @author Yobex.
 */
public class StreamingPlayerAdapter extends RecyclerView.Adapter<StreamingPlayerAdapter.StreamingViewHolder> {

    private List<Media> streamingList;
    MediaModel mMediaModel;
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private StartAppAd startAppAd;
    private EasyPlexMainPlayer mainPlayer;
    private RewardedAd mRewardedAd;
    private Random random;
    ClickDetectListner clickDetectListner;
    private AuthManager authManager;
    private TokenManager tokenManager;
    private SettingsManager settingsManager;
    private Context context;



    public StreamingPlayerAdapter(EasyPlexMainPlayer mainPlayer){

        this.mainPlayer = mainPlayer;
    }

    public void addStreaming(Context context,List<Media> castList, ClickDetectListner clickDetectListner,AuthManager authManager,SettingsManager settingsManager
    ,TokenManager tokenManager) {
        this.context = context;
        this.streamingList = castList;
        notifyDataSetChanged();
        this.clickDetectListner = clickDetectListner;
        this.authManager = authManager;
        this.settingsManager = settingsManager;
        this.tokenManager = tokenManager;

    }

    @NonNull
    @Override
    public StreamingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        RowPlayerLivetvBinding binding = RowPlayerLivetvBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new StreamingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StreamingViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (streamingList != null) {
            return streamingList.size();
        } else {
            return 0;
        }
    }


    class StreamingViewHolder extends RecyclerView.ViewHolder {


        private final RowPlayerLivetvBinding binding;

        StreamingViewHolder (@NonNull RowPlayerLivetvBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = streamingList.get(position);

            Glide.with(context).load(media.getPosterPath())
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


            binding.eptitle.setText(media.getTitle());
            binding.epoverview.setText(media.getOverview());

            binding.epLayout.setOnClickListener(v -> {


                if (media.getLink().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                }else {




                    if ( media.getVip() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                        clickDetectListner.onMoviesListClicked(true);

                        String artwork = media.getPosterPath();
                        String type = STREAMING;
                        String name = media.getName();
                        String videourl = media.getLink();

                        mMediaModel = MediaModel.media(media.getId(), null, null, type, name, videourl, artwork, null,
                                null, null, null, null, null,
                                null,null,null,null,null);
                        mainPlayer.playNext(mMediaModel);

                        clickDetectListner.onStreamingclicked(true);


                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && media.getVip() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                        onLoadSubscribeDialog(media);

                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && media.getVip() == 0) {


                        String artwork = media.getPosterPath();
                        String type = STREAMING;
                        String name = media.getName();
                        String videourl = media.getLink();

                        mMediaModel = MediaModel.media(media.getId(), null, null, type, name, videourl, artwork, null,
                                null, null, null, null, null,
                                null,null,null,null,null);
                        mainPlayer.playNext(mMediaModel);

                        clickDetectListner.onStreamingclicked(true);


                    } else if (authManager.getUserInfo().getPremuim() == 1 && media.getVip() == 0) {


                        String artwork = media.getPosterPath();
                        String type = STREAMING;
                        String name = media.getName();
                        String videourl = media.getLink();

                        mMediaModel = MediaModel.media(media.getId(), null, null, type, name, videourl, artwork, null,
                                null, null, null, null, null,
                                null,null,null,null,null);
                        mainPlayer.playNext(mMediaModel);

                        clickDetectListner.onStreamingclicked(true);


                    } else {

                        DialogHelper.showPremuimWarning(context);

                    }

                }

            });

        }





        private void onLoadSubscribeDialog(Media media) {

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


                }else if ("Appodeal".equals(defaultRewardedNetworkAds)) {

                    onLoadAppOdealRewardAds(media);

                } else if ("Facebook".equals(defaultRewardedNetworkAds)) {

                    onLoadFaceBookRewardAds(media);

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




        private void onLoadUnityAds(Media streaming) {


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


                    String artwork = streaming.getPosterPath();
                    String type = STREAMING;
                    String name = streaming.getName();
                    String videourl = streaming.getLink();

                    mMediaModel = MediaModel.media(streaming.getId(), null, null, type, name, videourl, artwork, null,
                            null, null, null, null, null,
                            null,null,null,null,null);
                    mainPlayer.playNext(mMediaModel);



                }

                @Override
                public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

                    DialogHelper.showAdsFailedWarning(context);

                }
            };

            // Add the listener to the SDK:
            UnityAds.addListener(myAdsListener);

        }

        private void onLoadStartAppAds(Media streaming) {

            final StartAppAd rewardedVideo = new StartAppAd(context);

            rewardedVideo.setVideoListener(() -> {

                String artwork = streaming.getPosterPath();
                String type = STREAMING;
                String name = streaming.getName();
                String videourl = streaming.getLink();

                mMediaModel = MediaModel.media(streaming.getId(), null, null, type, name, videourl, artwork, null,
                        null, null, null, null, null,
                        null,null,null,null,null);
                mainPlayer.playNext(mMediaModel);


            });


            rewardedVideo.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                @Override
                public void onReceiveAd(Ad ad) {
                    rewardedVideo.showAd();
                }

                @Override
                public void onFailedToReceiveAd(Ad ad) {

                    DialogHelper.showPremuimWarning(context);

                }
            });


        }



        private void onLoadAppOdealRewardAds(Media media) {

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

                    String artwork = media.getPosterPath();
                    String type = STREAMING;
                    String nameStream = media.getName();
                    String videourl = media.getLink();

                    mMediaModel = MediaModel.media(media.getId(), null, null, type, nameStream, videourl, artwork, null,
                            null, null, null, null, null,
                            null,null,null,null,null);
                    mainPlayer.playNext(mMediaModel);

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


        private void onLoadFaceBookRewardAds(Media streaming) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());
            mFacebookRewardedVideoAd.loadAd(mFacebookRewardedVideoAd.buildLoadAdConfig()
                    .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                        @Override
                        public void onRewardedVideoCompleted() {

                            String artwork = streaming.getPosterPath();
                            String type = STREAMING;
                            String name = streaming.getName();
                            String videourl = streaming.getLink();

                            mMediaModel = MediaModel.media(streaming.getId(), null, null, type, name, videourl, artwork, null,
                                    null, null, null, null, null,
                                    null,null,null,null,null);
                            mainPlayer.playNext(mMediaModel);

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



        private void onLoadAdmobRewardAds(Media streaming) {

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

                        String artwork = streaming.getPosterPath();
                        String type = STREAMING;
                        String name = streaming.getName();
                        String videourl = streaming.getLink();

                        mMediaModel = MediaModel.media(streaming.getId(), null, null, type, name, videourl, artwork, null,
                                null, null, null, null, null,
                                null,null,null,null,null);
                        mainPlayer.playNext(mMediaModel);

                    }
                };


                mRewardedAd.show((EasyPlexMainPlayer) context, adCallback);


            }
        }



        private void onLoadAutoRewardAds(Media streaming) {

            random = new Random();
            int numberOfMethods = 4;

            switch(random.nextInt(numberOfMethods)) {
                case 0:
                    onLoadStartAppAds(streaming);
                    break;
                case 1:
                    onLoadUnityAds(streaming);
                    break;
                case 2:
                    onLoadAdmobRewardAds(streaming);
                    break;
                case 3:
                    onLoadFaceBookRewardAds(streaming);
                    break;
                default:
                    onLoadAdmobRewardAds(streaming);
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
