package io.luzh.cordova.plugin.helpers

import android.R
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.view.contains
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import io.luzh.cordova.plugin.utils.ConstantsEvents
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView
import org.json.JSONObject


internal class BannerAdsHelper(
    cordovaPlugin: CordovaPlugin,
    cordovaWebView: CordovaWebView,
    blockId: String,
    val bannerAtTop: Boolean,
    val bannerSize: JSONObject?
) : BaseAdsHelper<Unit>(cordovaPlugin, cordovaWebView, blockId) {
    private var bannerContainerLayout: RelativeLayout? = null
    private var bannerParrentLayout: RelativeLayout? = null
    private var bannerLoaded: Boolean = false
    private var bannerShown: Boolean = false
    private var mBannerAdView: BannerAdView? = null
    private var inlineRoot: LinearLayout? = null
    private var inlineWebViewHost: FrameLayout? = null
    private var originalWebViewParent: ViewGroup? = null
    private var originalWebViewParams: ViewGroup.LayoutParams? = null
    private var originalWebViewIndex = 0

    override fun getLoader() = null

    override fun show(callbackContext: CallbackContext) {
        cordova.activity.runOnUiThread {
            // cant show or already shown
            if (mBannerAdView == null || !bannerLoaded || bannerShown) {
                callbackContext.success()
                return@runOnUiThread
            }

            bannerShown = true

            if (bannerSize == null) {
                val bannerLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    val alignRule = if (bannerAtTop) RelativeLayout.ALIGN_PARENT_TOP
                    else RelativeLayout.ALIGN_PARENT_BOTTOM

                    addRule(alignRule)
                }

                bannerParrentLayout = RelativeLayout(cordova.activity)
                val bannerParrentLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )

                try {
                    (cordovaWebView.view.parent as? ViewGroup)?.addView(bannerParrentLayout, bannerParrentLayoutParams)
                } catch (e: java.lang.Exception) {
                    (cordovaWebView as ViewGroup).addView(bannerParrentLayout, bannerParrentLayoutParams)
                }

                bannerParrentLayout?.addView(mBannerAdView, bannerLayoutParams)
                bannerParrentLayout?.bringToFront()
            } else {
                val view = cordovaWebView.view
                val wvParentView = view.parent as? ViewGroup
                val parentView = LinearLayout(cordovaWebView.context)
                inlineRoot = parentView

                // if we have a parent of this element,
                // we remove it from there and put it in the linerLayout
                val rootView: ViewGroup? = if (wvParentView != null) {
                    originalWebViewParent = wvParentView
                    originalWebViewIndex = wvParentView.indexOfChild(view)
                    originalWebViewParams = FrameLayout.LayoutParams(view.layoutParams as FrameLayout.LayoutParams)
                    wvParentView.removeView(view)
                    parentView.orientation = LinearLayout.VERTICAL
                    parentView.layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // Cordova 15's root insets listener retains the WebView reference
                    // and expects FrameLayout.LayoutParams even after it is reparented.
                    val webViewHost = FrameLayout(cordovaWebView.context)
                    inlineWebViewHost = webViewHost
                    webViewHost.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0, 1.0f
                    )
                    webViewHost.addView(view, FrameLayout.LayoutParams(originalWebViewParams as FrameLayout.LayoutParams))
                    parentView.addView(webViewHost)
                    wvParentView
                } else {
                    view as? ViewGroup
                }
                rootView?.addView(parentView, originalWebViewIndex)

                bannerContainerLayout = RelativeLayout(cordovaPlugin.cordova.activity)

                bannerContainerLayout?.gravity = Gravity.BOTTOM
                bannerContainerLayout?.setBackgroundColor(0x000000)

                val layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )

                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)

                bannerContainerLayout?.addView(mBannerAdView, layoutParams)

                mBannerAdView?.layoutParams = layoutParams

                // detect banner size
                val vto = mBannerAdView?.viewTreeObserver
                vto?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mBannerAdView?.viewTreeObserver?.removeOnGlobalLayoutListener(
                            this
                        )

                        mBannerAdView?.measuredHeight?.let {
                            bannerContainerLayout?.minimumHeight = it
                        }
                    }
                })

                // show banner
                if (bannerAtTop) {
                    parentView.addView(bannerContainerLayout, 0)
                } else {
                    parentView.addView(bannerContainerLayout)
                }

                val contentView = cordova.activity.findViewById<ViewGroup>(R.id.content)
                if (contentView != null) {
                    contentView.bringToFront()
                    contentView.requestLayout()
                    contentView.requestFocus()
                }
            }

            callbackContext.success()
        }
    }

    override fun load(callbackContext: CallbackContext) {
        cordova.getActivity().runOnUiThread(Runnable {
            hideBannerView()
            mBannerAdView = BannerAdView(cordova.activity)

            // determine the size of the advertising banner
            val adSize =
                if (bannerSize != null && bannerSize.has("width") && bannerSize.has("height")) {
                    BannerAdSize.inline(
                        cordova.context,
                        bannerSize.optInt("width"),
                        bannerSize.optInt("height")
                    )
                } else {
                    val adWidth = cordovaWebView.view.width
                    BannerAdSize.sticky(cordova.context, adWidth)
                }

            // set the banner size
            mBannerAdView?.setAdSize(adSize)

            bannerShown = false

            val adRequest: AdRequest = AdRequest.Builder(blockId).build()

            mBannerAdView?.setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {
                    bannerLoaded = true
                    emitWindowEvent(ConstantsEvents.EVENT_BANNER_DID_LOAD)
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    emitWindowEvent(ConstantsEvents.EVENT_BANNER_FAILED_TO_LOAD, error.description)
                }

                override fun onAdClicked() {
                    emitWindowEvent(ConstantsEvents.EVENT_BANNER_DID_CLICK)
                }

                override fun onImpression(impressionData: ImpressionData?) {
                    emitWindowEvent(ConstantsEvents.EVENT_BANNER_IMPRESSION)
                }
            })

            mBannerAdView?.loadAd(adRequest)
            callbackContext.success()
        })
    }

    /**
     * Destroys Yandex Ads Banner and removes it from the container
     */
    fun hide(callbackContext: CallbackContext) {
        cordova.getActivity().runOnUiThread(Runnable {
            hideBannerView()
            callbackContext.success()
        })
    }

    fun reload(callbackContext: CallbackContext) {
        cordova.activity.runOnUiThread {
            if (mBannerAdView != null && bannerShown) {
                if (getParentLayout() != null && bannerContainerLayout != null) {

                    (mBannerAdView?.parent as? ViewGroup)?.let {
                        it.removeView(mBannerAdView)
                    }

                    if (bannerParrentLayout != null) {
                        mBannerAdView?.let {
                            if(bannerParrentLayout?.contains(it) == true){
                                bannerParrentLayout?.removeView(it)
                            }
                        }
                    }

                    log("+++ AFTER REMOVE " + bannerContainerLayout?.childCount)
                }
                destroyBanner()
            }
            mBannerAdView = BannerAdView(cordovaPlugin.cordova.activity)

            val adSize =
                if (bannerSize != null && bannerSize.has("width") && bannerSize.has("height")) {
                    BannerAdSize.inline(
                        cordovaPlugin.cordova.context,
                        bannerSize.optInt("width"),
                        bannerSize.optInt("height")
                    )
                } else {
                    val adWidth = cordovaWebView.view.width
                    BannerAdSize.sticky(cordovaPlugin.cordova.context, adWidth)
                }

            mBannerAdView?.let { it.setAdSize(adSize) }

            bannerShown = false

            val adRequest: AdRequest = AdRequest.Builder(blockId).build()

            mBannerAdView?.setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {
                    bannerLoaded = true
                    log(ConstantsEvents.EVENT_BANNER_DID_LOAD)

                    if (bannerSize == null) {
                        val alignRule = if (bannerAtTop) RelativeLayout.ALIGN_PARENT_TOP
                        else RelativeLayout.ALIGN_PARENT_BOTTOM

                        val bannerParrentParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        ).apply { addRule(alignRule) }

                        bannerParrentLayout?.addView(mBannerAdView, bannerParrentParams)
                        bannerParrentLayout?.bringToFront()
                    } else {
                        val layoutParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        )

                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL)

                        bannerContainerLayout?.addView(mBannerAdView, layoutParams)

                        mBannerAdView?.layoutParams = layoutParams
                    }

                    bannerShown = true
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    emitWindowEvent(ConstantsEvents.EVENT_BANNER_FAILED_TO_LOAD, error.description)
                }

                override fun onAdClicked() {
                    emitWindowEvent(ConstantsEvents.EVENT_BANNER_DID_CLICK)
                }

                override fun onImpression(impressionData: ImpressionData?) {
                    emitWindowEvent(ConstantsEvents.EVENT_BANNER_IMPRESSION)
                }
            })

            if (bannerSize != null) {
                val vto = mBannerAdView?.viewTreeObserver
                vto?.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mBannerAdView?.viewTreeObserver?.removeGlobalOnLayoutListener(
                                this
                            )
                        } else {
                            mBannerAdView?.viewTreeObserver?.removeOnGlobalLayoutListener(
                                this
                            )
                        }

                        mBannerAdView?.measuredHeight?.let {
                            bannerContainerLayout?.minimumHeight = it
                        }
                    }
                })
            }

            mBannerAdView?.loadAd(adRequest)
            callbackContext.success()
        }
    }

    private fun hideBannerView() {
        cordova.getActivity().runOnUiThread(Runnable {
            destroyBanner()
            bannerParrentLayout?.let { (it.parent as? ViewGroup)?.removeView(it) }
            inlineRoot?.let { root ->
                val view = cordovaWebView.view
                inlineWebViewHost?.removeView(view)
                (root.parent as? ViewGroup)?.removeView(root)
                originalWebViewParent?.addView(view, originalWebViewIndex, originalWebViewParams)
            }
            bannerParrentLayout = null
            bannerContainerLayout = null
            inlineRoot = null
            inlineWebViewHost = null
            originalWebViewParent = null
            originalWebViewParams = null
            bannerLoaded = false
            bannerShown = false
        })
    }

    /**
     * Destory Banner
     */
    private fun destroyBanner() {
        mBannerAdView?.let {
            try {
                (it.parent as? ViewGroup)?.removeView(it)
                it.destroy()
            } catch (e: Exception) {
                log("Exception while destroying banner, seems too many requests")
            }
        }
        mBannerAdView = null
    }

    private fun getParentLayout() = cordovaWebView.view?.parent as? ViewGroup
}
