import YandexMobileAds

let EVENT_INTERSTITIAL_DID_LOAD = "interstitialDidLoad"
let EVENT_INTERSTITIAL_FAILED_TO_LOAD = "interstitialFailedToLoad"

extension YandexAdsPlugin {
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
}

extension YandexAdsPlugin: YMAInterstitialAdLoaderDelegate {
    func interstitialAdLoader(_ adLoader: YMAInterstitialAdLoader, didLoad interstitialAd: YMAInterstitialAd) {
        self.interstitialAd = interstitialAd
        self.interstitialAd?.delegate = self
        
        self.emitWindowEvent(event: EVENT_INTERSTITIAL_DID_LOAD)
    }

    func interstitialAdLoader(_ adLoader: YMAInterstitialAdLoader, didFailToLoadWithError error: YMAAdRequestError) {
        let id = error.adUnitId
        let error = error.error
        
        let data = ErrorData(id: id, message: error.localizedDescription)
        self.emitWindowEvent(event: EVENT_INTERSTITIAL_FAILED_TO_LOAD, data: data)
    }
}

let EVENT_INTERSTITIAL_DID_FAIL_TO_SHOW_WITH_ERROR = "interstitialDidFailToShowWithError"
let EVENT_INTERSTITIAL_DID_SHOW = "interstitialDidShow"
let EVENT_INTERSTITIAL_DID_DISMISS = "interstitialDidDismiss"
let EVENT_INTERSTITIAL_DID_CLICK = "interstitialDidClick"
let EVENT_INTERSTITIAL_DID_TRACK_IMPRESSION_WITH = "interstitialDidTrackImpressionWith"

extension YandexAdsPlugin: YMAInterstitialAdDelegate {
    func interstitialAd(_ interstitialAd: YMAInterstitialAd, didFailToShowWithError error: Error) {
        let data = ErrorData(message: error.localizedDescription)
        self.emitWindowEvent(event: EVENT_INTERSTITIAL_DID_FAIL_TO_SHOW_WITH_ERROR, data: data)
    }

    func interstitialAdDidShow(_ interstitialAd: YMAInterstitialAd) {
        self.emitWindowEvent(event: EVENT_INTERSTITIAL_DID_SHOW)
    }

    func interstitialAdDidDismiss(_ interstitialAd: YMAInterstitialAd) {
        self.emitWindowEvent(event: EVENT_INTERSTITIAL_DID_DISMISS)
    }

    func interstitialAdDidClick(_ interstitialAd: YMAInterstitialAd) {
        self.emitWindowEvent(event: EVENT_INTERSTITIAL_DID_CLICK)
    }

    func interstitialAd(_ interstitialAd: YMAInterstitialAd, didTrackImpressionWith impressionData: YMAImpressionData?) {
        self.emitWindowEvent(event: EVENT_INTERSTITIAL_DID_TRACK_IMPRESSION_WITH)
    }
}
