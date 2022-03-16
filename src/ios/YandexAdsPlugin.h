#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>
#import <YandexMobileAds/YMARewardedAd.h>

@interface YandexAdsPlugin : CDVPlugin <YMARewardedAdDelegate, YMAInterstitialAdDelegate>

@property (nonatomic, strong) YMARewardedAd *rewardedAd;
@property (nonatomic, strong) YMAInterstitialAd *interstitialAd;
@property NSString *rewardedBlockId;
@property NSString *interstitialBlockId;

- (void)init:(CDVInvokedUrlCommand *)command;

- (void)loadRewardedVideo:(CDVInvokedUrlCommand *)command;

- (void)showRewardedVideo:(CDVInvokedUrlCommand *)command;

- (void)showBanner:(CDVInvokedUrlCommand *)command;

- (void)hideBanner:(CDVInvokedUrlCommand *)command;

- (void)loadInterstitial:(CDVInvokedUrlCommand *)command;

- (void)showInterstitial:(CDVInvokedUrlCommand *)command;

@end
