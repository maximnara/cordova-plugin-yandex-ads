package io.luzh.cordova.plugin.helpers

import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_INTERSTITIAL_AD_CLICKED
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_INTERSTITIAL_AD_DISMISSED
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_INTERSTITIAL_AD_IMPRESSION
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_INTERSTITIAL_FAILED_TO_LOAD
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_INTERSTITIAL_FAILED_TO_SHOW
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_INTERSTITIAL_LOADED
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_INTERSTITIAL_SHOWN
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView

internal class InterstitialAdsHelper(
    cordovaPlugin: CordovaPlugin,
    cordovaWebView: CordovaWebView,
    blockId: String
) : BaseAdsHelper<InterstitialAdLoader>(cordovaPlugin, cordovaWebView, blockId) {
    private var mInterstitialAd: InterstitialAd? = null

    override fun getLoader() = InterstitialAdLoader(cordova.context).apply {
        setAdLoadListener(object : InterstitialAdLoadListener {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd

                interstitialAd.setAdEventListener(object : InterstitialAdEventListener {
                    override fun onAdShown() {
                        emitWindowEvent(EVENT_INTERSTITIAL_SHOWN)
                    }

                    override fun onAdFailedToShow(adError: AdError) {
                        emitWindowEvent(EVENT_INTERSTITIAL_FAILED_TO_SHOW)
                    }

                    override fun onAdDismissed() {
                        emitWindowEvent(EVENT_INTERSTITIAL_AD_DISMISSED)
                    }

                    override fun onAdClicked() {
                        emitWindowEvent(EVENT_INTERSTITIAL_AD_CLICKED)
                    }

                    override fun onAdImpression(impressionData: ImpressionData?) {
                        emitWindowEvent(EVENT_INTERSTITIAL_AD_IMPRESSION)
                    }
                })

                emitWindowEvent(EVENT_INTERSTITIAL_LOADED)
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                emitWindowEvent(EVENT_INTERSTITIAL_FAILED_TO_LOAD, error.description)
            }
        })
    }

    override fun load(callbackContext: CallbackContext) {
        cordova.activity.runOnUiThread {
            getLoader().loadAd(AdRequestConfiguration.Builder(blockId).build())
            callbackContext.success()
        }
    }

    override fun show(callbackContext: CallbackContext) {
        cordova.activity.runOnUiThread {
            mInterstitialAd?.show(cordova.activity)
            callbackContext.success()
        }
    }
}