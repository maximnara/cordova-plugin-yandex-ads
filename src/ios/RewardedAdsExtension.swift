import YandexMobileAds

let EVENT_REWARDED_DID_LOAD = "rewardedDidLoad"
let EVENT_REWARDED_FAILED_TO_LOAD = "rewardedFailedToLoad"

// MARK: - YMARewardedAdLoaderDelegate
extension YandexAdsPlugin: YMARewardedAdLoaderDelegate {
    func rewardedAdLoader(_ adLoader: YMARewardedAdLoader, didLoad rewardedAd: YMARewardedAd) {
        self.rewardedAd = rewardedAd
        self.rewardedAd?.delegate = self
        
        self.emitWindowEvent(event: EVENT_REWARDED_DID_LOAD)
    }

    func rewardedAdLoader(_ adLoader: YMARewardedAdLoader, didFailToLoadWithError error: YMAAdRequestError) {
        let id = error.adUnitId
        let error = error.error
        
        let data = ErrorData(id: id, message: error.localizedDescription)
        self.emitWindowEvent(event: EVENT_REWARDED_FAILED_TO_LOAD, data: data)
    }
}

let EVENT_REWARDED_DID_REWARD = "rewardedDidReward"
let EVENT_REWARDED_DID_FAIL_TO_SHOW_WITH_ERROR = "rewardedDidFailToShowWithError"
let EVENT_REWARDED_DID_SHOW = "rewardedDidShow"
let EVENT_REWARDED_DID_DISMISS = "rewardedDidDismiss"
let EVENT_REWARDED_DID_CLICK = "rewardedDidClick"
let EVENT_REWARDED_DID_TRACK_IMPRESSION_WITH = "rewardedDidTrackImpressionWith"

// MARK: - YMARewardedAdDelegate\
extension YandexAdsPlugin: YMARewardedAdDelegate {
    func rewardedAd(_ rewardedAd: YMARewardedAd, didReward reward: YMAReward) {
        self.emitWindowEvent(event: EVENT_REWARDED_DID_REWARD)
    }

    func rewardedAd(_ rewardedAd: YMARewardedAd, didFailToShowWithError error: Error) {
        let data = ErrorData(message: error.localizedDescription)
        self.emitWindowEvent(event: EVENT_REWARDED_DID_FAIL_TO_SHOW_WITH_ERROR, data: data)
    }

    func rewardedAdDidShow(_ rewardedAd: YMARewardedAd) {
        self.emitWindowEvent(event: EVENT_REWARDED_DID_SHOW)
    }

    func rewardedAdDidDismiss(_ rewardedAd: YMARewardedAd) {
        self.emitWindowEvent(event: EVENT_REWARDED_DID_DISMISS)
    }

    func rewardedAdDidClick(_ rewardedAd: YMARewardedAd) {
        self.emitWindowEvent(event: EVENT_REWARDED_DID_CLICK)
    }

    func rewardedAd(_ rewardedAd: YMARewardedAd, didTrackImpressionWith impressionData: YMAImpressionData?) {
        self.emitWindowEvent(event: EVENT_REWARDED_DID_TRACK_IMPRESSION_WITH)
    }
}
