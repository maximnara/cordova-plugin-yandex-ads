#import "YandexAdsPlugin.h"
#import <Cordova/CDV.h>

static NSString *const EVENT_INTERSTITIAL_LOADED = @"interstitialLoaded";
static NSString *const EVENT_INTERSTITIAL_SHOWN = @"interstitialShown";
static NSString *const EVENT_INTERSTITIAL_CLOSED = @"interstitialClosed";
static NSString *const EVENT_INTERSTITIAL_FAILED_TO_LOAD = @"interstitialFailedToLoad";

static NSString *const EVENT_REWARDED_VIDEO_LOADED = @"rewardedVideoLoaded";
static NSString *const EVENT_REWARDED_VIDEO_FAILED = @"rewardedVideoFailed";
static NSString *const EVENT_REWARDED_VIDEO_REWARDED = @"rewardedVideoRewardReceived";
static NSString *const EVENT_REWARDED_VIDEO_STARTED = @"rewardedVideoStarted";
static NSString *const EVENT_REWARDED_VIDEO_CLOSED = @"rewardedVideoClosed";

static NSString *const EVENT_BANNER_DID_LOAD = @"bannerDidLoad";
static NSString *const EVENT_BANNER_FAILED_TO_LOAD = @"bannerFailedToLoad";
static NSString *const EVENT_BANNER_DID_CLICK = @"bannerDidClick";
static NSString *const EVENT_BANNER_WILL_PRESENT_SCREEN = @"bannerWillPresentScreen";
static NSString *const EVENT_BANNER_DID_DISMISS_SCREEN = @"bannerDidDismissScreen";
static NSString *const EVENT_BANNER_WILL_LEAVE_APPLICATION = @"bannerWillLeaveApplication";

@implementation YandexAdsPlugin

#pragma mark - CDVPlugin

/**
 * Init
 * @params {CDVInvokedUrlCommand} command
 */
- (void)init:(CDVInvokedUrlCommand *)command
{
    NSLog(@"%s", "TEST");

    self.rewardedBlockId = [command argumentAtIndex:0];
    self.interstitialBlockId = [command argumentAtIndex:1];

    // Send callback successfull
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

/**
 * Emit window event
 * @param {NString} - event name
 */
- (void)emitWindowEvent:(NSString *)event
{
    NSString *js = [NSString stringWithFormat:@"cordova.fireWindowEvent('%@')", event];
    [self.commandDelegate evalJs:js];
}

/**
 * Emits window event with data
 * @param {NSString} - event name
 * @param {NSDictionary} - event data
 */
- (void)emitWindowEvent:(NSString *)event withData:(NSDictionary *)data
{
    NSError *error = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:data options:kNilOptions error:&error];

    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    NSString *js = [NSString stringWithFormat:@"cordova.fireWindowEvent('%@', %@)", event, jsonString];
    [self.commandDelegate evalJs:js];
}

//- (void)listSubviewsOfView:(UIView *)view {
//
//    // Get the subviews of the view
//    NSArray *subviews = [view subviews];
//
//    // Return if there are no subviews
//    if ([subviews count] == 0) return; // COUNT CHECK LINE
//
//    for (UIView *subview in subviews) {
//
//        // Do what you want to do with the subview
//        NSLog(@"%@", subview);
//
//        // List the subviews of subview
//        [self listSubviewsOfView:subview];
//    }
//}

#pragma mark - Rewarded Video Delegate Functions

/**
 * Load rewarded video
 */
