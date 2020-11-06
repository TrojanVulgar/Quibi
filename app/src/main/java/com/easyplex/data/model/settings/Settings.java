package com.easyplex.data.model.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Settings {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("app_name")
    @Expose
    private String appName;

    @SerializedName("paypal_client_id")
    @Expose
    private String paypalClientId;

    @SerializedName("paypal_amount")
    @Expose
    private String paypalAmount;

    @SerializedName("privacy_policy")
    @Expose
    private String privacyPolicy;


    @SerializedName("tmdb_api_key")
    @Expose
    private String tmdbApiKey;


    @SerializedName("purchase_key")
    @Expose
    private String purchaseKey;


    @SerializedName("stripe_publishable_key")
    @Expose
    private String stripePublishableKey;


    @SerializedName("stripe_secret_key")
    @Expose
    private String stripeSecretKey;




    @SerializedName("app_url_android")
    @Expose
    private String appUrlAndroid;


    @SerializedName("autosubstitles")
    @Expose
    private int autosubstitles;


    @SerializedName("ads_player")
    @Expose
    private int ads;

    @SerializedName("anime")
    @Expose
    private Integer anime;

    @SerializedName("ad_app_id")
    @Expose
    private String adAppId;


    @SerializedName("latestVersion")
    @Expose
    private String latestVersion;


    @SerializedName("update_title")
    @Expose
    private String updateTitle;


    @SerializedName("releaseNotes")
    @Expose
    private String releaseNotes;


    @SerializedName("url")
    @Expose
    private String url;



    @SerializedName("imdb_cover_path")
    @Expose
    private String imdbCoverPath;




    @SerializedName("custom_message")
    @Expose
    private String customMessage;


    @SerializedName("wach_ads_to_unlock")
    @Expose
    private int wachAdsToUnlock;


    @SerializedName("startapp_id")
    @Expose
    private String startappId;




    @SerializedName("ad_unit_id_rewarded")
    @Expose
    private String adUnitIdRewarded;


    @SerializedName("ad_unit_id__facebook_rewarded")
    @Expose
    private String adUnitIdFacebookRewarded;



    @SerializedName("unity_game_id")
    @Expose
    private String unityGameId;



    @SerializedName("default_network")
    @Expose
    private String defaultRewardedNetworkAds;


    @SerializedName("ad_unit_id__appodeal_rewarded")
    @Expose
    private String adUnitIdAppodealRewarded;

    @SerializedName("facebook_show_interstitial")
    @Expose
    private int facebookShowInterstitial;

    @SerializedName("ad_show_interstitial")
    @Expose
    private int adShowInterstitial;

    @SerializedName("ad_interstitial")
    @Expose
    private int adInterstitial;


    @SerializedName("ad_banner")
    @Expose
    private int adBanner;


    @SerializedName("ad_face_audience_interstitial")
    @Expose
    private int adFaceAudienceInterstitial;

    @SerializedName("ad_face_audience_banner")
    @Expose
    private int adFaceAudienceBanner;



    @SerializedName("enable_custom_message")
    @Expose
    private int enableCustomMessage;



    @SerializedName("ad_unit_id_facebook_interstitial_audience")
    @Expose
    private String adUnitIdFacebookInterstitialAudience;


    @SerializedName("ad_unit_id_facebook_banner_audience")
    @Expose
    private String adUnitIdFacebookBannerAudience;



    @SerializedName("ad_unit_id_interstitial")
    @Expose
    private String adUnitIdInterstitial;


    @SerializedName("ad_unit_id_banner")
    @Expose
    private String adUnitIdBanner;


    @SerializedName("featured_home_numbers")
    @Expose
    private int featuredHomeNumbers;


    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;


    public String getAdUnitIdAppodealRewarded() {
        return adUnitIdAppodealRewarded;
    }

    public void setAdUnitIdAppodealRewarded(String adUnitIdAppodealRewarded) {
        this.adUnitIdAppodealRewarded = adUnitIdAppodealRewarded;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTmdbApiKey() {
        return tmdbApiKey;
    }

    public void setTmdbApiKey(String tmdbApiKey) {
        this.tmdbApiKey = tmdbApiKey;
    }

    public String getPurchaseKey() {
        return purchaseKey;
    }

    public void setPurchaseKey(String purchaseKey) {
        this.purchaseKey = purchaseKey;
    }

    public String getAppUrlAndroid() {
        return appUrlAndroid;
    }

    public void setAppUrlAndroid(String appUrlAndroid) {
        this.appUrlAndroid = appUrlAndroid;
    }


    public Integer getAnime() {
        return anime;
    }

    public void setAnime(Integer anime) {
        this.anime = anime;
    }

    public int getAds() {
        return ads;
    }

    public void setAds(int ads) {
        this.ads = ads;
    }

    public String getAdAppId() {
        return adAppId;
    }

    public void setAdAppId(String adAppId) {
        this.adAppId = adAppId;
    }

    public int getAdInterstitial() {
        return adInterstitial;
    }

    public void setAdInterstitial(int adInterstitial) {
        this.adInterstitial = adInterstitial;
    }

    public String getAdUnitIdInterstitial() {
        return adUnitIdInterstitial;
    }

    public void setAdUnitIdInterstitial(String adUnitIdInterstitial) {
        this.adUnitIdInterstitial = adUnitIdInterstitial;
    }


    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }


    public int getEnableCustomMessage() {
        return enableCustomMessage;
    }

    public void setEnableCustomMessage(int enableCustomMessage) {
        this.enableCustomMessage = enableCustomMessage;
    }



    public String getAdUnitIdBanner() {
        return adUnitIdBanner;
    }

    public void setAdUnitIdBanner(String adUnitIdBanner) {
        this.adUnitIdBanner = adUnitIdBanner;
    }


    public int getAdBanner() {
        return adBanner;
    }

    public void setAdBanner(int adBanner) {
        this.adBanner = adBanner;
    }


    public int getFacebookShowInterstitial() {
        return facebookShowInterstitial;
    }

    public void setFacebookShowInterstitial(int facebookShowInterstitial) {
        this.facebookShowInterstitial = facebookShowInterstitial;
    }


    public int getAdShowInterstitial() {
        return adShowInterstitial;
    }

    public void setAdShowInterstitial(int adShowInterstitial) {
        this.adShowInterstitial = adShowInterstitial;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }



    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }


    public String getPaypalClientId() {
        return paypalClientId;
    }

    public void setPaypalClientId(String paypalClientId) {
        this.paypalClientId = paypalClientId;
    }


    public String getStripePublishableKey() {
        return stripePublishableKey;
    }

    public void setStripePublishableKey(String stripePublishableKey) {
        this.stripePublishableKey = stripePublishableKey;
    }

    public String getStripeSecretKey() {
        return stripeSecretKey;
    }

    public void setStripeSecretKey(String stripeSecretKey) {
        this.stripeSecretKey = stripeSecretKey;
    }

    public int getAutosubstitles() {
        return autosubstitles;
    }

    public void setAutosubstitles(int autosubstitles) {
        this.autosubstitles = autosubstitles;
    }


    public String getPaypalAmount() {
        return paypalAmount;
    }

    public void setPaypalAmount(String paypalAmount) {
        this.paypalAmount = paypalAmount;
    }


    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }


    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }


    public String getImdbCoverPath() {
        return imdbCoverPath;
    }

    public void setImdbCoverPath(String imdbCoverPath) {
        this.imdbCoverPath = imdbCoverPath;
    }


    public int getFeaturedHomeNumbers() {
        return featuredHomeNumbers;
    }

    public void setFeaturedHomeNumbers(int featuredHomeNumbers) {
        this.featuredHomeNumbers = featuredHomeNumbers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getAdFaceAudienceInterstitial() {
        return adFaceAudienceInterstitial;
    }

    public void setAdFaceAudienceInterstitial(int adFaceAudienceInterstitial) {
        this.adFaceAudienceInterstitial = adFaceAudienceInterstitial;
    }

    public int getAdFaceAudienceBanner() {
        return adFaceAudienceBanner;
    }

    public void setAdFaceAudienceBanner(int adFaceAudienceBanner) {
        this.adFaceAudienceBanner = adFaceAudienceBanner;
    }

    public String getAdUnitIdFacebookInterstitialAudience() {
        return adUnitIdFacebookInterstitialAudience;
    }

    public void setAdUnitIdFacebookInterstitialAudience(String adUnitIdFacebookInterstitialAudience) {
        this.adUnitIdFacebookInterstitialAudience = adUnitIdFacebookInterstitialAudience;
    }

    public String getAdUnitIdFacebookBannerAudience() {
        return adUnitIdFacebookBannerAudience;
    }

    public void setAdUnitIdFacebookBannerAudience(String adUnitIdFacebookBannerAudience) {
        this.adUnitIdFacebookBannerAudience = adUnitIdFacebookBannerAudience;
    }


    public String getDefaultRewardedNetworkAds() {
        return defaultRewardedNetworkAds;
    }

    public void setDefaultRewardedNetworkAds(String defaultRewardedNetworkAds) {
        this.defaultRewardedNetworkAds = defaultRewardedNetworkAds;
    }


    public int getWachAdsToUnlock() {
        return wachAdsToUnlock;
    }

    public void setWachAdsToUnlock(int wachAdsToUnlock) {
        this.wachAdsToUnlock = wachAdsToUnlock;
    }


    public String getStartappId() {
        return startappId;
    }

    public void setStartappId(String startappId) {
        this.startappId = startappId;
    }

    public String getAdUnitIdRewarded() {
        return adUnitIdRewarded;
    }

    public void setAdUnitIdRewarded(String adUnitIdRewarded) {
        this.adUnitIdRewarded = adUnitIdRewarded;
    }

    public String getAdUnitIdFacebookRewarded() {
        return adUnitIdFacebookRewarded;
    }

    public void setAdUnitIdFacebookRewarded(String adUnitIdFacebookRewarded) {
        this.adUnitIdFacebookRewarded = adUnitIdFacebookRewarded;
    }

    public String getUnityGameId() {
        return unityGameId;
    }

    public void setUnityGameId(String unityGameId) {
        this.unityGameId = unityGameId;
    }


}