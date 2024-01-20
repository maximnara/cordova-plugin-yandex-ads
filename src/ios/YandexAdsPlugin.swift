import Foundation

import YandexMobileAds

@objc(YandexAdsPlugin)
class YandexAdsPlugin: CDVPlugin {
    private var rewardedBlockId: String?
    private var interstitialBlockId: String?
    private var bannerBlockId: String?
    private var openAppBlockId: String?
    
    public var bannerAtTop: Bool
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
        
        self.bannerAtTop = options["bannerAtTop"] as! Bool;

        self.bannerSize = options["bannerSize"] as! NSDictionary;

        self.sendResult(command: command, status: CDVCommandStatus_OK);
    }
    
    private lazy var adView: YMAAdView = {
        let width = webView.safeAreaLayoutGuide.layoutFrame.width
        var adSize = YMABannerAdSize.stickySize(withContainerWidth: width)
        
        if (self.bannerSize != nil && self.bannerSize?["width"] != nil && self.bannerSize?["height"] != nil) {
            adSize = YMABannerAdSize.inlineSize(withWidth: self.bannerSize?["width"] as! CGFloat, maxHeight: self.bannerSize?["height"] as! CGFloat)
        }
        
        let adView = YMAAdView(adUnitID: self.bannerBlockId!, adSize: adSize)
        
        adView.delegate = self
        
        return adView
    }()
    
    @objc(loadBanner:)
    func loadBanner(command: CDVInvokedUrlCommand) {
        self.adView.loadAd()
    }
    
    @objc(showBanner:)
    func showBanner(command: CDVInvokedUrlCommand) {
        if self.bannerAtTop {
            adView.displayAtTop(in: webView)
        } else {
            adView.displayAtBottom(in: webView)
        }
    }
    
    @objc(hideBanner:)
    func hideBanner(command: CDVInvokedUrlCommand) {
        adView.delegate = nil
        adView.removeFromSuperview()
    }
    
    private lazy var interstitialAdLoader: YMAInterstitialAdLoader = {
        let loader = YMAInterstitialAdLoader()

        loader.delegate = self

        return loader
    }()
    
    @objc(loadInterstitial:)
    func loadInterstitial(command: CDVInvokedUrlCommand) {
        let configuration = YMAAdRequestConfiguration(adUnitID: self.interstitialBlockId!)
        self.interstitialAdLoader.loadAd(with: configuration)
    }
    
    @objc(showInterstitial:)
    func showInterstitial(command: CDVInvokedUrlCommand) {
        self.interstitialAd?.show(from: viewController)
    }

    private lazy var rewardedAdLoader: YMARewardedAdLoader = {
        let loader = YMARewardedAdLoader()
        loader.delegate = self
        return loader
    }()
    
    @objc(loadRewardedVideo:)
    func loadRewardedVideo(command: CDVInvokedUrlCommand) {
        let configuration = YMAAdRequestConfiguration(adUnitID: self.rewardedBlockId!)
        self.rewardedAdLoader.loadAd(with: configuration)
    }
    
    @objc(showRewardedVideo:)
    func showRewardedVideo(command: CDVInvokedUrlCommand) {
        self.rewardedAd?.show(from: viewController)
    }

    private lazy var appOpenAdLoader: YMAAppOpenAdLoader = {
            let loader = YMAAppOpenAdLoader()
            loader.delegate = self
            return loader
    }()
    
    @objc(loadOpenAppAds:)
    func loadOpenAppAds(command: CDVInvokedUrlCommand) {
        let configuration = YMAAdRequestConfiguration(adUnitID: self.openAppBlockId!)
        appOpenAdLoader.loadAd(with: configuration)
    }
    
    @objc(showOpenAppAds:)
    func showOpenAppAds(command: CDVInvokedUrlCommand) {
        self.appOpenAd?.show(from: viewController)
    }
    
    @objc(setUserConsent:)
    func setUserConsent(command: CDVInvokedUrlCommand) {
        YMAMobileAds.setUserConsent(command.arguments[0] as? Bool ?? false)
    }
}
