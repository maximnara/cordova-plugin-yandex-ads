/**
 * Yandex Ads Plugin TypeScript definitions
 */

export interface YandexAdsEvents {
    interstitial: {
        loaded: 'interstitialDidLoad';
        failedToLoad: 'interstitialFailedToLoad';
        shown: 'interstitialDidShow';
        failedToShow: 'interstitialDidFailToShowWithError';
        dismissed: 'interstitialDidDismiss';
        clicked: 'interstitialDidClick';
        impression: 'interstitialDidTrackImpressionWith';
    };
    rewarded: {
        loaded: 'rewardedDidLoad';
        failedToLoad: 'rewardedFailedToLoad';
        rewarded: 'rewardedDidReward';
        shown: 'rewardedDidShow';
        failedToShow: 'rewardedDidFailToShowWithError';
        dismissed: 'rewardedDidDismiss';
        clicked: 'rewardedDidClick';
        impression: 'rewardedDidTrackImpressionWith';
    };
    openAppAds: {
        loaded: 'appOpenDidLoad';
        failedToLoad: 'appOpenFailedToLoad';
        shown: 'appOpenDidShow';
        failedToShow: 'appOpenDidFailToShowWithError';
        dismissed: 'appOpenDidDismiss';
        clicked: 'appOpenDidClick';
        impression: 'appOpenDidTrackImpressionWith';
    };
    banner: {
        loaded: 'bannerDidLoad';
        failedToLoad: 'bannerFailedToLoad';
        clicked: 'bannerDidClick';
        impression: 'bannerDidTrackImpressionWith';
        leftApplication: 'bannerWillLeaveApplication';
    };
    feed: {
        loaded: 'feedDidLoad';
        failedToLoad: 'feedFailedToLoad';
        clicked: 'feedDidClick';
        impression: 'feedDidTrackImpressionWith';
    };
    instream: {
        loaded: 'instreamDidLoad';
        failedToLoad: 'instreamFailedToLoad';
        error: 'instreamError';
        completed: 'instreamAdCompleted';
        prepared: 'instreamAdPrepared';
        bufferingFinished: 'instreamAdBufferingFinished';
        bufferingStarted: 'instreamAdBufferingStarted';
        adCompleted: 'instreamAdCompleted';
        adPaused: 'instreamAdPaused';
        adPreparedPlayer: 'instreamAdPreparedPlayer';
        adResumed: 'instreamAdResumed';
        adSkipped: 'instreamAdSkipped';
        adStarted: 'instreamAdStarted';
        adStopped: 'instreamAdStopped';
        adErrorPlayer: 'instreamAdErrorPlayer';
        volumeChanged: 'instreamAdVolumeChanged';
    };
}

export interface YandexAdsInitParams {
    rewardedBlockId?: string;
    interstitialBlockId?: string;
    bannerBlockId?: string;
    openAppBlockId?: string;
    instreamBlockId?: string;
    feedBlockId?: string;
    options?: Record<string, any>;
}

export interface YandexAdsParams {
    placement?: string;
}

export interface YandexAds {
    /**
     * Event names for different ad types
     */
    readonly events: YandexAdsEvents;

    /**
     * Returns the state of initialization
     */
    isInitialized(): boolean;

    /**
     * Initializes Yandex Ads
     * @param params - Initialization parameters
     * @returns Promise that resolves when initialization is complete
     */
    init(params: YandexAdsInitParams): Promise<void>;

    /**
     * Loads rewarded video ad
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is loaded
     */
    loadRewardedVideo(params?: YandexAdsParams): Promise<void>;

    /**
     * Shows rewarded video ad
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is shown
     */
    showRewardedVideo(params?: YandexAdsParams): Promise<void>;

    /**
     * Loads open app ads
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is loaded
     */
    loadOpenAppAds(params?: YandexAdsParams): Promise<void>;

    /**
     * Shows open app ads
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is shown
     */
    showOpenAppAds(params?: YandexAdsParams): Promise<void>;

    /**
     * Shows banner ad
     * @param params - Optional parameters
     * @returns Promise that resolves when banner is shown
     */
    showBanner(params?: YandexAdsParams): Promise<void>;

    /**
     * Hides banner ad
     * @param params - Optional parameters
     * @returns Promise that resolves when banner is hidden
     */
    hideBanner(params?: YandexAdsParams): Promise<void>;

    /**
     * Loads banner ad
     * @param params - Optional parameters
     * @returns Promise that resolves when banner is loaded
     */
    loadBanner(params?: YandexAdsParams): Promise<void>;

    /**
     * Reloads banner ad
     * @param params - Optional parameters
     * @returns Promise that resolves when banner is reloaded
     */
    reloadBanner(params?: YandexAdsParams): Promise<void>;

    /**
     * Loads interstitial ad
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is loaded
     */
    loadInterstitial(params?: YandexAdsParams): Promise<void>;

    /**
     * Shows interstitial ad
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is shown
     */
    showInterstitial(params?: YandexAdsParams): Promise<void>;

    /**
     * Loads instream ads
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is loaded
     */
    loadInstream(params?: YandexAdsParams): Promise<void>;

    /**
     * Shows instream ads
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is shown
     */
    showInstream(params?: YandexAdsParams): Promise<void>;

    /**
     * Hides instream ads
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is hidden
     */
    hideInstream(params?: YandexAdsParams): Promise<void>;

    /**
     * Loads feed ads
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is loaded
     */
    loadFeed(params?: YandexAdsParams): Promise<void>;

    /**
     * Shows feed ads
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is shown
     */
    showFeed(params?: YandexAdsParams): Promise<void>;

    /**
     * Hides feed ads
     * @param params - Optional parameters
     * @returns Promise that resolves when ad is hidden
     */
    hideFeed(params?: YandexAdsParams): Promise<void>;

    /**
     * Sets user consent for ads
     * @param value - Consent value
     * @param params - Optional parameters
     * @returns Promise that resolves when consent is set
     */
    setUserConsent(value: boolean, params?: YandexAdsParams): Promise<void>;
}

declare const YandexAds: YandexAds;

export default YandexAds;