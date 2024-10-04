import Foundation

import YandexMobileAds

let PLUGIN_NOT_INITIALIZED_ERROR = [
    "code": "plugin_not_initialized",
    "message": "Plugin not initialized, call .init before ads load"
]

@objc(YandexAdsPlugin)
class YandexAdsPlugin: CDVPlugin {
    public var rewardedBlockId: String?
    public var interstitialBlockId: String?
    public var bannerBlockId: String?
    public var openAppBlockId: String?
    public var instreamBlockId: String?
    public var feedBlockId: String?

    public var bannerAtTop: Bool?
    public var bannerSize: NSDictionary?

    public var interstitialAd: InterstitialAd?
    public var rewardedAd: RewardedAd?
    public var appOpenAd: AppOpenAd?
    public var bannerAdViewCache: AdView?

    public var superView: UIView?
    public var stackViewInlineBannerView: UIStackView?
    public var bannerStackView: UIView?
    public var bannerReloaded: Bool?

    override init() {
        super.init()

        self.bannerAtTop = false
        self.bannerReloaded = false
    }

    override func pluginInitialize() {
        super.pluginInitialize()

        self.superView = webView.superview
    }

    @objc(run:)
    func run(command: CDVInvokedUrlCommand) {
        self.rewardedBlockId = command.arguments[0] as? String ?? ""
        self.interstitialBlockId = command.arguments[1] as? String ?? ""
        self.bannerBlockId = command.arguments[2] as? String ?? ""
        self.openAppBlockId = command.arguments[3] as? String ?? ""
        self.instreamBlockId = command.arguments[4] as? String ?? ""
        self.feedBlockId = command.arguments[5] as? String ?? ""

        let options = command.arguments[6] as! NSDictionary;

        self.bannerAtTop = options["bannerAtTop"] as? Bool;

        self.bannerSize = options["bannerSize"] as? NSDictionary;

        self.sendResult(command: command);
    }

    public lazy var interstitialAdLoader: InterstitialAdLoader = {
        let loader = InterstitialAdLoader()

        loader.delegate = self

        return loader
    }()

    public lazy var rewardedAdLoader: RewardedAdLoader = {
        let loader = RewardedAdLoader()

        loader.delegate = self

        return loader
    }()

    public lazy var appOpenAdLoader: AppOpenAdLoader = {
        let loader = AppOpenAdLoader()

        loader.delegate = self

        return loader
    }()

    @objc(setUserConsent:)
    func setUserConsent(command: CDVInvokedUrlCommand) {
        MobileAds.setUserConsent(command.arguments[0] as? Bool ?? false);

        self.sendResult(command: command);
    }
}
