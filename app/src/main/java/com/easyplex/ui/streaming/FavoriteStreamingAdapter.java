package com.easyplex.ui.streaming;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
import com.easyplex.data.local.entity.Stream;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.ItemShowStreamFavoriteBinding;
import com.easyplex.databinding.ItemShowStreamingBinding;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.ui.settings.SettingsActivity;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.NetworkUtils;
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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.easyplex.util.Constants.ARG_MOVIE;
import static com.easyplex.util.Constants.WIFI_CHECK;

/**
 * Adapter for  Streaming Channels
 *
 * @author Yobex.
 */
public class FavoriteStreamingAdapter extends RecyclerView.Adapter<FavoriteStreamingAdapter.StreamingViewHolder> {

    private List<Stream> streamingList;
    private SharedPreferences preferences;
    private AuthManager authManager;
    private TokenManager tokenManager;
    private SettingsManager settingsManager;
    private Context context;
    private RewardedAd mRewardedAd;
    private StartAppAd startAppAd;
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private Random random;
    private IUnityAdsListener iUnityAdsListener;

    public void addStreaming(Context context,List<Stream> castList , AuthManager authManager,TokenManager tokenManager,SettingsManager settingsManager,SharedPreferences preferences) {
        this.streamingList = castList;
        this.context = context;
        this.authManager = authManager;
        this.tokenManager = tokenManager;
        this.settingsManager = settingsManager;
        this.preferences = preferences;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StreamingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemShowStreamFavoriteBinding binding = ItemShowStreamFavoriteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new FavoriteStreamingAdapter.StreamingViewHolder(binding);
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

        private final ItemShowStreamFavoriteBinding binding;


        StreamingViewHolder (@NonNull ItemShowStreamFavoriteBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = streamingList.get(position);


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



            Glide.with(context).asBitmap().load(media.getPosterPath())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .placeholder(R.color.app_background)
                    .into(binding.itemMovieImage);


            binding.movietitle.setText(media.getName());



            binding.rootLayout.setOnClickListener(v -> {

                if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                    DialogHelper.showWifiWarning(context);

                }else {


                    if (media.getLink() !=null && !media.getLink().isEmpty()) {


                        if (media.getVip() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() !=null) {

                            String artwork = media.getPosterPath();
                            String name = media.getName();
                            String videourl = media.getLink();
                            String type = "streaming";
                            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(),null,null,type, name, videourl, artwork, null
                                    , null, null,null,
                                    null,null,
                                    null,
                                    null,null,null,null));
                            intent.putExtra(ARG_MOVIE, media);
                            context.startActivity(intent);


                        }else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && media.getVip() != 1 && authManager.getUserInfo().getPremuim() == 0) {

                            onLoadSubscribeDialog(media);

                        }else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && media.getVip() == 0 ){


                            String artwork = media.getPosterPath();
                            String name = media.getName();
                            String videourl = media.getLink();
                            String type = "streaming";
                            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(),null,null,type, name, videourl, artwork, null
                                    , null, null,null,
                                    null,null,
                                    null,
                                    null,null,null,null));
                            intent.putExtra(ARG_MOVIE, media);
                            context.startActivity(intent);


                        } else if (authManager.getUserInfo().getPremuim() == 1 && media.getVip() == 0){


                            String artwork = media.getPosterPath();
                            String name = media.getName();
                            String videourl = media.getLink();
                            String type = "streaming";
                            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(),null,null,type, name, videourl, artwork, null
                                    , null, null,null,
                                    null,null,
                                    null,
                                    null,null,null,null));
                            intent.putExtra(ARG_MOVIE, media);
                            context.startActivity(intent);

                        }else {

                            DialogHelper.showPremuimWarning(context);

                        }



                    }else {


                        DialogHelper.showNoStreamAvailable(context);

                    }


                }



            });


        }




        private void onLoadSubscribeDialog(Media media) {

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




        private void onLoadAutoRewardAds(Media media) {

            random = new Random();
            int numberOfMethods = 5;

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
                case 4:
                    onLoadAppOdealRewardAds(media);
                    break;
                default:
                    onLoadAdmobRewardAds(media);
            }

        }


        private void onLoadAppOdealRewardAds(Media media) {

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

                    String artwork = media.getPosterPath();
                    String streamName = media.getName();
                    String videourl = media.getLink();
                    String type = "streaming";
                    Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                    intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(),null,null,type, streamName, videourl, artwork, null
                            , null, null,null,
                            null,null,
                            null,
                            null,null,null,null));
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);

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

            mFacebookRewardedVideoAd.loadAd(mFacebookRewardedVideoAd.buildLoadAdConfig()
                    .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                        @Override
                        public void onRewardedVideoCompleted() {

                            String artwork = media.getPosterPath();
                            String name = media.getName();
                            String videourl = media.getLink();
                            String type = "streaming";
                            Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                            intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(),null,null,type, name, videourl, artwork, null
                                    , null, null,null,
                                    null,null,
                                    null,
                                    null,null,null,null));
                            intent.putExtra(ARG_MOVIE, media);
                            context.startActivity(intent);

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


        private void onLoadStartAppAds(Media media) {

            startAppAd = new StartAppAd(context);

            startAppAd.setVideoListener(() -> {

                String artwork = media.getPosterPath();
                String name = media.getName();
                String videourl = media.getLink();
                String type = "streaming";
                Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(),null,null,type, name, videourl, artwork, null
                        , null, null,null,
                        null,null,
                        null,
                        null,null,null,null));
                intent.putExtra(ARG_MOVIE, media);
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




        private void onLoadAdmobRewardAds(Media media) {


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

                        String artwork = media.getPosterPath();
                        String name = media.getName();
                        String videourl = media.getLink();
                        String type = "streaming";
                        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(),null,null,type, name, videourl, artwork, null
                                , null, null,null,
                                null,null,
                                null,
                                null,null,null,null));
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);

                    }
                };


                mRewardedAd.show((BaseActivity) context, adCallback);


            }

        }



        private void onLoadUnityAds(Media media) {


            if (UnityAds.isReady()) {
                UnityAds.show ((BaseActivity) context, "rewardedVideo");
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


                    String artwork = media.getPosterPath();
                    String name = media.getName();
                    String videourl = media.getLink();
                    String type = "streaming";
                    Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                    intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getId(),null,null,type, name, videourl, artwork, null
                            , null, null,null,
                            null,null,
                            null,
                            null,null,null,null));
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);


                    createAndLoadRewardedAd();

                }

                @Override
                public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

                    DialogHelper.showAdsFailedWarning(context);

                }
            };

            // Add the listener to the SDK:
            UnityAds.addListener(iUnityAdsListener);
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


    private void createAndLoadRewardedAd() {

        if ("StartApp".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            startAppAd = new StartAppAd(context);


        } else if ("UnityAds".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            UnityAds.initialize ((BaseActivity) context, settingsManager.getSettings().getUnityGameId(), false);

        } else if ("Facebook".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());

        }else if ("Appodeal".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

        }
        else if ("Auto".equals(settingsManager.getSettings().getDefaultRewardedNetworkAds())) {

            mFacebookRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(context, settingsManager.getSettings().getAdUnitIdFacebookRewarded());

            UnityAds.initialize ((BaseActivity) context, settingsManager.getSettings().getUnityGameId(), false);
            startAppAd = new StartAppAd(context);

            Appodeal.initialize((BaseActivity) context, settingsManager.getSettings().getAdUnitIdAppodealRewarded(),Appodeal.REWARDED_VIDEO);

        }
    }


}
