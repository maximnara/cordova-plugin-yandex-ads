declare interface IAdsEvents {
    readonly interstitial: {
        readonly loaded: string;
        readonly failedToLoad: string;
        readonly shown: string;
        readonly failedToShow: string;
        readonly dismissed: string;
        readonly clicked: string;
        readonly impression: string;
    };
    readonly rewarded: {
        readonly loaded: string;
        readonly failedToLoad: string;
        readonly rewarded: string;
        readonly shown: string;
        readonly failedToShow: string;
        readonly dismissed: string;
        readonly clicked: string;
        readonly impression: string;
    };
    readonly openAppAds: {
        readonly loaded: string;
        readonly failedToLoad: string;
        readonly shown: string;
        readonly failedToShow: string;
        readonly dismissed: string;
        readonly clicked: string;
        readonly impression: string;
    };
    readonly banner: {
        readonly loaded: string;
        readonly failedToLoad: string;
        readonly clicked: string;
        readonly impression: string;
        readonly leftApplication: string;
    };
}
declare interface IInitParams {
    rewardedBlockId?: string;
    interstitialBlockId?: string;
    bannerBlockId?: string;
    openAppBlockId?: string;
    options?: IInitParamsOptions;
}
declare interface IInitParamsOptions {
    bannerAtTop?: boolean;
    bannerSize?: IBannerSize;
}
declare interface IBannerSize {
    height?: number;
    width?: number;
}
declare interface IFunctionParams {
    onSuccess?: Function;
    onFailure?: Function;
}
declare interface IRewardedVideoParams
    extends IFunctionParams {
    placement?: string;
}
declare interface IOpenAppAdsParams
    extends IRewardedVideoParams { }

export declare const events: IAdsEvents;
export declare function isInitialized(): boolean;
export declare function init(params: IInitParams): Promise<any>;
export declare function setUserConsent(value: boolean): Promise<any>;

// #region Rewarded
export declare function showRewardedVideo(params?: IRewardedVideoParams): Promise<any>;
export declare function loadRewardedVideo(params?: IRewardedVideoParams): Promise<any>
// #endregion

// #region Interstitial
export declare function loadInterstitial(params?: IFunctionParams): Promise<any>;
export declare function showInterstitial(params?: IFunctionParams): Promise<any>;
export declare function hideInterstitial(params?: IFunctionParams): Promise<any>;
// #endregion

// #region Banner
export declare function loadBanner(params?: IFunctionParams): Promise<any>;
export declare function reloadBanner(params?: IFunctionParams): Promise<any>;
export declare function showBanner(params?: IFunctionParams): Promise<any>;
export declare function hideBanner(params?: IFunctionParams): Promise<any>;
// #endregion

// #region OpenAppAds
export declare function loadOpenAppAds(params?: IOpenAppAdsParams): Promise<any>;
export declare function showOpenAppAds(params?: IOpenAppAdsParams): Promise<any>;
// #endregion