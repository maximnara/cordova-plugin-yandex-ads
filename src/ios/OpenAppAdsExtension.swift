import YandexMobileAds

let EVENT_APP_OPEN_DID_DISMISS = "appOpenDidDismiss"
let EVENT_APP_OPEN_DID_FAIL_TO_SHOW_WITH_ERROR = "appOpenDidFailToShowWithError"
let EVENT_APP_OPEN_DID_SHOW = "appOpenDidShow"
let EVENT_APP_OPEN_DID_CLICK = "appOpenDidClick"
let EVENT_APP_OPEN_DID_TRACK_IMPRESSION_WITH = "appOpenDidTrackImpressionWith"

extension YandexAdsPlugin {
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
}

// MARK: - YMAAppOpenAdDelegate
extension YandexAdsPlugin: YMAAppOpenAdDelegate {
    func appOpenAdDidDismiss(_ appOpenAd: YMAAppOpenAd) {
        self.appOpenAd?.delegate = nil
        self.appOpenAd = nil

        self.emitWindowEvent(event: EVENT_APP_OPEN_DID_DISMISS)
    }

    func appOpenAd(
        _ appOpenAd: YMAAppOpenAd,
        didFailToShowWithError error: Error
    ) {
        self.appOpenAd = nil

        let data = ErrorData(message: error.localizedDescription)
        self.emitWindowEvent(event: EVENT_APP_OPEN_DID_FAIL_TO_SHOW_WITH_ERROR, data: data)
    }

    func appOpenAdDidShow(_ appOpenAd: YMAAppOpenAd) {
        self.emitWindowEvent(event: EVENT_APP_OPEN_DID_SHOW)
    }

    func appOpenAdDidClick(_ appOpenAd: YMAAppOpenAd) {
        self.emitWindowEvent(event: EVENT_APP_OPEN_DID_CLICK)
    }

    func appOpenAd(_ appOpenAd: YMAAppOpenAd, didTrackImpressionWith impressionData: YMAImpressionData?) {
        self.emitWindowEvent(event: EVENT_APP_OPEN_DID_TRACK_IMPRESSION_WITH)
    }
}

let EVENT_APP_OPEN_DID_LOAD = "appOpenDidLoad"
let EVENT_APP_OPEN_FAILED_TO_LOAD = "appOpenFailedToLoad"

// MARK: - YMAAppOpenAdLoaderDelegate
extension YandexAdsPlugin: YMAAppOpenAdLoaderDelegate {
    func appOpenAdLoader(
        _ adLoader: YMAAppOpenAdLoader,
        didLoad appOpenAd: YMAAppOpenAd
    ) {
        self.appOpenAd = appOpenAd
        self.appOpenAd?.delegate = self
        
        self.emitWindowEvent(event: EVENT_APP_OPEN_DID_LOAD)
    }

    func appOpenAdLoader(
        _ adLoader: YMAAppOpenAdLoader,
        didFailToLoadWithError error: YMAAdRequestError
    ) {
        self.appOpenAd = nil
        let id = error.adUnitId
        let error = error.error
        
        let data = ErrorData(message: error.localizedDescription)
        self.emitWindowEvent(event: EVENT_APP_OPEN_DID_LOAD, data: data)
    }
}
