package com.easyplex.ui.player.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.R;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.RowPlayerMoviesEndedBinding;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
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
import java.io.Serializable;
import java.util.List;
import java.util.Random;


/**
 * Adapter for Next Movie.
 *
 * @author Yobex.
 */
public class NextPlayMoviesAdapter extends RecyclerView.Adapter<NextPlayMoviesAdapter.NextPlayMoviesViewHolder> {

    private List<Media> castList;
    MediaModel mMediaModel;
    private AuthManager authManager;
    private SettingsManager settingsManager;
    ClickDetectListner clickDetectListner;
    private EasyPlexMainPlayer player;
    private RewardedAd mRewardedAd;
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private StartAppAd startAppAd;
    private Context context;
    private CountDownTimer mCountDownTimer;
    private Random random;
    private TokenManager tokenManager;


    public NextPlayMoviesAdapter(Context context, ClickDetectListner clickDetectListner,
                                 EasyPlexMainPlayer player, AuthManager
                                         authManager, SettingsManager settingsManager, CountDownTimer mCountDownTimer,TokenManager tokenManager) {

        this.context = context;
        this.clickDetectListner = clickDetectListner;
        this.player = player;
        this.authManager = authManager;
        this.settingsManager = settingsManager;
        this.mCountDownTimer = mCountDownTimer;
        this.tokenManager = tokenManager;
    }

    public void addSeasons(List<Media> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NextPlayMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowPlayerMoviesEndedBinding binding = RowPlayerMoviesEndedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new NextPlayMoviesAdapter.NextPlayMoviesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NextPlayMoviesViewHolder holder, int position) {
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



    public Serializable getFirstItem() {
        if (castList != null) {
            return castList.get(0).getTmdbId();
        } else {
            return 0;
        }
    }


    class NextPlayMoviesViewHolder extends RecyclerView.ViewHolder {


        private final RowPlayerMoviesEndedBinding binding;

        NextPlayMoviesViewHolder (@NonNull RowPlayerMoviesEndedBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media media = castList.get(position);



            Glide.with(context).load(media.getBackdropPath())
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

            binding.movieName.setText(media.getTitle());

            binding.epoverview.setText(media.getOverview());



            for (Genre genre : media.getGenres()) {
                binding.movieGenre.setText(" - "+genre.getName());
            }


            binding.epLayout.setOnClickListener(v -> {



                if (media.getVideos().isEmpty()) {

                    DialogHelper.showNoStreamAvailable(context);

                }else {




                    if ( media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {

                        String artwork = media.getBackdropPath();
                        String movieId = media.getTmdbId();
                        String type = "0";
                        String currentQuality = media.getVideos().get(0).getServer();
                        String name = media.getTitle();
                        String streamLink = media.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(movieId, null, currentQuality, type, name, streamLink, artwork, null,
                                null, null, null, null,
                                null,null,null,
                                null,null, player.getPlayerController().getCurrentTvShowName());
                        player.playNext(mMediaModel);


                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && media.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                        onLoadSubscribeDialog(media);

                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && media.getPremuim() == 0) {


                        String artwork = media.getBackdropPath();
                        String movieId = media.getTmdbId();
                        String type = "0";
                        String currentQuality = media.getVideos().get(0).getServer();
                        String name = media.getTitle();
                        String streamLink = media.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(movieId, null, currentQuality, type, name, streamLink, artwork, null,
                                null, null, null, null,
                                null,null,null,
                                null,null, player.getPlayerController().getCurrentTvShowName());
                        player.playNext(mMediaModel);


                    } else if (authManager.getUserInfo().getPremuim() == 1 && media.getPremuim() == 0) {


                        String artwork = media.getBackdropPath();
                        String movieId = media.getTmdbId();
                        String type = "0";
                        String currentQuality = media.getVideos().get(0).getServer();
                        String name = media.getTitle();
                        String streamLink = media.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(movieId, null, currentQuality, type, name, streamLink, artwork, null,
                                null, null, null, null,
                                null,null,null,
                                null,null, player.getPlayerController().getCurrentTvShowName());
                        player.playNext(mMediaModel);


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


                if(mCountDownTimer!=null){

                    mCountDownTimer.cancel();

                }



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



    private void onLoadFaceBookRewardAds(Media media) {

        mFacebookRewardedVideoAd.loadAd(mFacebookRewardedVideoAd.buildLoadAdConfig()
                .withAdListener(new com.facebook.ads.RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoCompleted() {

                        String artwork = media.getBackdropPath();
                        String movieId = media.getTmdbId();
                        String type = "0";
                        String currentQuality = media.getVideos().get(0).getServer();
                        String name = media.getTitle();
                        String streamLink = media.getVideos().get(0).getLink();

                        mMediaModel = MediaModel.media(movieId, null, currentQuality, type, name, streamLink, artwork, null,
                                null, null, null, null,
                                null,null,null,
                                null,null, player.getPlayerController().getCurrentTvShowName());
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

                String artwork = media.getBackdropPath();
                String movieId = media.getTmdbId();
                String type = "0";
                String currentQuality = media.getVideos().get(0).getServer();
                String epName = media.getTitle();
                String streamLink = media.getVideos().get(0).getLink();

                mMediaModel = MediaModel.media(movieId, null, currentQuality, type, epName, streamLink, artwork, null,
                        null, null, null, null,
                        null,null,null,
                        null,null, player.getPlayerController().getCurrentTvShowName());
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


                    Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                    intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getTmdbId(), media.getGenres().get(0).getName(), media.getVideos().get(0).getServer(), "0", media.getTitle(),
                            media.getVideos().get(0).getLink(), media.getBackdropPath(), null, null
                            , null, null,
                            null, null,
                            null,
                            null,
                            null, media.getPremuim(), null));
                    context.startActivity(intent);
                    Animatoo.animateFade(context);

                }
            };


            mRewardedAd.show((EasyPlexMainPlayer) context, adCallback);


        }

    }

    private void onLoadUnityAds(Media media) {

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


                String artwork = media.getBackdropPath();
                String movieId = media.getTmdbId();
                String type = "0";
                String currentQuality = media.getVideos().get(0).getServer();
                String name = media.getTitle();
                String streamLink = media.getVideos().get(0).getLink();

                mMediaModel = MediaModel.media(movieId, null, currentQuality, type, name, streamLink, artwork, null,
                        null, null, null, null,
                        null,null,null,
                        null,null, null);
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

    private void onLoadStartAppAds(Media media) {


        startAppAd.setVideoListener(() -> {


            String artwork = media.getBackdropPath();
            String movieId = media.getTmdbId();
            String type = "0";
            String currentQuality = media.getVideos().get(0).getServer();
            String name = media.getTitle();
            String streamLink = media.getVideos().get(0).getLink();

            mMediaModel = MediaModel.media(movieId, null, currentQuality, type, name, streamLink, artwork, null,
                    null, null, null, null,
                    null,null,null,
                    null,null, player.getPlayerController().getCurrentTvShowName());
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
