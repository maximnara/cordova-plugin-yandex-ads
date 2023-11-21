import YandexMobileAds

let EVENT_APP_OPEN_DID_DISMISS = "appOpenDidDismiss"
let EVENT_APP_OPEN_DID_FAIL_TO_SHOW_WITH_ERROR = "appOpenDidFailToShowWithError"
let EVENT_APP_OPEN_DID_SHOW = "appOpenDidShow"
let EVENT_APP_OPEN_DID_CLICK = "appOpenDidClick"
let EVENT_APP_OPEN_DID_TRACK_IMPRESSION_WITH = "appOpenDidTrackImpressionWith"

// MARK: - YMAAppOpenAdDelegate
extension YandexAdsPlugin: YMAAppOpenAdDelegate {
    func appOpenAdDidDismiss(_ appOpenAd: YMAAppOpenAd) {
        self.appOpenAd = nil
        appOpenDelegate?.appOpenAdControllerDidDismiss(self)
        
        self.emitWindowEvent(event: EVENT_APP_OPEN_DID_DISMISS)
    }

    func appOpenAd(
        _ appOpenAd: YMAAppOpenAd,
        didFailToShowWithError error: Error
    ) {
        self.appOpenAd = nil
        appOpenDelegate?.appOpenAdController(self, didFailToShowWithError: error)
        
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
        
        appOpenDelegate?.appOpenAdControllerDidLoad(self)
        
        self.emitWindowEvent(event: EVENT_APP_OPEN_DID_LOAD)
    }

    func appOpenAdLoader(
        _ adLoader: YMAAppOpenAdLoader,
        didFailToLoadWithError error: YMAAdRequestError
    ) {
        self.appOpenAd = nil
        let id = error.adUnitId
        let error = error.error

        appOpenDelegate?.appOpenAdController(self, didFailToLoadWithError: error)
        
        let data = ErrorData(message: error.localizedDescription)
        self.emitWindowEvent(event: EVENT_APP_OPEN_DID_LOAD, data: data)
    }
}
