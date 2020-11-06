package com.easyplex.ui.watchhistory;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.easyplex.R;
import com.easyplex.data.local.entity.History;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.media.Resume;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.databinding.ItemHistoryBinding;
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

import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.easyplex.util.Constants.WIFI_CHECK;
import static com.unity3d.services.core.properties.ClientProperties.getApplicationContext;

/**
 * Adapter for Movie Casts.
 *
 * @author Yobex.
 */
public class WatchHistorydapter extends RecyclerView.Adapter<WatchHistorydapter.MainViewHolder> {

    private List<History> castList;
    private MediaRepository mediaRepository;
    private AuthManager authManager;
    private Context context;
    private SharedPreferences preferences;
    private SettingsManager settingsManager;
    private TokenManager tokenManager;
    private com.facebook.ads.RewardedVideoAd mFacebookRewardedVideoAd;
    private Random random;
    private StartAppAd startAppAd;
    private RewardedAd mRewardedAd;
    private IUnityAdsListener iUnityAdsListener;

    public void addToContent(List<History> castList,MediaRepository mediaRepository,AuthManager authManager
            ,Context context,SharedPreferences preferences,SettingsManager settingsManager,TokenManager tokenManager) {
        this.castList = castList;
        this.mediaRepository = mediaRepository;
        this.authManager = authManager;
        this.context = context;
        this.preferences = preferences;
        this.settingsManager = settingsManager;
        this.tokenManager = tokenManager;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WatchHistorydapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemHistoryBinding binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new WatchHistorydapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchHistorydapter.MainViewHolder holder, int position) {
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

    class MainViewHolder extends RecyclerView.ViewHolder {

        private final ItemHistoryBinding binding;


        MainViewHolder(@NonNull ItemHistoryBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final History history = castList.get(position);


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




                if (preferences.getBoolean(WIFI_CHECK, false) && NetworkUtils.isWifiConnected(context)) {

                    DialogHelper.showWifiWarning(context);

                }else {


                    if (history.getPremuim() == 1 && authManager.getUserInfo().getPremuim() == 1 && tokenManager.getToken() != null) {


                        onLoadStream(history);



                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 1 && history.getPremuim() != 1 && authManager.getUserInfo().getPremuim() == 0) {


                        onLoadSubscribeDialog(history);

                    } else if (settingsManager.getSettings().getWachAdsToUnlock() == 0 && history.getPremuim() == 0) {


                        onLoadStream(history);


                    } else if (authManager.getUserInfo().getPremuim() == 1 && history.getPremuim() == 0) {


                        onLoadStream(history);


                    } else {

                        DialogHelper.showPremuimWarning(context);

                    }


                }




            });


            binding.movietitle.setText(history.getTitle());

            mediaRepository.getResumeById(history.getTmdbId())
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
                                    Timber.i("Can't be divided by Zero%s", e);
                                }

                                binding.resumeProgressBar.setProgress((int) moveProgress);
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

            Tools.onLoadMediaCover(binding.itemMovieImage, history.getPosterPath());

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



        private void onLoadSubscribeDialog(History media) {


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

        private void onLoadAutoRewardAds(History media) {

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

        private void onLoadFaceBookRewardAds(History media) {


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


        private void onLoadAppOdealRewardAds(History media) {

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


        private void onLoadAdmobRewardAds(History media) {


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

        private void onLoadUnityAds(History media) {

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

        private void onLoadStartAppAds(History media) {

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

        private void onLoadStream(History history) {

            if (history.getBackdropPath() !=null) {

                Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(history.getTmdbId(),null,null,"0", history.getTitle(),
                        history.getLink(), history.getBackdropPath(), null, null
                        , null,null,null,null,null,null,null,null,null));
                context.startActivity(intent);

            }else {

                Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(history.getTmdbId(),null,null,"0", history.getTitle(),
                        history.getLink(), history.getPosterPath(), null, null
                        , null,null,null,null,null,null,null,null,null));
                context.startActivity(intent);

            }

        }
    }
}
