import Foundation

import YandexMobileAds

let PLUGIN_NOT_INITIALIZED_ERROR = [
    "code": "plugin_not_initialized",
    "message": "Plugin not initialized, call .init before ads load"
]

@objc(YandexAdsPlugin)
class YandexAdsPlugin: CDVPlugin {
    private var rewardedBlockId: String?
    private var interstitialBlockId: String?
    private var bannerBlockId: String?
    private var openAppBlockId: String?

    public var bannerAtTop: Bool?
    public var bannerSize: NSDictionary?

    public var interstitialAd: YMAInterstitialAd?
    public var rewardedAd: YMARewardedAd?
    public var appOpenAd: YMAAppOpenAd?

    override init() {
        self.bannerAtTop = false
    }

    @objc(run:)
    func run(command: CDVInvokedUrlCommand) {
        self.rewardedBlockId = command.arguments[0] as? String ?? ""
        self.interstitialBlockId = command.arguments[1] as? String ?? ""
        self.bannerBlockId = command.arguments[2] as? String ?? ""
        self.openAppBlockId = command.arguments[3] as? String ?? ""

        let options = command.arguments[4] as! NSDictionary;

        self.bannerAtTop = options["bannerAtTop"] as? Bool;

        self.bannerSize = options["bannerSize"] as? NSDictionary;

        self.sendResult(command: command);
    }

    private var bannerAdViewCache: YMAAdView?;

    func getBannerAdView() -> YMAAdView {
        if self.bannerAdViewCache != nil {
            return self.bannerAdViewCache!
        }

        let width = self.webView.safeAreaLayoutGuide.layoutFrame.width
        var adSize = YMABannerAdSize.stickySize(withContainerWidth: width)

        if (self.bannerSize != nil && self.bannerSize?["width"] != nil && self.bannerSize?["height"] != nil) {
            adSize = YMABannerAdSize.inlineSize(withWidth: self.bannerSize?["width"] as! CGFloat, maxHeight: self.bannerSize?["height"] as! CGFloat)
        }

        let adView = YMAAdView(adUnitID: self.bannerBlockId!, adSize: adSize)

        adView.delegate = self

        self.bannerAdViewCache = adView

        return adView
    }

    @objc(loadBanner:)
    func loadBanner(command: CDVInvokedUrlCommand) {
        if self.bannerBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        self.getBannerAdView().loadAd()

        self.sendResult(command: command);
    }

    @objc(showBanner:)
    func showBanner(command: CDVInvokedUrlCommand) {
        if self.bannerBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        let banner = self.getBannerAdView();
        print(banner)

        if self.bannerAtTop != nil && self.bannerAtTop == true {
            banner.displayAtTop(in: webView)
        } else {
            banner.displayAtBottom(in: webView)
        }

        self.sendResult(command: command);
    }

    @objc(hideBanner:)
    func hideBanner(command: CDVInvokedUrlCommand) {
        if self.bannerBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        self.getBannerAdView().delegate = nil
        self.getBannerAdView().removeFromSuperview()
        self.bannerAdViewCache = nil

        self.sendResult(command: command);
    }

    private lazy var interstitialAdLoader: YMAInterstitialAdLoader = {
        let loader = YMAInterstitialAdLoader()

        loader.delegate = self

        return loader
    }()

    @objc(loadInterstitial:)
    func loadInterstitial(command: CDVInvokedUrlCommand) {
        if self.interstitialBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        let configuration = YMAAdRequestConfiguration(adUnitID: self.interstitialBlockId!)
        self.interstitialAdLoader.loadAd(with: configuration)

        self.sendResult(command: command);
    }

    @objc(showInterstitial:)
    func showInterstitial(command: CDVInvokedUrlCommand) {
        if self.interstitialBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        self.interstitialAd?.show(from: viewController)

        self.sendResult(command: command);
    }

    private lazy var rewardedAdLoader: YMARewardedAdLoader = {
        let loader = YMARewardedAdLoader()

        loader.delegate = self

        return loader
    }()

    @objc(loadRewardedVideo:)
    func loadRewardedVideo(command: CDVInvokedUrlCommand) {
        if self.rewardedBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        let configuration = YMAAdRequestConfiguration(adUnitID: self.rewardedBlockId!)
        self.rewardedAdLoader.loadAd(with: configuration)

        self.sendResult(command: command);
    }

    @objc(showRewardedVideo:)
    func showRewardedVideo(command: CDVInvokedUrlCommand) {
        if self.rewardedBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        self.rewardedAd?.show(from: viewController)

        self.sendResult(command: command);
    }

    private lazy var appOpenAdLoader: YMAAppOpenAdLoader = {
            let loader = YMAAppOpenAdLoader()
            loader.delegate = self
            return loader
    }()

    @objc(loadOpenAppAds:)
    func loadOpenAppAds(command: CDVInvokedUrlCommand) {
        if self.openAppBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        let configuration = YMAAdRequestConfiguration(adUnitID: self.openAppBlockId!)
        appOpenAdLoader.loadAd(with: configuration)

        self.sendResult(command: command);
    }

    @objc(showOpenAppAds:)
    func showOpenAppAds(command: CDVInvokedUrlCommand) {
        if self.openAppBlockId == nil {
            self.sendError(command: command, code: PLUGIN_NOT_INITIALIZED_ERROR["code"]!, message: PLUGIN_NOT_INITIALIZED_ERROR["message"]!);
            return;
        }

        self.appOpenAd?.show(from: viewController)

        self.sendResult(command: command);
    }

    @objc(setUserConsent:)
    func setUserConsent(command: CDVInvokedUrlCommand) {
        YMAMobileAds.setUserConsent(command.arguments[0] as? Bool ?? false);

        self.sendResult(command: command);
    }
}
