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

        let request = AdRequest(adUnitID: self.rewardedBlockId!)
        self.rewardedAd = nil
        self.rewardedAdLoader.loadAd(with: request) { [weak self] result in
            guard let self = self else { return }
            switch result {
            case .success(let ad):
                self.rewardedAd = ad
                ad.delegate = self
                self.emitWindowEvent(event: EVENT_REWARDED_DID_LOAD)
            case .failure(let error):
                let data = ErrorData(id: request.adUnitID, message: error.localizedDescription)
                self.emitWindowEvent(event: EVENT_REWARDED_FAILED_TO_LOAD, data: data)
            }
        }

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

let EVENT_REWARDED_DID_REWARD = "rewardedDidReward"
let EVENT_REWARDED_DID_FAIL_TO_SHOW_WITH_ERROR = "rewardedDidFailToShowWithError"
let EVENT_REWARDED_DID_SHOW = "rewardedDidShow"
let EVENT_REWARDED_DID_DISMISS = "rewardedDidDismiss"
let EVENT_REWARDED_DID_CLICK = "rewardedDidClick"
let EVENT_REWARDED_DID_TRACK_IMPRESSION_WITH = "rewardedDidTrackImpressionWith"

// MARK: - RewardedAdDelegate
extension YandexAdsPlugin: RewardedAdDelegate {
    func rewardedAd(_ rewardedAd: RewardedAd, didReward reward: Reward) {
        self.emitWindowEvent(event: EVENT_REWARDED_DID_REWARD)
    }

    func rewardedAd(_ rewardedAd: RewardedAd, didFailToShow error: Error) {
        let data = ErrorData(message: error.localizedDescription)
        self.emitWindowEvent(event: EVENT_REWARDED_DID_FAIL_TO_SHOW_WITH_ERROR, data: data)
    }

    func rewardedAdDidShow(_ rewardedAd: RewardedAd) {
        self.emitWindowEvent(event: EVENT_REWARDED_DID_SHOW)
    }

    func rewardedAdDidDismiss(_ rewardedAd: RewardedAd) {
        self.rewardedAd?.delegate = nil
        self.rewardedAd = nil

        self.emitWindowEvent(event: EVENT_REWARDED_DID_DISMISS)
    }

    func rewardedAdDidClick(_ rewardedAd: RewardedAd) {
        self.emitWindowEvent(event: EVENT_REWARDED_DID_CLICK)
    }

    func rewardedAd(_ rewardedAd: RewardedAd, didTrackImpression impressionData: ImpressionData?) {
        self.emitWindowEvent(event: EVENT_REWARDED_DID_TRACK_IMPRESSION_WITH)
    }
}
