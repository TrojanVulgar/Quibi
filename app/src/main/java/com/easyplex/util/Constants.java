package com.easyplex.util;


import android.util.Base64;

import java.io.UnsupportedEncodingException;

import static com.easyplex.BuildConfig.APP_CONFIG;
import static com.easyplex.BuildConfig.APP_STARUP;

/**
 * Application CONSTANTS.
 *
 * @author Yobex.
 */
public abstract class Constants {



    // load c++ library
    static {
        System.loadLibrary("native-lib");
    }

    private Constants(){


    }


    // API Constants
    public static final String IMDB_BASE_URL = "https://api.themoviedb.org/3/";
    public static final String SERVER_BASE_URL = getSecureBaseUrl();


    public static native String baseUrlFromJNI();

    public static native String AppfromJNI();

    // decode base64 to a string and get normal url
    public static String getSecureBaseUrl() {
        final String mUrl = baseUrlFromJNI();
        try {
            return new String(Base64.decode(mUrl, Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return mUrl;
    }




    // decode base64 to a string and get normal url
    public static String getApp() {
        final String mUrl = AppfromJNI();
        try {
            return new String(Base64.decode(mUrl, Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return mUrl;
    }




    public static final String PURCHASE_KEY = "";



    public static final String MOVIE ="MOVIE";
    public static final String SERIE ="SERIE";
    public static final String EMPTY_URL ="about:blank";
    public static final String MOVIE_ID = "id";
    public static final String YOUTUBE_WATCH_BASE_URL = "https://www.youtube.com/watch?v=";
    public static final String YOUTUBE_SEARCH_BASE_URL = " https://www.youtube.com/results?search_query=";
    public static final String PAYPAL_CLIENT_ID = "clientid";
    public static final String PAYMENT = "payment";
    public static final String SUBS_SIZE = "subs_size";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String ARG_MOVIE = "movie";
    public static final String ARG_PAYMENT = "payment";
    public static final String SUBSCRIPTIONS = "You Subscription has ended !";

    public static final String MOVIE_LINK = "link";
    public static final String MOVIE_IMDB_NOTIFICATION = "tmdb_id";


    // Buttons Switch Constants
    public static final String WIFI_CHECK = "wifi_check";
    public static final String SWITCH_PUSH_NOTIFICATION = "switch_push_notification";
    public static final String AUTO_PLAY = "autoplay_check";



    // Auth Constants
    public static final String PREMUIM = "premuim";
    public static final String AUTH_NAME = "name";
    public static final String AUTH_ID = "id";
    public static final String AUTH_EXPIRED_DATE = "expired_in";
    public static final String ERROR = "Error";



    // Ads Constants
    public static final String ADS_LINK = "link";
    public static final String ADS_CLICKTHROUGHURL = "clickThroughUrl";


    // Admob



    // Remote
    public static final String APPLICATION_JSON = "application/json";
    public static final String ACCEPT = "Accept";


    // Substitles

    public static final String STORAGE_LOCATION = "file:///storage/emulated/0/Android/data/";
    public static final String ZIP_FILE_NAME = "1.srt";
    public static final String SUBSTITLE_LOCATION = "file:///storage/emulated/0/Android/data/";
    public static final String SUBSTITLE_SUB_FILENAME_ZIP = "/subs.zip";



    // Settings Constants
    public static final String APP_NAME = "app_name";
    public static final String AD_FACEBOOK_INTERSTITIAL_SHOW = "facebook_show_interstitial";
    public static final String AD_INTERSTITIAL_SHOW = "ad_show_interstitial";
    public static final String AD_INTERSTITIAL = "ad_interstitial";
    public static final String AD_INTERSTITIAL_UNIT = "ad_unit_id_interstitial";
    public static final String AD_BANNER = "ad_banner";
    public static final String AD_BANNER_UNIT = "ad_unit_id_banner";
    public static final String TMDB = "tmdb_api_key";
    public static final String APP_URL_ANDROID = "app_url_android";
    public static final String PRIVACY_POLICY = "privacy_policy";
    public static final String LATEST_VERSION = "latestVersion";
    public static final String UPDATE_TITLE = "update_title";
    public static final String RELEASE_NOTES = "releaseNotes";
    public static final String PAYPAL_AMOUNT = "paypal_amount";
    public static final String FEATURED_HOME_NUMBERS = "featured_home_numbers";
    public static final String IMDB_COVER_PATH = "imdb_cover_path";
    public static final String AUTOSUBSTITLES = "autosubstitles";
    public static final String ANIME = "anime";
    public static final String ADS_SETTINGS = "ads";
    public static final String AD_INTERSTITIAL_FACEEBOK_ENABLE = "ad_face_audience_interstitial";
    public static final String AD_INTERSTITIAL_FACEEBOK_UNIT_ID = "ad_unit_id_facebook_interstitial_audience";
    public static final String PREFS = APP_CONFIG;
    public static final String PREFS2 = APP_STARUP;
    public static final String AD_BANNER_FACEEBOK_ENABLE = "ad_face_audience_banner";
    public static final String AD_BANNER_FACEEBOK_UNIT_ID = "ad_unit_id_facebook_banner_audience";
    public static final String DEFAULT_NETWORK = "default_network";
    public static final String STARTAPP_ID = "startapp_id";
    public static final String ADMOB_REWARD = "ad_unit_id_rewarded";
    public static final String FACEBOOK_REWARD = "ad_unit_id__facebook_rewarded";
    public static final String UNITY_GAME_ID = "unity_game_id";
    public static final String WATCH_ADS_TO_UNLOCK = "wach_ads_to_unlock";
    public static final String CUSTOM_MESSAGE = "custom_message";
    public static final String ENABLE_CUSTOM_MESSAGE = "enable_custom_message";
    public static final String STRIPE_PUBLISHABLE_KEY = "stripe_publishable_key";
    public static final String STRIPE_SECRET_KEY = "stripe_secret_key";
    public static final String APPODEAL_REWARD = "ad_unit_id__appodeal_rewarded";


    // Status
    public static final String CODE = "code";

    public static final String APP = getApp();

    // TV-SERIES
    public static final String SPECIALS = "Specials";
    public static final String SEASONS = "Seasons: ";
    public static final String RESUME = "RESUME";


    // Player Constants
    public static final int CUSTOM_SEEK_CONTROL_STATE = 2; // Every time long press left/right will enter this state
    public static final int EDIT_CUSTOM_SEEK_CONTROL_STATE = 3; // After long press left/right will enter this state
    public static final long DEFAULT_FREQUENCY = 1000;
    public static final String FSMPLAYER_TESTING = "FSM_LOGGING";
    public static final String SUBSTITLE_SIZE = "subs_size";
    public static final String UPNEXT = "Up Next in : ";
    public static final String EP = "EP";
    public static final String S0 = "S0";
    public static final String E = "E";
    public static final String STREAMING = "streaming";
    public static final String Anime = "anime";



    // Shared Preferences Constants
    public static final String PREF_FILE = "Preferences";
    public static final String ANDROI_ID = "android_id";

}
