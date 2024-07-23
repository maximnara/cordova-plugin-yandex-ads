package io.luzh.cordova.plugin.helpers

import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_APP_OPEN_ADS_CLICKED
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_APP_OPEN_ADS_DISMISSED
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_APP_OPEN_ADS_FAILED_TO_LOAD
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_APP_OPEN_ADS_FAILED_TO_SHOW
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_APP_OPEN_ADS_IMPRESSION
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_APP_OPEN_ADS_LOADED
import io.luzh.cordova.plugin.utils.ConstantsEvents.EVENT_APP_OPEN_ADS_SHOWN
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView

internal class OpenAppAdsHelper(
    cordovaPlugin: CordovaPlugin,
    cordovaWebView: CordovaWebView,
    blockId: String
) : BaseAdsHelper<AppOpenAdLoader>(cordovaPlugin, cordovaWebView, blockId) {
    private var mOpenAppAd: AppOpenAd? = null

    override fun getLoader() = AppOpenAdLoader(cordova.context).apply {
        setAdLoadListener(object : AppOpenAdLoadListener {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                mOpenAppAd = appOpenAd

                appOpenAd.setAdEventListener(object : AppOpenAdEventListener {
                    override fun onAdShown() {
                        emitWindowEvent(EVENT_APP_OPEN_ADS_SHOWN)
                    }

                    override fun onAdFailedToShow(adError: AdError) {
                        emitWindowEvent(EVENT_APP_OPEN_ADS_FAILED_TO_SHOW)
                    }

                    override fun onAdDismissed() {
                        emitWindowEvent(EVENT_APP_OPEN_ADS_DISMISSED)
                    }

                    override fun onAdClicked() {
                        emitWindowEvent(EVENT_APP_OPEN_ADS_CLICKED)
                    }

                    override fun onAdImpression(impressionData: ImpressionData?) {
                        emitWindowEvent(EVENT_APP_OPEN_ADS_IMPRESSION)
                    }
                })

                emitWindowEvent(EVENT_APP_OPEN_ADS_LOADED)
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                emitWindowEvent(EVENT_APP_OPEN_ADS_FAILED_TO_LOAD, error.description)
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
        callbackContext.success()
        mOpenAppAd?.show(cordova.activity)
    }
}