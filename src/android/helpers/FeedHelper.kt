package io.luzh.cordova.plugin.helpers

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.feed.FeedAd
import com.yandex.mobile.ads.feed.FeedAdAdapter
import com.yandex.mobile.ads.feed.FeedAdAppearance
import com.yandex.mobile.ads.feed.FeedAdEventListener
import com.yandex.mobile.ads.feed.FeedAdLoadListener
import com.yandex.mobile.ads.feed.FeedAdRequestConfiguration
import io.luzh.cordova.plugin.utils.ConstantsEvents
import io.luzh.cordova.plugin.utils.ScreenUtil.screenWidth
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView

internal class FeedHelper(
    cordovaPlugin: CordovaPlugin,
    cordovaWebView: CordovaWebView,
    blockId: String
): BaseAdsHelper<Unit>(cordovaPlugin, cordovaWebView, blockId) {
    private var feedAd: FeedAd? = null
    private var feedAdAdapter: FeedAdAdapter? = null

    override fun getLoader() = null
    override fun load(callbackContext: CallbackContext) {
        val calculatedFeedCardWidth = calculateFeedCardWidth()
        val feedAdAppearance = FeedAdAppearance(
            cardWidth = calculatedFeedCardWidth,
            cardCornerRadius = CARD_CORNER_RADIUS_DP
        )
        val feedAdRequestConfiguration = FeedAdRequestConfiguration.Builder(blockId).build()

        feedAd = FeedAd.Builder(cordova.context, feedAdRequestConfiguration, feedAdAppearance).build()
        feedAd?.loadListener = getAdLoadListener()
        feedAd?.preloadAd()

        cordova.activity.runOnUiThread {
            callbackContext.success()
        }
    }

    override fun show(callbackContext: CallbackContext) {
        feedAdAdapter = feedAd?.let { FeedAdAdapter(it) }
        feedAdAdapter?.eventListener = getAdEventListener()

        (cordovaWebView.view as? ViewGroup)?.addView(feedAdAdapter?.let { getRecyclerView(it) })
        cordova.activity.runOnUiThread {
            callbackContext.success()
        }
    }

    private fun getAdLoadListener() = object: FeedAdLoadListener {
        override fun onAdFailedToLoad(error: AdRequestError) {
            emitWindowEvent(ConstantsEvents.EVENT_FEED_FAILED_TO_LOAD, error.description)
        }

        override fun onAdLoaded() {
            emitWindowEvent(ConstantsEvents.EVENT_FEED_LOADED)
        }
    }

    private fun getAdEventListener() = object: FeedAdEventListener {
        override fun onAdClicked() {
            emitWindowEvent(ConstantsEvents.EVENT_FEED_CLICKED)
        }

        override fun onImpression(impressionData: ImpressionData?) {
            emitWindowEvent(ConstantsEvents.EVENT_FEED_IMPRESSION)
        }
    }

    private fun calculateFeedCardWidth(): Int {
        return cordova.context.screenWidth - 2 * CARD_WIDTH_MARGIN_DP
    }

    private fun getRecyclerView(adapter: FeedAdAdapter): RecyclerView {
        return RecyclerView(cordova.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
    }

    private companion object {
        private const val CARD_WIDTH_MARGIN_DP = 24
        private const val CARD_CORNER_RADIUS_DP = 14.0
        private const val SCREEN_CONTENT_ITEMS_COUNT = 15
    }
}