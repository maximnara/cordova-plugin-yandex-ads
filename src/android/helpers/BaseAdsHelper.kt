package io.luzh.cordova.plugin.helpers

import android.util.Log
import io.luzh.cordova.plugin.utils.Constants
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView
import org.json.JSONObject

internal abstract class BaseAdsHelper<T>(
    protected val cordovaPlugin: CordovaPlugin,
    protected val cordovaWebView: CordovaWebView,
    protected val blockId: String
) {
    protected val cordova = cordovaPlugin.cordova

    protected abstract fun getLoader(): T?
    abstract fun load(callbackContext: CallbackContext)
    abstract fun show(callbackContext: CallbackContext)

    fun emitWindowEvent(event: String, logEventDescription: String? = null) {
        log(event, logEventDescription)
        val payload = logEventDescription?.let {
            ", " + JSONObject().put("message", it).toString()
        } ?: ""

        cordovaPlugin.cordova.getActivity().runOnUiThread(Runnable {
            cordovaWebView.loadUrl(
                "javascript:cordova.fireWindowEvent(${JSONObject.quote(event)}$payload);"
            )
        })
    }

    fun log(event: String, logEventDescription: String? = null) {
        val logEvent =
            if (logEventDescription != null) event + ": " + logEventDescription else event
        Log.d(Constants.YANDEX_ADS_TAG, logEvent)
    }

}
