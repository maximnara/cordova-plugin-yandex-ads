package io.luzh.cordova.plugin.helpers.instream

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.KeyEvent
import android.widget.FrameLayout
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
    private var focusMaintenanceRunnable: Runnable? = null
    private var keyInterceptor: KeyInterceptorFrameLayout? = null

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

                instreamAdView?.let { adView ->
                    if (!view.contains(adView)) {
                        // Создаем wrapper для перехвата D-pad событий
                        keyInterceptor = KeyInterceptorFrameLayout(cordova.context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            isFocusable = true
                            isFocusableInTouchMode = false
                            descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS

                            // Добавляем InstreamAdView в wrapper
                            addView(adView)
                        }

                        // Добавляем wrapper в WebView
                        view.addView(keyInterceptor)

                        // Переопределяем focusSearch на родительском ViewGroup
                        // чтобы все запросы фокуса перенаправлялись в InstreamAdView
                        setupFocusRedirect(view, adView)
                    }
                }
            }

            instreamAdBinder = InstreamAdBinder(
                cordova.context,
                instreamAd,
                checkNotNull(instreamAdPlayer),
                checkNotNull(contentVideoPlayer)
            ).apply {
                setInstreamAdListener(eventLogger)
                instreamAdView?.let { adView ->
                    bind(adView)

                    // Запрашиваем фокус ПОСЛЕ bind - когда SDK создаст свои UI элементы
                    (cordovaWebView.view as? ViewGroup)?.let { view ->
                        adView.postDelayed({
                            Log.d("InstreamAdsHelper", "Requesting focus for InstreamAdView AFTER bind, isAttachedToWindow=${adView.isAttachedToWindow}, visibility=${adView.visibility}")

                            // Обходим все дочерние view и ищем focusable элементы
                            fun findFocusableView(viewGroup: ViewGroup): View? {
                                for (i in 0 until viewGroup.childCount) {
                                    val child = viewGroup.getChildAt(i)
                                    if (child.isFocusable && child.visibility == View.VISIBLE) {
                                        Log.d("InstreamAdsHelper", "Found focusable child manually: ${child.javaClass.simpleName}")
                                        return child
                                    }
                                    if (child is ViewGroup) {
                                        val found = findFocusableView(child)
                                        if (found != null) return found
                                    }
                                }
                                return null
                            }

                            val focusable = findFocusableView(adView)
                            Log.d("InstreamAdsHelper", "Manual search result: ${focusable?.javaClass?.simpleName}, isFocusable=${focusable?.isFocusable}")

                            if (focusable != null) {
                                // Собираем всю иерархию parent view
                                val parents = mutableListOf<ViewGroup>()
                                var parent = focusable.parent
                                while (parent != null) {
                                    if (parent is ViewGroup) {
                                        Log.d("InstreamAdsHelper", "Parent: ${parent.javaClass.simpleName}, isFocusable=${parent.isFocusable}, descendantFocusability=${parent.descendantFocusability}")
                                        parents.add(parent)
                                    }
                                    parent = parent.parent
                                }

                                // Настраиваем кнопку для TV
                                focusable.isFocusableInTouchMode = false
                                Log.d("InstreamAdsHelper", "Button before focus: isFocusable=${focusable.isFocusable}, isFocusableInTouchMode=${focusable.isFocusableInTouchMode}, isClickable=${focusable.isClickable}")

                                // Делаем все parent view focusable и устанавливаем правильный descendantFocusability
                                parents.forEach { p ->
                                    if (p.javaClass.simpleName != "SystemWebView") {
                                        p.isFocusable = true
                                        p.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
                                    }
                                }

                                // Выходим из touch mode для TV
                                val decorView = cordova.activity.window.decorView
                                decorView.requestFocusFromTouch()
                                Log.d("InstreamAdsHelper", "Requested exit from touch mode, isInTouchMode=${decorView.isInTouchMode}")

                                // Добавляем OnKeyListener для отладки
                                focusable.setOnKeyListener { v, keyCode, event ->
                                    if (event.action == KeyEvent.ACTION_DOWN) {
                                        Log.d("InstreamAdsHelper", "Button received key: keyCode=$keyCode, event=$event, hasFocus=${v.hasFocus()}")
                                    }
                                    false // Пропускаем событие дальше
                                }

                                val focusResult = focusable.requestFocus()

                                Log.d("InstreamAdsHelper", "Focus requested, result=$focusResult, hasFocus=${focusable.hasFocus()}, findFocus=${decorView.findFocus()?.javaClass?.simpleName}, isInTouchMode=${decorView.isInTouchMode}")

                                // Принудительно вызываем отрисовку фокуса
                                focusable.post {
                                    focusable.requestFocus()
                                    focusable.invalidate()
                                    Log.d("InstreamAdsHelper", "After post: hasFocus=${focusable.hasFocus()}, isFocused=${focusable.isFocused}")
                                }

                                // НЕ восстанавливаем обратно - оставляем focusable=true для работы D-pad навигации
                                // Только SystemWebView остается false
                            } else {
                                Log.d("InstreamAdsHelper", "No focusable child found")
                            }
                        }, 500)
                    }
                }
            }

            callbackContext.success()
        }
    }

    /**
     * Настраивает блокировку фокуса на WebView
     * чтобы фокус оставался в InstreamAdView
     */
    private fun setupFocusRedirect(parentView: ViewGroup, adView: InstreamAdView) {
        Log.d("InstreamAdsHelper", "Setting up focus redirect")

        // Полностью отключаем возможность parent (SystemWebView) получать фокус
        // чтобы он не перехватывал фокус у InstreamAdView
        parentView.isFocusable = false
        parentView.isFocusableInTouchMode = false
        parentView.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS

        Log.d("InstreamAdsHelper", "SystemWebView focus completely disabled")
        Log.d("InstreamAdsHelper", "Focus redirect setup complete")
    }

    fun hide(callbackContext: CallbackContext) {
        cordova.activity.runOnUiThread {
            (cordovaWebView.view as? ViewGroup)?.let { view ->
                // Удаляем wrapper, который содержит instreamAdView
                view.removeView(keyInterceptor)

                // Восстанавливаем фокус для WebView
                cordovaWebView.view.isFocusable = true
                cordovaWebView.view.isFocusableInTouchMode = true

                // Восстанавливаем фокус для дочерних WebView
                for (i in 0 until view.childCount) {
                    val child = view.getChildAt(i)
                    if (child is android.webkit.WebView) {
                        child.isFocusable = true
                        child.isFocusableInTouchMode = true
                    }
                }

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
        val adView = InstreamAdView(cordova.context).apply {
            // Генерируем уникальный ID для view
            id = android.view.View.generateViewId()

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Настройки для получения фокуса на TV
            isFocusable = true
            isFocusableInTouchMode = false
            isClickable = true
            descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS

            // Добавляем OnFocusChangeListener для логирования
            setOnFocusChangeListener { view, hasFocus ->
                Log.d("InstreamAdsHelper", "InstreamAdView.onFocusChange() hasFocus=$hasFocus, id=${view.id}, findFocus=${view.findFocus()?.javaClass?.simpleName}")
            }

            // Перехватываем D-pad события на уровне InstreamAdView
            setOnKeyListener { v, keyCode, event ->
                Log.d("InstreamAdsHelper", "InstreamAdView received key: keyCode=$keyCode, action=${event.action}")

                // Передаем события дальше в SDK (не блокируем)
                false
            }

            // Переопределяем dispatchKeyEvent для перехвата D-pad на более низком уровне
            setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
                override fun onChildViewAdded(parent: View?, child: View?) {
                    Log.d("InstreamAdsHelper", "Child added to InstreamAdView: ${child?.javaClass?.simpleName}")
                }

                override fun onChildViewRemoved(parent: View?, child: View?) {
                    Log.d("InstreamAdsHelper", "Child removed from InstreamAdView: ${child?.javaClass?.simpleName}")
                }
            })

            Log.d("InstreamAdsHelper", "InstreamAdView created: id=$id, isFocusable=$isFocusable, isClickable=$isClickable")
        }

        return adView
    }

    private fun createPlayerView(): PlayerView {
        return PlayerView(cordova.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    /**
     * Wrapper для перехвата D-pad событий
     */
    inner class KeyInterceptorFrameLayout(context: Context) : FrameLayout(context) {
        override fun dispatchKeyEvent(event: KeyEvent): Boolean {
            Log.d("InstreamAdsHelper", "KeyInterceptor.dispatchKeyEvent: keyCode=${event.keyCode}, action=${event.action}")

            // Передаем событие дальше
            return super.dispatchKeyEvent(event)
        }

        override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
            Log.d("InstreamAdsHelper", "KeyInterceptor.onKeyDown: keyCode=$keyCode")
            return super.onKeyDown(keyCode, event)
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