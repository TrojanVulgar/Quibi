package com.easyplex.ui.home.adapters;


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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.easyplex.R;
import com.easyplex.data.local.entity.History;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.databinding.RowItemFeaturedBinding;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.ui.seriedetails.SerieDetailsActivity;
import com.easyplex.ui.settings.SettingsActivity;
import com.easyplex.ui.trailer.TrailerPreviewActivity;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.NetworkUtils;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.easyplex.util.Constants.ARG_MOVIE;
import static com.easyplex.util.Constants.WIFI_CHECK;
import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;


/**
 * Adapter for Featured Movies.
 *
 * @author Yobex.
 */
public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder>{

    private List<Media> castList;
    private SharedPreferences preferences;
    private AuthManager authManager;
    private SettingsManager settingsManager;
    private Context context;
    private TokenManager tokenManager;
    private MediaRepository mediaRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private Random random;
    private StartAppAd startAppAd;
    private RewardedAd mRewardedAd;
    private IUnityAdsListener iUnityAdsListener;
    private History history;



    public void addFeatured(List<Media> castList, Context context, SharedPreferences preferences,
                            MediaRepository mediaRepository, AuthManager authManager, SettingsManager settingsManager, TokenManager tokenManager){
        this.castList = castList;
        this.context = context;
        this.preferences = preferences;
        this.mediaRepository = mediaRepository;
        this.authManager = authManager;
        this.settingsManager = settingsManager;
        this.tokenManager = tokenManager;
        notifyDataSetChanged();



    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowItemFeaturedBinding binding = RowItemFeaturedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);


        return new FeaturedViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {

        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {

            if(castList.size() < 10){

                return castList.size();
            }else {

                return 10;
            }

        } else {
            return 0;
        }
    }


    class FeaturedViewHolder extends RecyclerView.ViewHolder {


        private final RowItemFeaturedBinding binding;

        FeaturedViewHolder (@NonNull RowItemFeaturedBinding binding)
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


            if (media.getSeasons() !=null) {

                binding.movietitle.setText(media.getName());

                for (Genre genre : media.getGenres()) {
                    binding.mgenres.setText(genre.getName());
                }


                binding.infoTrailer.setOnClickListener(v -> {


                    if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                        DialogHelper.showWifiWarning(context);

                    } else {


                        Intent intent = new Intent(context, TrailerPreviewActivity.class);
                        intent.putExtra(ARG_MOVIE, media);
                        context.startActivity(intent);
                        Animatoo.animateFade(context);
                    }


                });


                binding.rootLayout.setOnClickListener(view -> {

                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);

                });

                binding.PlayButtonIcon.setOnClickListener(view -> {

                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);

                });


                if (media.getPremuim() == 1) {

                    binding.moviePremuim.setVisibility(View.VISIBLE);


                }else {

                    binding.moviePremuim.setVisibility(View.GONE);
                }


            }else {


                onLoadMovies(media);

            }



            binding.addToFavorite.setOnClickListener(view -> addFavorite(media));
            checkMovieFavorite(media);
            Picasso.get().load(media.getPosterPath()).placeholder(R.color.app_background).into(binding.itemMovieImage);



        }

        public void addFavorite(Media mediaDetail) {

            if (mediaRepository.isFeaturedFavorite(Integer.parseInt(mediaDetail.getTmdbId()))) {

                Timber.i("Removed From Watchlist");
                compositeDisposable.add(Completable.fromAction(() -> mediaRepository.removeFavorite(mediaDetail))
                        .subscribeOn(Schedulers.io())
                        .subscribe());

                binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

                Toast.makeText(context, "Removed From Watchlist", Toast.LENGTH_SHORT).show();

            }else {

                Timber.i("Added To Watchlist");
                compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addFavorite(mediaDetail))
                        .subscribeOn(Schedulers.io())
                        .subscribe());

                binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

                Toast.makeText(context, "Added To Watchlist", Toast.LENGTH_SHORT).show();
            }


        }



        private void onLoadMovies(Media media) {


            if (media.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }

            binding.infoTrailer.setOnClickListener(v -> {


                if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                    DialogHelper.showWifiWarning(context);

                }else {

                    Intent intent = new Intent(context, TrailerPreviewActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);
                    Animatoo.animateFade(context);
                }

            });


            binding.movietitle.setText(media.getTitle());

            for (Genre genre : media.getGenres()) {
                binding.mgenres.setText(genre.getName());
            }

            binding.rootLayout.setOnClickListener(view -> {

                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, media);
                context.startActivity(intent);

            });

            binding.PlayButtonIcon.setOnClickListener(view -> onLoadFeaturedStream(media));
        }


        private void onLoadFeaturedStream(Media media) {

            if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                DialogHelper.showWifiWarning(context);

            }else {


                if (media.getVideos() !=null && !media.getVideos().isEmpty()) {


                    if (media.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {


                        onLoadStream(media);



                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && media.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                        onLoadSubscribeDialog(media);

                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && media.getPremuim() == 0) {


                        onLoadStream(media);


                    } else if (authManager.getUserInfo().getPremuim() == 1 && media.getPremuim() == 0) {


                        onLoadStream(media);


                    } else {

                        DialogHelper.showPremuimWarning(context);

                    }

                }else {



                    DialogHelper.showNoDownloadAvailable(context);
                }


            }

        }


        private void checkMovieFavorite(Media featured) {

            if (mediaRepository.isFeaturedFavorite(Integer.parseInt(featured.getTmdbId()))) {

                binding.addToFavorite.setImageResource(R.drawable.ic_in_favorite);

            } else {

                binding.addToFavorite.setImageResource(R.drawable.add_from_queue);

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


                Toast.makeText(context, "Loading Reward", Toast.LENGTH_SHORT).show();


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

                }
                 else if ("Auto".equals(defaultRewardedNetworkAds)) {

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

                case 4:
                    onLoadAppOdealRewardAds(media);
                    break;
                default:
                    onLoadAdmobRewardAds(media);
            }

        }


    }

    private void onLoadStream(Media media) {

        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(media.getTmdbId(), media.getGenres().get(0).getName()
                , media.getVideos().get(0).getServer(), "0", media.getTitle(),
                media.getVideos().get(0).getLink(), media.getBackdropPath(),
                null, null
                , null, null,
                null, null,
                null,
                null,
                null,media.getPremuim(),null));
        context.startActivity(intent);
        Animatoo.animateFade(context);


        history  = new History(media.getId(),media.getTmdbId(),media.getPosterPath(),media.getTitle(),media.getBackdropPath(),media.getLink());

        history.setLink(media.getVideos().get(0).getLink());
        history.setPremuim(media.getPremuim());
        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addhistory(history))
                .subscribeOn(Schedulers.io())
                .subscribe());

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

                onLoadStream(media);

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

                        onLoadStream(media);

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


                    onLoadStream(media);

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

                onLoadStream(media);

                UnityAds.removeListener(iUnityAdsListener);

            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

               DialogHelper.showAdsFailedWarning(context);

            }
        };

        UnityAds.addListener(iUnityAdsListener);

    }

    private void onLoadStartAppAds(Media media) {

        startAppAd.setVideoListener(() -> onLoadStream(media));

        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                startAppAd.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                Toast.makeText(getApplicationContext(), R.string.cant_show, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        compositeDisposable.clear();
    }


}
