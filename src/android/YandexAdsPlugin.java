package io.luzh.cordova.plugin;

import android.util.Log;
import android.text.TextUtils;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.InitializationListener;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.rewarded.RewardedAd;
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener;
import com.yandex.mobile.ads.rewarded.Reward;

import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;

import android.util.Log;

import androidx.annotation.Nullable;

public class YandexAdsPlugin extends CordovaPlugin {

    private static final String TAG = "YANDEX_ADS";

    private static final String EVENT_INTERSTITIAL_LOADED = "interstitialLoaded";
    private static final String EVENT_INTERSTITIAL_FAILED_TO_LOAD = "interstitialFailedToLoad";
    private static final String EVENT_INTERSTITIAL_SHOWN = "interstitialShown";
    private static final String EVENT_INTERSTITIAL_CLOSED = "interstitialClosed";

    private static final String EVENT_REWARDED_VIDEO_LOADED = "rewardedVideoLoaded";
    private static final String EVENT_REWARDED_VIDEO_REWARDED = "rewardedVideoRewardReceived";
    private static final String EVENT_REWARDED_VIDEO_FAILED = "rewardedVideoFailed";
    private static final String EVENT_REWARDED_VIDEO_STARTED = "rewardedVideoStarted";
    private static final String EVENT_REWARDED_VIDEO_CLOSED = "rewardedVideoClosed";

    private static final String EVENT_BANNER_DID_LOAD = "bannerDidLoad";
    private static final String EVENT_BANNER_FAILED_TO_LOAD = "bannerFailedToLoad";
    private static final String EVENT_BANNER_DID_CLICK = "bannerDidClick";
    private static final String EVENT_BANNER_WILL_PRESENT_SCREEN = "bannerWillPresentScreen";
    private static final String EVENT_BANNER_DID_DISMISS_SCREEN = "bannerDidDismissScreen";
    private static final String EVENT_BANNER_WILL_LEAVE_APPLICATION = "bannerWillLeaveApplication";

    private RelativeLayout bannerContainerLayout;
    private boolean bannerLoaded;
    private boolean bannerShowing;
    private CordovaWebView cordovaWebView;
    private ViewGroup parentLayout;

    private RewardedAd mRewardedAd;
    private InterstitialAd mInterstitialAd;
    private String rewardedBlockId;
    private String interstitialBlockId;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        if (action.equals("init")) {
            this.initAction(args, callbackContext);
            return true;
        }

        else if (action.equals("loadRewardedVideo")) {
            this.loadRewardedAction(args, callbackContext);
            return true;
        }

        else if (action.equals("showRewardedVideo")) {
            this.showRewardedVideoAction(args, callbackContext);
            return true;
        }

        else if (action.equals("loadBanner")) {
            this.loadBannerAction(args, callbackContext);
            return true;
        }

        else if (action.equals("showBanner")) {
            this.showBannerAction(args, callbackContext);
            return true;
        }

        else if (action.equals("hideBanner")) {
            this.hideBannerAction(args, callbackContext);
            return true;
        }

        else if (action.equals("loadInterstitial")) {
            this.loadInterstitialAction(args, callbackContext);
            return true;
        }

        else if (action.equals("showInterstitial")) {
            this.showInterstitialAction(args, callbackContext);
            return true;
        }

