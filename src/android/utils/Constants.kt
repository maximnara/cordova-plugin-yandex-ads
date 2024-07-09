package io.luzh.cordova.plugin.utils

internal object Constants {
    const val YANDEX_ADS_TAG = "YANDEX_ADS"

    const val KEY_BLOCK_ID_REWARDED = 0
    const val KEY_BLOCK_ID_INTERSTITIAL = 1
    const val KEY_BLOCK_ID_BANNER = 2
    const val KEY_BLOCK_ID_OPEN_APP = 3
    const val KEY_OPTIONS = 4
    const val KEY_BANNER_AT_TOP = "bannerAtTop"
    const val KEY_BANNER_SIZE = "bannerSize"
}

internal object ConstantsActions {
    // global
    const val ACTION_RUN: String = "run"
    const val ACTION_SET_USER_CONSENT: String = "setUserConsent"

    // rewarded video
    const val ACTION_lOAD_REWARDED_VIDEO: String = "loadRewardedVideo"
    const val ACTION_SHOW_REWARDED_VIDEO: String = "showRewardedVideo"

    // banner
    const val ACTION_LOAD_BANNER: String = "loadBanner"
    const val ACTION_SHOW_BANNER: String = "showBanner"
    const val ACTION_HIDE_BANNER: String = "hideBanner"
    const val ACTION_RELOAD_BANNER: String = "reloadBanner"

    // interstitial
    const val ACTION_LOAD_INTERSTITIAL: String = "loadInterstitial"
    const val ACTION_SHOW_INTERSTITIAL: String = "showInterstitial"

    // open app
    const val ACTION_LOAD_OPEN_APP_ADS: String = "loadOpenAppAds"
    const val ACTION_SHOW_OPEN_APP_ADS: String = "showOpenAppAds"

    // instream
    const val ACTION_LOAD_INSTREAM_APP_ADS: String = "loadInstreamAppAds"
    const val ACTION_HIDE_INSTREAM_APP_ADS: String = "hideInstreamAppAds"
}

internal object ConstantsEvents {
    // Interstitial events
    const val EVENT_INTERSTITIAL_LOADED: String = "interstitialDidLoad"
    const val EVENT_INTERSTITIAL_FAILED_TO_LOAD: String = "interstitialFailedToLoad"
    const val EVENT_INTERSTITIAL_SHOWN: String = "interstitialDidShow"
    const val EVENT_INTERSTITIAL_FAILED_TO_SHOW: String = "interstitialDidFailToShowWithError"
    const val EVENT_INTERSTITIAL_AD_DISMISSED: String = "interstitialDidDismiss"
    const val EVENT_INTERSTITIAL_AD_CLICKED: String = "interstitialDidClick"
    const val EVENT_INTERSTITIAL_AD_IMPRESSION: String = "interstitialDidTrackImpressionWith"

    // Open App Ad events
    const val EVENT_APP_OPEN_ADS_LOADED: String = "appOpenDidLoad"
    const val EVENT_APP_OPEN_ADS_FAILED_TO_LOAD: String = "appOpenFailedToLoad"
    const val EVENT_APP_OPEN_ADS_SHOWN: String = "appOpenDidShow"
    const val EVENT_APP_OPEN_ADS_FAILED_TO_SHOW: String = "appOpenDidFailToShowWithError"
    const val EVENT_APP_OPEN_ADS_DISMISSED: String = "appOpenDidDismiss"
    const val EVENT_APP_OPEN_ADS_CLICKED: String = "appOpenDidClick"
    const val EVENT_APP_OPEN_ADS_IMPRESSION: String = "appOpenDidTrackImpressionWith"

    // Rewarded events
    const val EVENT_REWARDED_VIDEO_LOADED: String = "rewardedDidLoad"
    const val EVENT_REWARDED_VIDEO_FAILED_TO_LOAD: String = "rewardedFailedToLoad"
    const val EVENT_REWARDED_VIDEO_REWARDED: String = "rewardedDidReward"
    const val EVENT_REWARDED_VIDEO_SHOWN: String = "rewardedDidShow"
    const val EVENT_REWARDED_VIDEO_FAILED_TO_SHOW: String = "rewardedDidFailToShowWithError"
    const val EVENT_REWARDED_VIDEO_AD_DISMISSED: String = "rewardedDidDismiss"
    const val EVENT_REWARDED_VIDEO_AD_CLICKED: String = "rewardedDidClick"
    const val EVENT_REWARDED_VIDEO_AD_IMPRESSION: String = "rewardedDidTrackImpressionWith"

    // Banner events
    const val EVENT_BANNER_DID_LOAD: String = "bannerDidLoad"
    const val EVENT_BANNER_FAILED_TO_LOAD: String = "bannerFailedToLoad"
    const val EVENT_BANNER_DID_CLICK: String = "bannerDidClick"
    const val EVENT_BANNER_IMPRESSION: String = "bannerDidTrackImpressionWith"
    const val EVENT_BANNER_LEFT_APPLICATION: String = "bannerWillLeaveApplication"

    // Feed events
    const val EVENT_FEED_LOADED: String = "feedDidLoad"
    const val EVENT_FEED_FAILED_TO_LOAD: String = "feedFailedToLoad"
    const val EVENT_FEED_CLICKED: String = "feedDidClick"
    const val EVENT_FEED_IMPRESSION: String = "feedDidTrackImpressionWith"

    // Instream events
    const val EVENT_INSTREAM_LOADED: String = "instreamDidLoad"
    const val EVENT_INSTREAM_FAILED_TO_LOAD = "instreamFailedToLoad"
    const val EVENT_INSTREAM_ERROR = "instreamError"
    const val EVENT_INSTREAM_AD_COMPLEATED = "instreamAdCompleated"
    const val EVENT_INSTREAM_AD_PREPARED = "instreamAdPrepared"
}