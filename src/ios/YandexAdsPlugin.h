#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>
#import <YandexMobileAds/YMARewardedAd.h>

@interface YandexAdsPlugin : CDVPlugin <YMARewardedAdDelegate, YMAInterstitialAdDelegate, YMAAdViewDelegate>

@property (nonatomic, strong) YMARewardedAd *rewardedAd;
@property (nonatomic, strong) YMAInterstitialAd *interstitialAd;
@property (nonatomic, strong) YMAAdView *adView;
@property NSString *rewardedBlockId;
@property NSString *interstitialBlockId;
@property NSString *bannerBlockId;

@property BOOL *bannerAtTop;
@property BOOL *bannerOverlap;
@property CGSize bannerSize;

- (void)init:(CDVInvokedUrlCommand *)command;

- (void)loadRewardedVideo:(CDVInvokedUrlCommand *)command;

- (void)showRewardedVideo:(CDVInvokedUrlCommand *)command;

- (void)loadBanner:(CDVInvokedUrlCommand *)command;

- (void)showBanner:(CDVInvokedUrlCommand *)command;

- (void)hideBanner:(CDVInvokedUrlCommand *)command;

- (void)loadInterstitial:(CDVInvokedUrlCommand *)command;

- (void)showInterstitial:(CDVInvokedUrlCommand *)command;

- (void)setUserConsent:(CDVInvokedUrlCommand *)command;

@end
