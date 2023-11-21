import YandexMobileAds

let EVENT_BANNER_DID_LOAD = "bannerDidLoad"
let EVENT_BANNER_DID_CLICK = "bannerDidClick"
let EVENT_BANNER_DID_TRACK_IMPRESSION_WITH = "bannerDidTrackImpressionWith"
let EVENT_BANNER_FAILED_TO_LOAD = "bannerFailedToLoad"
let EVENT_BANNER_WILL_LEAVE_APPLICATION = "bannerWillLeaveApplication"
let EVENT_BANNER_WILL_SHOW = "bannerWillShow"
let EVENT_BANNER_DID_DISMISS = "bannerDidDismiss"

extension YandexAdsPlugin: YMAAdViewDelegate {
    func adViewDidLoad(_ adView: YMAAdView) {
        self.emitWindowEvent(event: EVENT_BANNER_DID_LOAD)
    }

    func adViewDidClick(_ adView: YMAAdView) {
        self.emitWindowEvent(event: EVENT_BANNER_DID_CLICK)
    }

    func adView(_ adView: YMAAdView, didTrackImpressionWith impressionData: YMAImpressionData?) {
        self.emitWindowEvent(event: EVENT_BANNER_DID_TRACK_IMPRESSION_WITH)
    }

    func adViewDidFailLoading(_ adView: YMAAdView, error: Error) {
        let data = ErrorData(message: error.localizedDescription)
        self.emitWindowEvent(event: EVENT_BANNER_FAILED_TO_LOAD, data: data)
    }

    func adViewWillLeaveApplication(_ adView: YMAAdView) {
        self.emitWindowEvent(event: EVENT_BANNER_WILL_LEAVE_APPLICATION)
    }

    func adView(_ adView: YMAAdView, willPresentScreen viewController: UIViewController?) {
        self.emitWindowEvent(event: EVENT_BANNER_WILL_SHOW)
    }

    func adView(_ adView: YMAAdView, didDismissScreen viewController: UIViewController?) {
        self.emitWindowEvent(event: EVENT_BANNER_DID_DISMISS)
    }
}