        return false;
    }

    /** --------------------------------------------------------------- */

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        cordovaWebView = webView;
        super.initialize(cordova, webView);
    }

    /** ----------------------- UTILS --------------------------- */

    private void emitWindowEvent(final String event) {
        final CordovaWebView view = this.webView;
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(String.format("javascript:cordova.fireWindowEvent('%s');", event));
            }
        });
    }

    private void emitWindowEvent(final String event, final JSONObject data) {
        final CordovaWebView view = this.webView;
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.loadUrl(String.format("javascript:cordova.fireWindowEvent('%s', %s);", event, data.toString()));
            }
        });
    }

    /** ----------------------- INITIALIZATION --------------------------- */

    /**
     * Intilization action Initializes Yandex Ads
     */
    private void initAction(JSONArray args, final CallbackContext callbackContext) throws JSONException {

        final YandexAdsPlugin self = this;
        rewardedBlockId = args.getString(0);
        interstitialBlockId = args.getString(1);

        MobileAds.initialize(this.cordova.getActivity(), new InitializationListener() {
            @Override
            public void onInitializationCompleted() {
                callbackContext.success();
            }
        });
    }

    /**
     * Initializes Rewarded ads
     *
     * @todo Provide
     */
    private void initRewarded(String blockId) {

        final YandexAdsPlugin self = this;
        mRewardedAd = new RewardedAd(this.cordova.getActivity());
        mRewardedAd.setAdUnitId(blockId);

        mRewardedAd.setRewardedAdEventListener(new RewardedAdEventListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, EVENT_REWARDED_VIDEO_LOADED);
                self.emitWindowEvent(EVENT_REWARDED_VIDEO_LOADED);
            }

            @Override
            public void onRewarded(final Reward reward) {
                Log.d(TAG, EVENT_REWARDED_VIDEO_REWARDED);
                self.emitWindowEvent(EVENT_REWARDED_VIDEO_REWARDED);
            }

            @Override
            public void onAdFailedToLoad(final AdRequestError adRequestError) {
                Log.d(TAG, EVENT_REWARDED_VIDEO_FAILED + ": " + adRequestError.getDescription());
                self.emitWindowEvent(EVENT_REWARDED_VIDEO_FAILED);
            }

            @Override
            public void onAdShown() {
                Log.d(TAG, EVENT_REWARDED_VIDEO_STARTED);
                self.emitWindowEvent(EVENT_REWARDED_VIDEO_STARTED);
            }

            @Override
            public void onAdDismissed() {
                Log.d(TAG, EVENT_REWARDED_VIDEO_CLOSED);
                self.emitWindowEvent(EVENT_REWARDED_VIDEO_CLOSED);
            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void onImpression(@Nullable ImpressionData var1) {

            }

            @Override
            public void onLeftApplication() {

            }

            @Override
            public void onReturnedToApplication() {

            }
        });
    }

    private void initInterstitial(String blockId) {

        final YandexAdsPlugin self = this;
        mInterstitialAd = new InterstitialAd(this.cordova.getActivity());
        mInterstitialAd.setAdUnitId(blockId);

        mInterstitialAd.setInterstitialAdEventListener(new InterstitialAdEventListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, EVENT_INTERSTITIAL_LOADED);
                self.emitWindowEvent(EVENT_INTERSTITIAL_LOADED);
            }

            @Override
            public void onAdFailedToLoad(AdRequestError adRequestError) {
                Log.d(TAG, EVENT_INTERSTITIAL_FAILED_TO_LOAD + ": " + adRequestError.getDescription());
                self.emitWindowEvent(EVENT_INTERSTITIAL_FAILED_TO_LOAD);
            }

            @Override
            public void onAdShown() {
                Log.d(TAG, EVENT_INTERSTITIAL_SHOWN);
                self.emitWindowEvent(EVENT_INTERSTITIAL_SHOWN);
            }

            @Override
            public void onAdDismissed() {
                Log.d(TAG, EVENT_INTERSTITIAL_CLOSED);
                self.emitWindowEvent(EVENT_INTERSTITIAL_CLOSED);
            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void onImpression(@Nullable ImpressionData var1) {

            }

            @Override
            public void onLeftApplication() {
            }

            @Override
            public void onReturnedToApplication() {
            }
        });
    }

    /**
     * ----------------------- VALIDATION INTEGRATION ---------------------------
     */

    /**
     * Validates integration action
     */
    private void validateIntegrationAction(JSONArray args, final CallbackContext callbackContext) {
    }

    /** ----------------------- REWARDED VIDEO --------------------------- */

    private void loadRewardedAction(JSONArray args, final CallbackContext callbackContext) {
        final YandexAdsPlugin self = this;
        final AdRequest adRequest = new AdRequest.Builder().build();
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                self.initRewarded(rewardedBlockId);
                mRewardedAd.loadAd(adRequest);
                callbackContext.success();
            }
        });
    }

    private void showRewardedVideoAction(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mRewardedAd.show();
                callbackContext.success();
            }
        });
    }

    /** ----------------------- INTERSTITIAL --------------------------- */

    private void loadInterstitialAction(JSONArray args, final CallbackContext callbackContext) {
        final YandexAdsPlugin self = this;
        final AdRequest adRequest = new AdRequest.Builder().build();
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                self.initInterstitial(interstitialBlockId);
                mInterstitialAd.loadAd(adRequest);
                callbackContext.success();
            }
        });
    }

    private void showInterstitialAction(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mInterstitialAd.show();
                callbackContext.success();
            }
        });
    }

    /** ----------------------- BANNER --------------------------- */
    private void showBannerAction(JSONArray args, final CallbackContext callbackContext) {

    }

    private void loadBannerAction(JSONArray args, final CallbackContext callbackContext) {

    }

    private void hideBannerView() {

    }

    /**
     * Destory Banner
     */
    private void destroyBanner() {
    }

    /**
     * Destroys Yandex Ads Banner and removes it from the container
     */
    private void hideBannerAction(JSONArray args, final CallbackContext callbackContext) {

    };

}