- (void)loadRewardedVideo:(CDVInvokedUrlCommand *)command
{
    self.rewardedAd = [[YMARewardedAd alloc] initWithAdUnitID:self.rewardedBlockId];
    self.rewardedAd.delegate = self;

    [self.rewardedAd load];

    // Send callback successfull
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

/**
 * Show rewarded video
 */
- (void)showRewardedVideo:(CDVInvokedUrlCommand *)command
{
    NSLog(@"%s", "showRewardedVideo");
    [self.rewardedAd presentFromViewController:self.viewController];

    // Send callback successfull
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)rewardedAdDidLoad:(YMARewardedAd *)rewardedAd
{
    NSLog(@"%s", __PRETTY_FUNCTION__);
    [self emitWindowEvent:EVENT_REWARDED_VIDEO_LOADED];
}

- (void)rewardedAdDidFailToLoad:(nonnull YMARewardedAd *)rewardedAd
                          error:(nonnull NSError *)error {
    NSLog(@"rewardedAdDidFailToLoad");
    NSLog(@"%@", error.description);
    [self emitWindowEvent:EVENT_REWARDED_VIDEO_FAILED];
}

- (void)rewardedAdDidAppear:(nonnull YMARewardedAd *)rewardedAd {
    [self emitWindowEvent:EVENT_REWARDED_VIDEO_STARTED];
}

- (void)rewardedAd:(nonnull YMARewardedAd *)rewardedAd
         didReward:(nonnull id<YMAReward>)reward {
    [self emitWindowEvent:EVENT_REWARDED_VIDEO_REWARDED];
}

- (void)rewardedAdDidDisappear:(nonnull YMARewardedAd *)rewardedAd {
    [self emitWindowEvent:EVENT_REWARDED_VIDEO_CLOSED];
}


- (void)loadBanner:(CDVInvokedUrlCommand *)command
{

}

#pragma mark - Banner Delegate Functions

// Show banner
- (void)showBanner:(CDVInvokedUrlCommand *)command
{
}

- (void)hideBanner:(CDVInvokedUrlCommand *)command
{
}

- (void)destroyBanner
{
}

// Banner dismissed screen
- (void)bannerDidDismissScreen
{
}

//
- (void)bannerDidFailToLoadWithError:(NSError *)error
{
}

- (void)bannerDidLoad:bannerView
{
}


- (void)bannerWillLeaveApplication
{
    NSLog(@"%s", __PRETTY_FUNCTION__);
    [self emitWindowEvent:EVENT_BANNER_WILL_LEAVE_APPLICATION];
}

- (void)bannerWillPresentScreen
{
    NSLog(@"%s", __PRETTY_FUNCTION__);
    [self emitWindowEvent:EVENT_BANNER_WILL_PRESENT_SCREEN];
}

- (void)didClickBanner
{
    NSLog(@"%s", __PRETTY_FUNCTION__);
    [self emitWindowEvent:EVENT_BANNER_DID_CLICK];
}

#pragma mark - Intersitial Delegate Functions

- (void)loadInterstitial:(CDVInvokedUrlCommand *)command
{
    self.interstitialAd = [[YMAInterstitialAd alloc] initWithAdUnitID:self.interstitialBlockId];
    self.interstitialAd.delegate = self;

    [self.interstitialAd load];

    // Send callback successfull
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)showInterstitial:(CDVInvokedUrlCommand *)command
{
    [self.interstitialAd presentFromViewController:self.viewController];

    // Send callback successfull
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)interstitialAdDidLoad:(nonnull YMAInterstitialAd *)interstitialAd {
    [self emitWindowEvent:EVENT_INTERSTITIAL_LOADED];
}

- (void)interstitialAdDidFailToLoad:(nonnull YMAInterstitialAd *)interstitialAd
                              error:(nonnull NSError *)error {
    NSLog(@"interstitialAdDidFailToLoad");
    NSLog(@"%@", error.description);
    [self emitWindowEvent:EVENT_INTERSTITIAL_FAILED_TO_LOAD];
}

- (void)interstitialAdDidAppear:(nonnull YMAInterstitialAd *)interstitialAd {
    [self emitWindowEvent:EVENT_INTERSTITIAL_SHOWN];
}

- (void)interstitialAdDidDisappear:(nonnull YMAInterstitialAd *)interstitialAd {
    [self emitWindowEvent:EVENT_INTERSTITIAL_CLOSED];
}

@end
