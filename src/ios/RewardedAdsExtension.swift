import YandexMobileAds

let EVENT_REWARDED_DID_LOAD = "rewardedDidLoad"
let EVENT_REWARDED_FAILED_TO_LOAD = "rewardedFailedToLoad"

extension YandexAdsPlugin {
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
}

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
