package com.easyplex.util;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.easyplex.R;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import kotlin.Suppress;
import timber.log.Timber;
import static android.os.Build.*;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

import static com.easyplex.util.Constants.SERVER_BASE_URL;


public class Tools {


    private static long exitTime = 0;
    private static int admobInterstitialShow = 0;
    private static int facebookInterstitialShow = 0;



    private Tools() {

    }



    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    public static String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }

    }


    public static void doExitApp(Context context) {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(context, "Press again to exit app", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            ((BaseActivity) context).finish();
            System.exit(0);
        }
    }



    public static void onLoadAdmobInterstitialAds(Context context, int admobEnableInterstitial, int admobShowInterstitial, String adUnitIdInterstitial) {

        admobInterstitialShow += 1;

        if (admobEnableInterstitial == 1 &&  admobShowInterstitial == admobInterstitialShow) {

            com.google.android.gms.ads.InterstitialAd admobInter = new com.google.android.gms.ads.InterstitialAd (context);
            admobInter.setAdUnitId(adUnitIdInterstitial);
            admobInter.loadAd(new AdRequest.Builder().build());
            admobInter.show();
            if (!admobInter.isLoaded()) {
                AdRequest adRequest1 = new AdRequest.Builder()
                        .build();
                admobInter.loadAd(adRequest1);
            }
            admobInter.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    admobInter.show();

                    Timber.d("Interstitial Loaded");

                    admobInterstitialShow = 0;
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);

                    Timber.d("Interstitial Failed : %s", loadAdError.getMessage());
                }
            });

        }


    }


    public static void onLoadFacebookAudience(Context context, int faceAudienceInterstitial , int facebookShowInterstitial , String adUnitIdFacebookInterstitialAudience) {


        facebookInterstitialShow += 1;


        if (faceAudienceInterstitial == 1 &&  facebookShowInterstitial == facebookInterstitialShow) {

            InterstitialAd facebookInterstitialAd = new InterstitialAd(context,adUnitIdFacebookInterstitialAudience);

            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {

                @Override
                public void onError(Ad ad, AdError adError) {

                    //

                }

                @Override
                public void onAdLoaded(Ad ad) {


                    facebookInterstitialAd.show();

                }

                @Override
                public void onAdClicked(Ad ad) {


                    //
                }

                @Override
                public void onLoggingImpression(Ad ad) {


                    //vvvvvv
                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {

                    //

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

                    //

                }
            };


            facebookInterstitialAd.loadAd(
                    facebookInterstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());

            facebookInterstitialShow = 0;


        }

    }




    // Return True if user has an active Network
    public static boolean checkIfHasNetwork(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();

                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                final Network n = cm.getActiveNetwork();

                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }

        return false;
    }

    private static StringBuilder formatBuilder = new StringBuilder();
    private static Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());



    // Determine the current progress for player
    public static String getProgressTime(long timeMs, boolean remaining) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        formatBuilder.setLength(0);
        String formatHours = "%d:%02d:%02d";
        String formatMinutes = "%02d:%02d";
        String time = hours > 0 ? formatter.format(formatHours, hours, minutes, seconds).toString()
                : formatter.format(formatMinutes, minutes, seconds).toString();
        return remaining && timeMs != 0 ? "-" + time : time;
    }



    // Load Admob Banner
    public static void onLoadAdmobBanner(@NonNull final Activity activity, FrameLayout
            frameLayout,String unitId){

        AdSize adSize = getAdSize(activity,frameLayout);
        // Create an ad request.
        AdView adView = new AdView(activity);
        adView.setAdUnitId(unitId);
        frameLayout.removeAllViews();
        frameLayout.addView(adView);
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);


        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("B3EEABB8EE11C2BE770B684D95219ECB")).build();
        MobileAds.setRequestConfiguration(configuration);

    }



    // Determine the screen width (less decorations) to use for the ad width.
    private static AdSize getAdSize(@NonNull final Activity activity,FrameLayout
            frameLayout) {

            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getRealMetrics(outMetrics);

            float density = outMetrics.density;

            float adWidthPixels = frameLayout.getWidth();

            // If the ad hasn't been laid out, default to the full screen width.
            if (adWidthPixels == 0) {
                adWidthPixels = outMetrics.widthPixels;
            }

            int adWidth = (int) (adWidthPixels / density);

            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);

    }

    // Return Player Duration in Milliseconds
    public static long progressToMilli(long playerDurationMs, SeekBar seekBar) {
        long duration = playerDurationMs < 1 ? C.TIME_UNSET : playerDurationMs;
        return duration == C.TIME_UNSET ? 0 : ((duration * seekBar.getProgress()) / seekBar.getMax());
    }




    public static String byteToMb(long bytes) {
        long kilobyte = 1024;
        long megabyte = kilobyte * 1024;
        long gigabyte = megabyte * 1024;
        long terabyte = gigabyte * 1024;

        if ((bytes >= 0) && (bytes < kilobyte)) {
            return bytes + " B";

        } else if ((bytes >= kilobyte) && (bytes < megabyte)) {
            return (bytes / kilobyte) + " KB";

        } else if ((bytes >= megabyte) && (bytes < gigabyte)) {
            return (bytes / megabyte) + " MB";

        } else if ((bytes >= gigabyte) && (bytes < terabyte)) {
            return (bytes / gigabyte) + " GB";

        } else if (bytes >= terabyte) {
            return (bytes / terabyte) + " TB";

        } else {
            return bytes + " Bytes";
        }
    }


    public static String milliToDate(long millisecond) {
        Date date = new Date(millisecond);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:ss a");
        return dateFormat.format(date);
    }


    // Load Toolbar
    public static void loadToolbar(AppCompatActivity appCompatActivity, Toolbar toolbar, AppBarLayout appBarLayout){

        appCompatActivity.setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        toolbar.setTitle(null);

        if (appBarLayout !=null) {

            appBarLayout.bringToFront();
        }

        appCompatActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);

    }


    // Animate The AppBar on Scroll
    public static void loadAppBar(NestedScrollView nestedScrollView, Toolbar toolbar){



        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = nestedScrollView.getScrollY();
            int color = Color.parseColor("#E6070707"); // ideally a global variable
            if (scrollY < 256) {
                int alpha = (scrollY << 24) | (-1 >>> 8) ;
                color &= (alpha);
            }
            toolbar.setBackgroundColor(color);

        });


    }

    // Load Main Logo
    public static void loadMainLogo(ImageView imageView){

        Picasso.get().load(SERVER_BASE_URL +"image/logo").memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageView);

    }


    // Load Media Cover Path for Media Details (Movie - Serie - Stream - Anime)
    public static void onLoadMediaCover(ImageView
            imageView,String mediaCoverPath){

        Picasso.get().load(mediaCoverPath).fit().centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).placeholder(R.color.app_background).into(imageView);

    }



    // Load Media Cover Path for Media Details (Movie - Serie - Stream - Anime)
    public static void onLoadMediaCoverAdapters(ImageView
            imageView,String mediaCoverPath){


        Picasso.get().load(mediaCoverPath).fit().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).centerInside().placeholder(R.color.app_background).into(imageView);
    }


    //
    public static void onLoadMediaCoverEpisode(ImageView
            imageView,String mediaCoverPath){

        Picasso.get().load(mediaCoverPath).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).fit().centerInside().placeholder(R.color.app_background).into(imageView);

    }


    // Load Mini Logo
    public static void loadMiniLogo(ImageView
            imageView){

        Picasso.get().load(SERVER_BASE_URL +"image/minilogo").memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageView);



    }



    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }




    // the system bars on Player
    public static void hideSystemPlayerUi(@NonNull final Activity activity, final boolean immediate) {
        hideSystemPlayerUi(activity, immediate, 5000);
    }

    // This snippet hides the system bars for api 30 or less
    @SuppressLint("ObsoleteSdkInt")
    @Suppress(names = "DEPRECATION")
    public static void hideSystemPlayerUi(@NonNull final Activity activity, final boolean immediate, final int delayMs) {

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = activity.getWindow().getDecorView();
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        int uiState = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar
        if (Util.SDK_INT > 18) {
            uiState |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;
        } else {
            final Handler handler = new Handler(Looper.getMainLooper());
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if (visibility == View.VISIBLE) {
                    Runnable runnable = () -> hideSystemPlayerUi(activity, false);
                    if (immediate) {
                        handler.post(runnable);
                    } else {
                        handler.postDelayed(runnable, delayMs);
                    }
                }
            });
        }
        decorView.setSystemUiVisibility(uiState);

    }


    // Making notification bar transparent
    @SuppressLint("ObsoleteSdkInt")
    public static void setSystemBarTransparent(Activity act) {

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }




    // Converting Pixels to DPI
    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



    // Converting Pixels to DPI
    @SuppressLint("SetTextI18n")
    public static void dateFormat(String date , TextView textView) {


        if (date != null && !date.trim().isEmpty()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
            try {
                Date releaseDate = sdf1.parse(date);
                textView.setText(" - " + sdf2.format(releaseDate));
            } catch (ParseException e) {

                Timber.d("%s", Arrays.toString(e.getStackTrace()));

            }
        } else {
            textView.setText("");
        }

    }


    // Start Media Trailer
    public static void startTrailer(@NonNull Context context, String trailerId, String title, String backdropPath) {

        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> files, VideoMeta vMeta) {

                if (files != null) {

                    // 720p  == 22
                    // 360p  == 18

                   int iTags = 22;

                    YtFile ytFile = files.get(iTags);

                    if (ytFile != null && ytFile.getUrl() !=null) {

                        String downloadUrl = ytFile.getUrl();

                        Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(null,null,null,null, title, downloadUrl, backdropPath, null
                                , null, null,null,null,
                                null,null,null,null,null,null));
                        context.startActivity(intent);

                    } else {

                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_WATCH_BASE_URL + trailerId));
                        context.startActivity(youtubeIntent);

                    }



                }

            }
        }.extract(trailerId, true, true);


    }


}
