package com.easyplex;

import android.content.Context;
import androidx.multidex.MultiDexApplication;

import com.appodeal.ads.Appodeal;
import com.easyplex.ui.manager.SettingsManager;
import com.explorestack.consent.Consent;
import com.ogury.consent.manager.ConsentManager;
import com.squareup.okhttp.OkHttpClient;
import com.startapp.sdk.adsbase.StartAppAd;
import com.easyplex.di.AppInjector;
import com.easyplex.util.Tools;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.startapp.sdk.adsbase.StartAppSDK;
import javax.inject.Inject;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import info.guardianproject.netcipher.client.StrongBuilder;
import info.guardianproject.netcipher.client.StrongOkHttpClientBuilder;
import info.guardianproject.netcipher.proxy.OrbotHelper;
import timber.log.Timber;

/**
 * Application level class.
 *
 * @author Yobex.
 */
public class EasyPlexApp extends MultiDexApplication implements HasAndroidInjector{


    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Inject
    SettingsManager settingsManager;

    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();

        AppInjector.init(this);


        // Initialize the StartAppSDK
        StartAppSDK.init(this,settingsManager.getSettings().getStartappId(),false);



        // Disable StartAppSDK Splash
        StartAppAd.disableSplash();
        StartAppAd.enableConsent(this,false);


        // Disable  StartAppSDK Return Ads
        StartAppSDK.enableReturnAds(false);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }


        Timber.i("Creating EasyPlex Application");


        EasyPlexApp.context = getApplicationContext();

    }


    public static Context  getInstance() {

        return EasyPlexApp.context;
    }

    public static boolean hasNetwork() {
        return Tools.checkIfHasNetwork(context);
    }


    public static Context getContext() {
        return context;
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        AppInjector.init(this);
        return androidInjector;
    }
}

/*
 * Application has activities that is why we implement HasActivityInjector interface.
 * Activities have fragments so we have to implement HasFragmentInjector/HasSupportFragmentInjector
 * in our activities.
 * No child fragment and don’t inject anything in your fragments, no need to implement
 * HasSupportFragmentInjector.
 */
