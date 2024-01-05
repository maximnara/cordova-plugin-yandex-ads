package io.luzh.cordova.plugin;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.banner.BannerAdSize;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.InitializationListener;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;

import com.yandex.mobile.ads.rewarded.RewardedAdLoader;
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener;
import com.yandex.mobile.ads.rewarded.RewardedAd;
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener;
import com.yandex.mobile.ads.rewarded.Reward;

import com.yandex.mobile.ads.interstitial.InterstitialAdLoader;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;

import com.yandex.mobile.ads.appopenad.AppOpenAdLoader;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener;
import com.yandex.mobile.ads.appopenad.AppOpenAd;
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class YandexAdsPlugin extends CordovaPlugin {

    private static final String TAG = "YANDEX_ADS";

    // Interstitial events
    private static final String EVENT_INTERSTITIAL_LOADED = "interstitialDidLoad";
    private static final String EVENT_INTERSTITIAL_FAILED_TO_LOAD = "interstitialFailedToLoad";
    private static final String EVENT_INTERSTITIAL_SHOWN = "interstitialDidShow";
    private static final String EVENT_INTERSTITIAL_FAILED_TO_SHOW = "interstitialDidFailToShowWithError";
    private static final String EVENT_INTERSTITIAL_AD_DISMISSED = "interstitialDidDismiss";
    private static final String EVENT_INTERSTITIAL_AD_CLICKED = "interstitialDidClick";
    private static final String EVENT_INTERSTITIAL_AD_IMPRESSION = "interstitialDidTrackImpressionWith";

    // Open App Ad events
    private static final String EVENT_APP_OPEN_ADS_LOADED = "appOpenDidLoad";
    private static final String EVENT_APP_OPEN_ADS_FAILED_TO_LOAD = "appOpenFailedToLoad";
    private static final String EVENT_APP_OPEN_ADS_SHOWN = "appOpenDidShow";
    private static final String EVENT_APP_OPEN_ADS_FAILED_TO_SHOW = "appOpenDidFailToShowWithError";
    private static final String EVENT_APP_OPEN_ADS_DISMISSED = "appOpenDidDismiss";
    private static final String EVENT_APP_OPEN_ADS_CLICKED = "appOpenDidClick";
    private static final String EVENT_APP_OPEN_ADS_IMPRESSION = "appOpenDidTrackImpressionWith";

    // Rewarded events
    private static final String EVENT_REWARDED_VIDEO_LOADED = "rewardedDidLoad";
    private static final String EVENT_REWARDED_VIDEO_FAILED_TO_LOAD = "rewardedFailedToLoad";
    private static final String EVENT_REWARDED_VIDEO_REWARDED = "rewardedDidReward";
    private static final String EVENT_REWARDED_VIDEO_SHOWN = "rewardedDidShow";
    private static final String EVENT_REWARDED_VIDEO_FAILED_TO_SHOW = "rewardedDidFailToShowWithError";
    private static final String EVENT_REWARDED_VIDEO_AD_DISMISSED = "rewardedDidDismiss";
    private static final String EVENT_REWARDED_VIDEO_AD_CLICKED = "rewardedDidClick";
    private static final String EVENT_REWARDED_VIDEO_AD_IMPRESSION = "rewardedDidTrackImpressionWith";

    // Banner events
    private static final String EVENT_BANNER_DID_LOAD = "bannerDidLoad";
    private static final String EVENT_BANNER_FAILED_TO_LOAD = "bannerFailedToLoad";
    private static final String EVENT_BANNER_DID_CLICK = "bannerDidClick";
    private static final String EVENT_BANNER_IMPRESSION = "bannerDidTrackImpressionWith";
    private static final String EVENT_BANNER_LEFT_APPLICATION = "bannerWillLeaveApplication";

    private RelativeLayout bannerContainerLayout;
    private CordovaWebView cordovaWebView;
    private ViewGroup parentLayout;
    private Boolean bannerLoaded = false;
    private Boolean bannerShown = false;
    private Boolean bannerAtTop = false;

    private JSONObject bannerSize = null;

    private RewardedAd mRewardedAd = null;
    private InterstitialAd mInterstitialAd = null;
    private AppOpenAd mOpenAppAd = null;
    private BannerAdView mBannerAdView;

    private String rewardedBlockId;
    private String interstitialBlockId;
    private String bannerBlockId;
    private String openAppBlockId;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        if (action.equals("run")) {
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

        else if (action.equals("loadOpenAppAds")) {
            this.loadOpenAppAdsAction(args, callbackContext);
            return true;
        }

        else if (action.equals("showOpenAppAds")) {
            this.showOpenAppAdsAction(args, callbackContext);
            return true;
        }

        else if (action.equals("setUserConsent")) {
            this.setUserConsentAction(args.getBoolean(0), callbackContext);
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
        bannerBlockId = args.getString(2);
        openAppBlockId = args.getString(3);

        JSONObject options = args.optJSONObject(4);
//        if(options.has("overlap")) bannerOverlap = options.optBoolean("overlap");
        if(options.has("bannerAtTop")) bannerAtTop = options.optBoolean("bannerAtTop");
        if(options.has("bannerSize")) bannerSize = options.optJSONObject("bannerSize");

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
    private RewardedAdLoader getRewardedLoader() {

        final YandexAdsPlugin self = this;
        final RewardedAdLoader loader = new RewardedAdLoader(this.cordova.getContext());

        loader.setAdLoadListener(new RewardedAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;

                mRewardedAd.setAdEventListener(new RewardedAdEventListener() {

                    @Override
                    public void onRewarded(final Reward reward) {
                        Log.d(TAG, EVENT_REWARDED_VIDEO_REWARDED);
                        self.emitWindowEvent(EVENT_REWARDED_VIDEO_REWARDED);
                    }

                    @Override
                    public void onAdShown() {
                        Log.d(TAG, EVENT_REWARDED_VIDEO_SHOWN);
                        self.emitWindowEvent(EVENT_REWARDED_VIDEO_SHOWN);
                    }

                    @Override
                    public void onAdFailedToShow(@NonNull AdError var1) {
                        Log.d(TAG, EVENT_REWARDED_VIDEO_FAILED_TO_SHOW);
                        self.emitWindowEvent(EVENT_REWARDED_VIDEO_FAILED_TO_SHOW);
                    }

                    @Override
                    public void onAdDismissed() {
                        Log.d(TAG, EVENT_REWARDED_VIDEO_AD_DISMISSED);
                        self.emitWindowEvent(EVENT_REWARDED_VIDEO_AD_DISMISSED);
                    }

                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, EVENT_REWARDED_VIDEO_AD_CLICKED);
                        self.emitWindowEvent(EVENT_REWARDED_VIDEO_AD_CLICKED);
                    }

                    @Override
                    public void onAdImpression(@Nullable ImpressionData var1) {
                        Log.d(TAG, EVENT_REWARDED_VIDEO_AD_IMPRESSION);
                        self.emitWindowEvent(EVENT_REWARDED_VIDEO_AD_IMPRESSION);
                    }
                });

                Log.d(TAG, EVENT_REWARDED_VIDEO_LOADED);
                self.emitWindowEvent(EVENT_REWARDED_VIDEO_LOADED);
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
                Log.d(TAG, EVENT_REWARDED_VIDEO_FAILED_TO_LOAD + ": " + adRequestError.getDescription());
                self.emitWindowEvent(EVENT_REWARDED_VIDEO_FAILED_TO_LOAD);
            }
        });

        return loader;
    }

    private InterstitialAdLoader getInterstitialLoader() {

        final YandexAdsPlugin self = this;

        final InterstitialAdLoader loader = new InterstitialAdLoader(this.cordova.getContext());
        loader.setAdLoadListener(new InterstitialAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;

                mInterstitialAd.setAdEventListener(new InterstitialAdEventListener() {
                    @Override
                    public void onAdShown() {
                        Log.d(TAG, EVENT_INTERSTITIAL_SHOWN);
                        self.emitWindowEvent(EVENT_INTERSTITIAL_SHOWN);
                    }

                    @Override
                    public void onAdFailedToShow(@NonNull AdError error) {
                        Log.d(TAG, EVENT_INTERSTITIAL_FAILED_TO_SHOW);
                        self.emitWindowEvent(EVENT_INTERSTITIAL_FAILED_TO_SHOW);
                    }

                    @Override
                    public void onAdDismissed() {
                        Log.d(TAG, EVENT_INTERSTITIAL_AD_DISMISSED);
                        self.emitWindowEvent(EVENT_INTERSTITIAL_AD_DISMISSED);
                    }

                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, EVENT_INTERSTITIAL_AD_CLICKED);
                        self.emitWindowEvent(EVENT_INTERSTITIAL_AD_CLICKED);
                    }

                    @Override
                    public void onAdImpression(@Nullable ImpressionData var1) {
                        Log.d(TAG, EVENT_INTERSTITIAL_AD_IMPRESSION);
                        self.emitWindowEvent(EVENT_INTERSTITIAL_AD_IMPRESSION);
                    }
                });

                Log.d(TAG, EVENT_INTERSTITIAL_LOADED);
                self.emitWindowEvent(EVENT_INTERSTITIAL_LOADED);
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
                Log.d(TAG, EVENT_INTERSTITIAL_FAILED_TO_LOAD + ": " + adRequestError.getDescription());
                self.emitWindowEvent(EVENT_INTERSTITIAL_FAILED_TO_LOAD);
            }
        });

        return loader;
    }

    private AppOpenAdLoader getOpenAppAdsLoader() {
        final YandexAdsPlugin self = this;

        final AppOpenAdLoader loader = new AppOpenAdLoader(this.cordova.getContext());
        loader.setAdLoadListener(new AppOpenAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd openAppAd) {
                mOpenAppAd = openAppAd;

                mOpenAppAd.setAdEventListener(new AppOpenAdEventListener() {
                    @Override
                    public void onAdShown() {
                        Log.d(TAG, EVENT_APP_OPEN_ADS_SHOWN);
                        self.emitWindowEvent(EVENT_APP_OPEN_ADS_SHOWN);
                    }

                    @Override
                    public void onAdFailedToShow(@NonNull AdError error) {
                        Log.d(TAG, EVENT_APP_OPEN_ADS_FAILED_TO_SHOW);
                        self.emitWindowEvent(EVENT_APP_OPEN_ADS_FAILED_TO_SHOW);
                    }

                    @Override
                    public void onAdDismissed() {
                        Log.d(TAG, EVENT_APP_OPEN_ADS_DISMISSED);
                        self.emitWindowEvent(EVENT_APP_OPEN_ADS_DISMISSED);
                    }

                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, EVENT_APP_OPEN_ADS_CLICKED);
                        self.emitWindowEvent(EVENT_APP_OPEN_ADS_CLICKED);
                    }

                    @Override
                    public void onAdImpression(@Nullable ImpressionData var1) {
                        Log.d(TAG, EVENT_APP_OPEN_ADS_IMPRESSION);
                        self.emitWindowEvent(EVENT_APP_OPEN_ADS_IMPRESSION);
                    }
                });

                Log.d(TAG, EVENT_APP_OPEN_ADS_LOADED);
                self.emitWindowEvent(EVENT_APP_OPEN_ADS_LOADED);
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
                Log.d(TAG, EVENT_APP_OPEN_ADS_FAILED_TO_LOAD + ": " + adRequestError.getDescription());
                self.emitWindowEvent(EVENT_APP_OPEN_ADS_FAILED_TO_LOAD);
            }
        });

        return loader;
    }

    /** ----------------------- REWARDED VIDEO --------------------------- */

    private void loadRewardedAction(JSONArray args, final CallbackContext callbackContext) {
        final YandexAdsPlugin self = this;
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final RewardedAdLoader loader = self.getRewardedLoader();
                loader.loadAd(new AdRequestConfiguration.Builder(rewardedBlockId).build());
                callbackContext.success();
            }
        });
    }

    private void showRewardedVideoAction(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final YandexAdsPlugin self = this;
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (mRewardedAd != null) {
                    mRewardedAd.show(self.cordova.getActivity());
                }
                callbackContext.success();
            }
        });
    }

    /** ----------------------- INTERSTITIAL --------------------------- */

    private void loadInterstitialAction(JSONArray args, final CallbackContext callbackContext) {
        final YandexAdsPlugin self = this;
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final InterstitialAdLoader loader = self.getInterstitialLoader();
                loader.loadAd(new AdRequestConfiguration.Builder(interstitialBlockId).build());
                callbackContext.success();
            }
        });
    }

    private void showInterstitialAction(JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final YandexAdsPlugin self = this;

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(self.cordova.getActivity());
                }
                callbackContext.success();
            }
        });
    }

    /** -------------------- OPEN APP ADS ------------------------ */
    private void loadOpenAppAdsAction(JSONArray args, final CallbackContext callbackContext) {
        final YandexAdsPlugin self = this;
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final AppOpenAdLoader loader = self.getOpenAppAdsLoader();
                loader.loadAd(new AdRequestConfiguration.Builder(openAppBlockId).build());
                callbackContext.success();
            }
        });
    }

    private void showOpenAppAdsAction(JSONArray args, final CallbackContext callbackContext) {
        final YandexAdsPlugin self = this;

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (mOpenAppAd != null) {
                    mOpenAppAd.show(self.cordova.getActivity());
                }
                callbackContext.success();
            }
        });
    }

    /** ----------------------- BANNER --------------------------- */
    private void showBannerAction(JSONArray args, final CallbackContext callbackContext) {
        final YandexAdsPlugin self = this;

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (mBannerAdView != null && bannerLoaded && !bannerShown) {
                    bannerShown = true;
                    if (bannerSize == null) {
                        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params2.addRule(bannerAtTop ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.ALIGN_PARENT_BOTTOM);

                        RelativeLayout adViewLayout = new RelativeLayout(cordova.getActivity());
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        try {
                            ((ViewGroup)(((View)webView.getClass().getMethod("getView").invoke(webView)).getParent())).addView(adViewLayout, params);
                        } catch (Exception e) {
                            ((ViewGroup) webView).addView(adViewLayout, params);
                        }

                        adViewLayout.addView(mBannerAdView, params2);
                        adViewLayout.bringToFront();
                    } else {
                        parentLayout = (ViewGroup) cordovaWebView.getView().getParent();

                        View view = cordovaWebView.getView();

                        ViewGroup wvParentView = (ViewGroup) view.getParent();

                        LinearLayout parentView = new LinearLayout(cordovaWebView.getContext());

                        if (wvParentView != null && wvParentView != parentView) {
                            ViewGroup rootView = (ViewGroup) (view.getParent());
                            wvParentView.removeView(view);
                            ((LinearLayout) parentView).setOrientation(LinearLayout.VERTICAL);
                            parentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
                            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));
                            parentView.addView(view);
                            rootView.addView(parentView);
                        }

                        bannerContainerLayout = new RelativeLayout(self.cordova.getActivity());

                        bannerContainerLayout.setGravity(Gravity.BOTTOM);

                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                        bannerContainerLayout.addView(mBannerAdView, layoutParams);

                        mBannerAdView.setLayoutParams(layoutParams);

                        if (bannerAtTop) {
                            parentView.addView(bannerContainerLayout, 0);
                        } else {
                            parentView.addView(bannerContainerLayout);
                        }

                        ViewGroup contentView = cordova.getActivity().findViewById(android.R.id.content);
                        if (contentView != null) {
                            contentView.bringToFront();
                            contentView.requestLayout();
                            contentView.requestFocus();
                        }
                    }
                }
                callbackContext.success();
            }
        });
    }

    private void loadBannerAction(JSONArray args, final CallbackContext callbackContext) {
        final YandexAdsPlugin self = this;
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                hideBannerView();

                mBannerAdView = new BannerAdView(self.cordova.getActivity());
                mBannerAdView.setAdUnitId(bannerBlockId);

                if (bannerSize != null && bannerSize.has("width") && bannerSize.has("height")) {
                    mBannerAdView.setAdSize(BannerAdSize.inlineSize(self.cordova.getContext(), bannerSize.optInt("width"), bannerSize.optInt("height")));
                } else {
                    int adWidth = self.cordovaWebView.getView().getWidth();
                    mBannerAdView.setAdSize(BannerAdSize.stickySize(self.cordova.getContext(), adWidth));
                }

                bannerShown = false;

                final AdRequest adRequest = new AdRequest.Builder().build();

                mBannerAdView.setBannerAdEventListener(new BannerAdEventListener() {
                    @Override
                    public void onAdLoaded() {
                        bannerLoaded = true;
                        Log.d(TAG, EVENT_BANNER_DID_LOAD);
                        self.emitWindowEvent(EVENT_BANNER_DID_LOAD);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
                        Log.d(TAG, EVENT_BANNER_FAILED_TO_LOAD + ": " + adRequestError.getDescription());
                        self.emitWindowEvent(EVENT_BANNER_FAILED_TO_LOAD);
                    }

                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, EVENT_BANNER_DID_CLICK);
                        self.emitWindowEvent(EVENT_BANNER_DID_CLICK);
                    }

                    @Override
                    public void onImpression(@Nullable ImpressionData var1) {
                        Log.d(TAG, EVENT_BANNER_IMPRESSION);
                        self.emitWindowEvent(EVENT_BANNER_IMPRESSION);
                    }

                    @Override
                    public void onLeftApplication() {
                        Log.d(TAG, EVENT_BANNER_LEFT_APPLICATION);
                        self.emitWindowEvent(EVENT_BANNER_LEFT_APPLICATION);
                    }

                    @Override
                    public void onReturnedToApplication() {}
                });

                mBannerAdView.loadAd(adRequest);

                callbackContext.success();
            }
        });
    }

    /**
     * Destroys Yandex Ads Banner and removes it from the container
     */
    private void hideBannerAction(JSONArray args, final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                hideBannerView();
                callbackContext.success();
            }
        });
    };

    private void hideBannerView() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (mBannerAdView != null && bannerShown) {
                    if (parentLayout != null && bannerContainerLayout != null) {
                        if (mBannerAdView.getParent() != null) {
                            bannerContainerLayout.removeView(mBannerAdView);
                        }
                        if (bannerContainerLayout.getParent() != null) {
                            parentLayout.removeView(bannerContainerLayout);
                        }
                    }
                    destroyBanner();
                }
            }
        });
    }

    /**
     * Destory Banner
     */
    private void destroyBanner() {
        if (mBannerAdView != null) {
            try {
                mBannerAdView.destroy();
            } catch(Exception e) {
                Log.d(TAG, "Exception while destroying banner, seems too many requests");
            }
            mBannerAdView = null;
        }
    }

    private void setUserConsentAction(Boolean value, final CallbackContext callbackContext) {
        MobileAds.setUserConsent(value);
        Log.d(TAG, "setUserConsent: " + value);
        callbackContext.success();
    }

}
