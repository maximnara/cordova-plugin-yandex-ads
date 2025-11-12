package io.luzh.cordova.plugin.helpers.instream

import android.util.Log
import android.view.ViewGroup
import android.view.KeyEvent
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
            val instreamAd = this.instreamAd ?: run {
                callbackContext.error("Instream ad not loaded")
                return@runOnUiThread
            }

            val adPlayer = instreamAdPlayer ?: run {
                callbackContext.error("Instream ad player not initialized")
                return@runOnUiThread
            }

            val videoPlayer = contentVideoPlayer ?: run {
                callbackContext.error("Content video player not initialized")
                return@runOnUiThread
            }

            (cordovaWebView.view as? ViewGroup)?.let { view ->

                view.isFocusable = false

                instreamAdView?.let { adView ->
                    if (!view.contains(adView)) {
                        view.addView(adView)
                        adView.requestFocus()
                    }
                }
            }

            instreamAdBinder = InstreamAdBinder(
                cordova.context,
                instreamAd,
                adPlayer,
                videoPlayer
            ).apply {
                setInstreamAdListener(eventLogger)
                instreamAdView?.let {
                    bind(it)
                }
            }

            callbackContext.success()
        }
    }

    /**
     * Обработчик D-pad событий, вызывается из Activity
     */
    fun handleDpadEvent(keyCode: Int, event: KeyEvent): Boolean {
//        if (!isInstreamAdShowing) return false

        Log.d("InstreamAdsHelper", "handleDpadEvent: keyCode=$keyCode, action=${event.action}")

        // Передаем событие напрямую в view с фокусом
        instreamAdView?.let { adView ->
            // При навигационных кнопках перемещаем фокус в нужном направлении
            if (event.action == KeyEvent.ACTION_DOWN) {
                val direction = when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> android.view.View.FOCUS_LEFT
                    KeyEvent.KEYCODE_DPAD_RIGHT -> android.view.View.FOCUS_RIGHT
                    KeyEvent.KEYCODE_DPAD_UP -> android.view.View.FOCUS_UP
                    KeyEvent.KEYCODE_DPAD_DOWN -> android.view.View.FOCUS_DOWN
                    else -> null
                }

                direction?.let { dir ->
                    val currentFocus = adView.findFocus()
                    val nextFocus = currentFocus?.focusSearch(dir)
                    Log.d("InstreamAdsHelper", "Focus search: direction=$dir, current=${currentFocus?.javaClass?.simpleName}, next=${nextFocus?.javaClass?.simpleName}")

                    nextFocus?.requestFocus()?.let { focused ->
                        Log.d("InstreamAdsHelper", "Focus moved: $focused")
                        return focused
                    }
                }
            }

            // Диспатчим событие синхронно, чтобы получить реальный результат
            val result = adView.dispatchKeyEvent(event)
            Log.d("InstreamAdsHelper", "dispatchKeyEvent to InstreamAdView: result=$result")

            return result
        }

        return false
    }

    fun hide(callbackContext: CallbackContext) {
        cordova.activity.runOnUiThread {
            (cordovaWebView.view as? ViewGroup)?.let { view ->
                view.removeView(instreamAdView)

                view.isFocusable = true
                view.requestFocus()

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
            isFocusable = true
            descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
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