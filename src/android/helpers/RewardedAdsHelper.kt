package io.luzh.cordova.plugin.helpers

import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_REWARDED_VIDEO_AD_CLICKED
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_REWARDED_VIDEO_AD_DISMISSED
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_REWARDED_VIDEO_AD_IMPRESSION
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_REWARDED_VIDEO_FAILED_TO_LOAD
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_REWARDED_VIDEO_FAILED_TO_SHOW
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_REWARDED_VIDEO_LOADED
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_REWARDED_VIDEO_REWARDED
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_REWARDED_VIDEO_SHOWN
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView

internal class RewardedAdsHelper(
    cordovaPlugin: CordovaPlugin,
    cordovaWebView: CordovaWebView,
    blockId: String
) : BaseAdsHelper<RewardedAdLoader>(cordovaPlugin, cordovaWebView, blockId) {
    private var mRewardedAd: RewardedAd? = null

    override fun getLoader() = RewardedAdLoader(cordova.context).apply {
        setAdLoadListener(object : RewardedAdLoadListener {
            override fun onAdLoaded(rewarded: RewardedAd) {
                mRewardedAd = rewarded
                rewarded.setAdEventListener(object : RewardedAdEventListener {
                    override fun onRewarded(reward: Reward) {
                        emitWindowEvent(EVENT_REWARDED_VIDEO_REWARDED)
                    }

                    override fun onAdShown() {
                        emitWindowEvent(EVENT_REWARDED_VIDEO_SHOWN)
                    }

                    override fun onAdFailedToShow(adError: AdError) {
                        emitWindowEvent(EVENT_REWARDED_VIDEO_FAILED_TO_SHOW)
                    }

                    override fun onAdDismissed() {
                        emitWindowEvent(EVENT_REWARDED_VIDEO_AD_DISMISSED)
                    }

                    override fun onAdClicked() {
                        emitWindowEvent(EVENT_REWARDED_VIDEO_AD_CLICKED)
                    }

                    override fun onAdImpression(impressionData: ImpressionData?) {
                        emitWindowEvent(EVENT_REWARDED_VIDEO_AD_IMPRESSION)
                    }
                })

                emitWindowEvent(EVENT_REWARDED_VIDEO_LOADED)
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                emitWindowEvent(EVENT_REWARDED_VIDEO_FAILED_TO_LOAD, error.description)
            }
        })
    }

    override fun load(callbackContext: CallbackContext) {
        cordova.getActivity().runOnUiThread(Runnable {
            getLoader().loadAd(AdRequestConfiguration.Builder(blockId).build())
            callbackContext.success()
        })
    }

    override fun show(callbackContext: CallbackContext) {
        cordova.getActivity().runOnUiThread(Runnable {
            mRewardedAd?.show(cordova.activity)
            callbackContext.success()
        })
    }
}