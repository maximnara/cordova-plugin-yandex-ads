package io.luzh.cordova.plugin.utils

internal object Constants {
    const val YANDEX_ADS_TAG = "YANDEX_ADS"

    const val KEY_BLOCK_ID_REWARDED = 0
    const val KEY_BLOCK_ID_INTERSTITIAL = 1
    const val KEY_BLOCK_ID_BANNER = 2
    const val KEY_BLOCK_ID_OPEN_APP = 3
    const val KEY_BLOCK_ID_INSTREAM = 4
    const val KEY_BLOCK_ID_FEED = 5
    const val KEY_OPTIONS = 6
    const val KEY_BANNER_AT_TOP = "bannerAtTop"
    const val KEY_BANNER_SIZE = "bannerSize"
}

internal object ConstantsActions {
    // global
    const val ACTION_RUN = "run"
    const val ACTION_SET_USER_CONSENT = "setUserConsent"

    // rewarded video
    const val ACTION_lOAD_REWARDED_VIDEO = "loadRewardedVideo"
    const val ACTION_SHOW_REWARDED_VIDEO = "showRewardedVideo"

    // banner
    const val ACTION_LOAD_BANNER = "loadBanner"
    const val ACTION_SHOW_BANNER = "showBanner"
    const val ACTION_HIDE_BANNER = "hideBanner"
    const val ACTION_RELOAD_BANNER = "reloadBanner"

    // interstitial
    const val ACTION_LOAD_INTERSTITIAL = "loadInterstitial"
    const val ACTION_SHOW_INTERSTITIAL = "showInterstitial"

    // open app
    const val ACTION_LOAD_OPEN_APP_ADS = "loadOpenAppAds"
    const val ACTION_SHOW_OPEN_APP_ADS = "showOpenAppAds"

    // instream
    const val ACTION_LOAD_INSTREAM_APP_ADS = "loadInstream"
    const val ACTION_SHOW_INSTREAM_APP_ADS = "showInstream"
    const val ACTION_HIDE_INSTREAM_APP_ADS = "hideInstream"

    // feed
    const val ACTION_LOAD_FEED_APP_ADS = "loadFeed"
    const val ACTION_SHOW_FEED_APP_ADS = "showFeed"
    const val ACTION_HIDE_FEED_APP_ADS = "hideFeed"
}

internal object ConstantsEvents {
    // Interstitial events
    const val EVENT_INTERSTITIAL_LOADED = "interstitialDidLoad"
    const val EVENT_INTERSTITIAL_FAILED_TO_LOAD = "interstitialFailedToLoad"
    const val EVENT_INTERSTITIAL_SHOWN = "interstitialDidShow"
    const val EVENT_INTERSTITIAL_FAILED_TO_SHOW = "interstitialDidFailToShowWithError"
    const val EVENT_INTERSTITIAL_AD_DISMISSED = "interstitialDidDismiss"
    const val EVENT_INTERSTITIAL_AD_CLICKED = "interstitialDidClick"
    const val EVENT_INTERSTITIAL_AD_IMPRESSION = "interstitialDidTrackImpressionWith"

    // Open App Ad events
    const val EVENT_APP_OPEN_ADS_LOADED = "appOpenDidLoad"
    const val EVENT_APP_OPEN_ADS_FAILED_TO_LOAD = "appOpenFailedToLoad"
    const val EVENT_APP_OPEN_ADS_SHOWN = "appOpenDidShow"
    const val EVENT_APP_OPEN_ADS_FAILED_TO_SHOW = "appOpenDidFailToShowWithError"
    const val EVENT_APP_OPEN_ADS_DISMISSED = "appOpenDidDismiss"
    const val EVENT_APP_OPEN_ADS_CLICKED = "appOpenDidClick"
    const val EVENT_APP_OPEN_ADS_IMPRESSION = "appOpenDidTrackImpressionWith"

    // Rewarded events
    const val EVENT_REWARDED_VIDEO_LOADED = "rewardedDidLoad"
    const val EVENT_REWARDED_VIDEO_FAILED_TO_LOAD = "rewardedFailedToLoad"
    const val EVENT_REWARDED_VIDEO_REWARDED = "rewardedDidReward"
    const val EVENT_REWARDED_VIDEO_SHOWN = "rewardedDidShow"
    const val EVENT_REWARDED_VIDEO_FAILED_TO_SHOW = "rewardedDidFailToShowWithError"
    const val EVENT_REWARDED_VIDEO_AD_DISMISSED = "rewardedDidDismiss"
    const val EVENT_REWARDED_VIDEO_AD_CLICKED = "rewardedDidClick"
    const val EVENT_REWARDED_VIDEO_AD_IMPRESSION = "rewardedDidTrackImpressionWith"

    // Banner events
    const val EVENT_BANNER_DID_LOAD = "bannerDidLoad"
    const val EVENT_BANNER_FAILED_TO_LOAD = "bannerFailedToLoad"
    const val EVENT_BANNER_DID_CLICK = "bannerDidClick"
    const val EVENT_BANNER_IMPRESSION = "bannerDidTrackImpressionWith"
    const val EVENT_BANNER_LEFT_APPLICATION = "bannerWillLeaveApplication"

    // Feed events
    const val EVENT_FEED_LOADED = "feedDidLoad"
    const val EVENT_FEED_FAILED_TO_LOAD = "feedFailedToLoad"
    const val EVENT_FEED_CLICKED = "feedDidClick"
    const val EVENT_FEED_IMPRESSION = "feedDidTrackImpressionWith"

    // Instream events
    const val EVENT_INSTREAM_LOADED = "instreamDidLoad"
    const val EVENT_INSTREAM_FAILED_TO_LOAD = "instreamFailedToLoad"
    const val EVENT_INSTREAM_ERROR = "instreamError"
    const val EVENT_INSTREAM_AD_COMPLEATED = "instreamAdCompleted"
    const val EVENT_INSTREAM_AD_PREPARED = "instreamAdPrepared"
}
