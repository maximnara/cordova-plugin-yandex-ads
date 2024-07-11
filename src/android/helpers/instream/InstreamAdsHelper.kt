package io.luzh.cordova.plugin.helpers.instream

import android.view.ViewGroup
import androidx.core.view.contains
import com.google.android.exoplayer2.ui.PlayerView
import com.yandex.mobile.ads.instream.InstreamAd
import com.yandex.mobile.ads.instream.InstreamAdBinder
import com.yandex.mobile.ads.instream.InstreamAdListener
import com.yandex.mobile.ads.instream.InstreamAdLoadListener
import com.yandex.mobile.ads.instream.InstreamAdLoader
import com.yandex.mobile.ads.instream.InstreamAdRequestConfiguration
import com.yandex.mobile.ads.instream.player.ad.InstreamAdView
import io.luzh.cordova.plugin.helpers.BaseAdsHelper
import io.luzh.cordova.plugin.helpers.instream.ad.SampleInstreamAdPlayer
import io.luzh.cordova.plugin.helpers.instream.content.ContentVideoPlayer
import io.luzh.cordova.plugin.utils.ConstantsEvents
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView

@Suppress("DEPRECATION")
internal class InstreamAdsHelper(
    cordovaPlugin: CordovaPlugin,
    cordovaWebView: CordovaWebView,
    blockId: String,
    private val contentStreamUrl: String
) : BaseAdsHelper<InstreamAdLoader>(cordovaPlugin, cordovaWebView, blockId) {
    private val eventLogger = InstreamAdEventLogger()
    private var instreamAdBinder: InstreamAdBinder? = null
    private var contentVideoPlayer: ContentVideoPlayer? = null
    private var instreamAdPlayer: SampleInstreamAdPlayer? = null
    private var activePlayer: SamplePlayer? = null
    private var playerView: PlayerView? = null
    private var instreamAdView: InstreamAdView? = null
    private var instreamAd: InstreamAd? = null
    private var isLoaded = false

    override fun getLoader() = InstreamAdLoader(cordova.context).apply {
        setInstreamAdLoadListener(eventLogger)
    }

    override fun load(callbackContext: CallbackContext) {
        init()

        val instreamAdLoader = getLoader()
        val configuration = InstreamAdRequestConfiguration.Builder(blockId).build()

        instreamAdLoader.loadInstreamAd(cordova.context, configuration)
        callbackContext.success()
    }

    override fun show(callbackContext: CallbackContext) {
        cordova.activity.runOnUiThread {
            val instreamAd = this.instreamAd ?: return@runOnUiThread

            (cordovaWebView.view as? ViewGroup)?.let { view ->
                instreamAdView?.let {
                    if (!view.contains(it)) view.addView(it)
                }
            }

            instreamAdBinder = InstreamAdBinder(
                cordova.context,
                instreamAd,
                checkNotNull(instreamAdPlayer),
                checkNotNull(contentVideoPlayer)
            ).apply {
                setInstreamAdListener(eventLogger)
                instreamAdView?.let { bind(it) }
            }

            callbackContext.success()
        }
    }

    fun hide(callbackContext: CallbackContext) {
        cordova.activity.runOnUiThread {
            (cordovaWebView.view as? ViewGroup)?.let {
                it.removeView(instreamAdView)
                onDestroy()
            }

            callbackContext.success()
        }
    }

    fun onPause() {
        if (!isLoaded) return

        if (instreamAdPlayer?.isPlaying() == true || contentVideoPlayer?.isPlaying() == true) {
            activePlayer = contentVideoPlayer?.takeIf { it.isPlaying() } ?: instreamAdPlayer
            activePlayer?.pause()
        } else {
            activePlayer = null
        }
    }

    fun onResume() {
        if (!isLoaded) return

        activePlayer?.resume()
    }

    fun onDestroy() {
        instreamAdBinder?.unbind()
        instreamAdBinder?.invalidateAdPlayer()
        instreamAdBinder?.invalidateVideoPlayer()

        contentVideoPlayer?.release()
        instreamAdPlayer?.release()
        activePlayer = null
        contentVideoPlayer = null
        instreamAdPlayer = null
        isLoaded = false
    }

    private fun init() {
        playerView = createPlayerView()
        instreamAdView = createInstreamAdView()
        instreamAdView?.addView(playerView)

        playerView?.let { initPlayer(it) }
    }

    private fun initPlayer(player: PlayerView) {
        cordova.activity.runOnUiThread {
            contentVideoPlayer = ContentVideoPlayer(contentStreamUrl, player)
            instreamAdPlayer = SampleInstreamAdPlayer(player)
        }
    }

    private fun createInstreamAdView(): InstreamAdView {
        return InstreamAdView(cordova.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    private fun createPlayerView(): PlayerView {
        return PlayerView(cordova.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    inner class InstreamAdEventLogger : InstreamAdLoadListener, InstreamAdListener {
        override fun onInstreamAdFailedToLoad(reason: String) {
            emitWindowEvent(ConstantsEvents.EVENT_INSTREAM_FAILED_TO_LOAD, reason)
            isLoaded = false
        }

        override fun onInstreamAdLoaded(instreamAd: InstreamAd) {
            emitWindowEvent(ConstantsEvents.EVENT_INSTREAM_LOADED)
            isLoaded = true
            this@InstreamAdsHelper.instreamAd = instreamAd
        }

        override fun onError(reason: String) {
            emitWindowEvent(ConstantsEvents.EVENT_INSTREAM_ERROR, reason)
        }

        override fun onInstreamAdCompleted() {
            emitWindowEvent(ConstantsEvents.EVENT_INSTREAM_AD_COMPLEATED)
        }

        override fun onInstreamAdPrepared() {
            emitWindowEvent(ConstantsEvents.EVENT_INSTREAM_AD_PREPARED)
        }
    }
}