package com.easyplex.di.module;

import com.easyplex.ui.animes.AnimeDetailsActivity;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.download.DownloadList;
import com.easyplex.ui.login.LoginActivity;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.ui.notifications.NotificationManager;
import com.easyplex.ui.payment.Payment;
import com.easyplex.ui.payment.PaymentDetails;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.profile.EditProfileActivity;
import com.easyplex.ui.register.RegisterActivity;
import com.easyplex.ui.seriedetails.SerieDetailsActivity;
import com.easyplex.ui.splash.SplashActivity;
import com.easyplex.ui.trailer.TrailerPreviewActivity;
import com.easyplex.ui.upcoming.UpcomingTitlesActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all sub-components within the app. Add bindings for other sub-components here.
 * @ContributesAndroidInjector was introduced removing the need to:
 * a) Create separate components annotated with @Subcomponent (the need to define @Subcomponent classes.)
 * b) Write custom annotations like @PerActivity.
 *
 * @author Yobex.
 */
@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract BaseActivity contributeMainActivity();


    @ContributesAndroidInjector()
    abstract Payment contributePayment();

    @ContributesAndroidInjector()
    abstract NotificationManager contributeNotificationManager();


    @ContributesAndroidInjector()
    abstract DownloadList contributeDownloadList();

    @ContributesAndroidInjector()
    abstract PaymentDetails contributePaymentDetails();

    @ContributesAndroidInjector()
    abstract EditProfileActivity contributeEditProfileActivity();

    @ContributesAndroidInjector()
    abstract MovieDetailsActivity contributeMovieDetailActivity();

    @ContributesAndroidInjector()
    abstract SerieDetailsActivity contributeSerieDetailActivity();

    @ContributesAndroidInjector()
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector()
    abstract RegisterActivity contributeRegisterActivity();

    @ContributesAndroidInjector()
    abstract TrailerPreviewActivity contributeTrailerPreviewActivity();

    @ContributesAndroidInjector()
    abstract UpcomingTitlesActivity contributeUpcomingTitlesActivity();

    @ContributesAndroidInjector()
    abstract AnimeDetailsActivity contributeAnimeDetailsActivity();

    @ContributesAndroidInjector()
    abstract EasyPlexMainPlayer contributeDoubleViewTubiPlayerActivity();

    @ContributesAndroidInjector()
    abstract SplashActivity contributeSplashActivity();


}
