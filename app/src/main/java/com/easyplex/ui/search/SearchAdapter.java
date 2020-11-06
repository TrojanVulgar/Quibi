package com.easyplex.ui.search;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.easyplex.R;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.ItemSearchBinding;
import com.easyplex.ui.animes.AnimeDetailsActivity;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
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

import java.util.List;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.easyplex.util.Constants.ARG_MOVIE;
import static com.easyplex.util.Constants.STREAMING;
import static com.easyplex.util.Constants.WIFI_CHECK;


/**
 * Adapter for Search Results (Movies,Series,Animes).
 *
 * @author Yobex.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Media> castList;
    private SettingsManager settingsManager;
    private AuthManager authManager;
    private TokenManager tokenManager;
    private SharedPreferences preferences;
    private Context context;
    private RewardedAd mRewardedAd;
    private StartAppAd startAppAd;
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private Random random;
    private IUnityAdsListener iUnityAdsListener;

    public void setSearch(List<Media> castList,SettingsManager settingsManager,AuthManager authManager,TokenManager tokenManager,SharedPreferences preferences,Context context) {
        this.castList = castList;
        this.settingsManager = settingsManager;
        this.authManager = authManager;
        this.tokenManager = tokenManager;
        this.preferences = preferences;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemSearchBinding binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new SearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
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

    class SearchViewHolder extends RecyclerView.ViewHolder {

        private final ItemSearchBinding binding;


        SearchViewHolder (@NonNull ItemSearchBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = castList.get(position);

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

                binding.rootLayout.setOnClickListener(view -> {

                    if (media.getIsAnime() == 1) {

                        Intent intent = new Intent(context, AnimeDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);


                    }else if (media.getLive() == 1) {

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


                    }

                    else if (media.getName() !=null) {


                        Intent intent = new Intent(context, SerieDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);

                    }else {


                        Intent intent = new Intent(context, MovieDetailsActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);

                    }


                });

            Tools.onLoadMediaCover(binding.itemMovieImage,media.getPosterPath());

        }
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
                String type = STREAMING;
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
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

                DialogHelper.showAdsFailedWarning(context);

            }
        };

        // Add the listener to the SDK:
        UnityAds.addListener(iUnityAdsListener);
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


    public RewardedAd initLoadRewardedAd() {
        mRewardedAd = new RewardedAd(context,
                settingsManager.getSettings().getAdUnitIdRewarded());
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {

                // Ad successfully loaded.
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
