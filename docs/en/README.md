[Русский](../ru/README.md) | [English](README.md) | [Project](../../README.md)

# Yandex Ads for Cordova apps
[![NPM Downloads](https://img.shields.io/npm/dt/cordova-plugin-yandex-ads)](https://www.npmjs.com/package/cordova-plugin-yandex-ads) [![NPM Version](https://img.shields.io/npm/v/cordova-plugin-yandex-ads)](https://www.npmjs.com/package/cordova-plugin-yandex-ads)

### [Demo Video](https://youtube.com/watch?v=GN_R5Am5hbI)

## Table of Contents

- [State of Development](#state-of-development)
- [Install](#install)
- [Requirements](#requirements)
- [Migration 7 → 8 version](MIGRATION.md)
- [Usage](#usage)
- [Official Android documentation](https://ads.yandex.com/helpcenter/en/dev/android)
- [Official iOS documentation](https://ads.yandex.com/helpcenter/en/dev/ios)


## State of Development
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Rewarded%20Video%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Interstitial%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Banner%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=App%20Open%20Ads%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Instream%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Feed%20Support&style=flat-square">

--------

## Install

```bash
cordova plugin add cordova-plugin-yandex-ads
```

## Requirements

This plugin uses Yandex Mobile Ads **8.6.0** on Android and iOS.

| Platform | Requirements |
| --- | --- |
| Android | Cordova Android 15+ and its standard Android SDK, JDK, and Gradle toolchain |
| iOS | Cordova iOS 7.0.1+ (8.x recommended), iOS 15+, Xcode 26.2+ recommended, CocoaPods |

Use Cordova CLI 13+ and a supported Node.js version. The plugin enables Kotlin using the version provided by Cordova and configures the iOS deployment target automatically. No application build preferences or helper scripts are needed for a new project.

Existing SDK 7 applications: see [Migration 7 → 8 version](MIGRATION.md).

--------
## Usage

- [Initialization](#initialization)
- [Rewarded Videos](#rewarded-videos)
  - [Load Rewarded Video](#load-rewarded-video)
  - [Show Rewarded Video](#show-rewarded-video)
- [Interstitials](#interstitials)
  - [Load Interstitial](#load-interstitial)
  - [Show Interstitial](#show-interstitial)
- [App open ads](#app-open-ads)
  - [Load App Open Ads](#load-app-open-ads)
  - [Show App Open Ads](#show-app-open-ads)
- [Events](#events)
- [Banners](#banners)
  - [Load Banner](#load-banner)
  - [Show Banner](#show-banner)
  - [Reload Banner](#reload-banner)
- [Instream](#instream)
  - [Load Instream](#load-instream)
  - [Show Instream](#show-instream)
  - [Hide Instream](#hide-instream)
- [Feed](#feed)
  - [Load Feed](#load-feed)
  - [Show Feed](#show-feed)
  - [Hide Feed](#hide-feed)

### Initialization

```javascript
document.addEventListener('deviceready', async () => {
  await YandexAds.init({
    rewardedBlockId: 'YOUR_REWARDED_BLOCK_ID',
    interstitialBlockId: 'YOUR_INTERSTITIAL_ID',
    bannerBlockId: 'YOUR_BANNER_ID',
    openAppBlockId: 'YOUR_OPEN_APP_ADS_ID',
    instreamBlockId: 'YOUR_INSTREAM_ID',
    feedBlockId: 'YOUR_FEED_ID',
    options: { // This is for banner ads
      bannerAtTop: true, // Show banner on top of screen, otherwise on bottom
      bannerSize: { width: 468, height: 100 }, // Your banner size
      // You can skip bannerSize option and width will be as big as possible
    },
  });
});
```

### Set user consent for GDPR
Pass the user’s consent choice with `setUserConsent()` on each app launch before requesting ads. The value below is an example; use the actual choice collected by your application.

```javascript
YandexAds.setUserConsent(true);
```
***
### Rewarded Videos

https://github.com/maximnara/cordova-plugin-yandex-ads/assets/2614172/520052cd-48ae-4db7-b888-6344fc83b54f


#### Load Rewarded Video

```javascript
YandexAds.loadRewardedVideo();
```

#### Show Rewarded Video

```javascript
YandexAds.showRewardedVideo();
```
***
### Interstitials

https://github.com/maximnara/cordova-plugin-yandex-ads/assets/2614172/2eb27491-e36d-4b2f-bd78-3020b1d86d86

#### Load Interstitial
Must be called before `showInterstitial`

```javascript
YandexAds.loadInterstitial();
```
***
#### Show Interstitial

```javascript
YandexAds.showInterstitial();
```
***
### App Open Ads

https://github.com/maximnara/cordova-plugin-yandex-ads/assets/2614172/e6761531-05c7-4699-a996-a940a75efee9

#### Load app open ads
Must be called before `showOpenAppAds`

```javascript
YandexAds.loadOpenAppAds();
```
***
#### Show app open ads

```javascript
YandexAds.showOpenAppAds();
```
***
### Banners

#### Load Banner
Must be called before `showBanner`

```javascript
YandexAds.loadBanner();
```
***
#### Show Banner

```javascript
YandexAds.showBanner();
```
***
#### Reload Banner
When `bannerSize` is set, the banner uses an inline container that changes the space available to the web view. Use `reloadBanner()` to preserve the container during subsequent loads and avoid layout jumps.

```javascript
YandexAds.reloadBanner();
```
***
#### Hide Banner

```javascript
YandexAds.hideBanner();
```
***
### Instream
InStream is currently Android-only. The plugin uses the bundled `jc.mp4` as content for the video player and supports D-pad navigation on Android TV.

#### Load instream
```javascript
YandexAds.loadInstream();
```
***

#### Show instream
```javascript
YandexAds.showInstream();
```
***

#### Hide instream
```javascript
YandexAds.hideInstream();
```
***

### Feed
Feed is currently Android-only. It displays a vertically scrolling list of advertising cards.

#### Load feed
```javascript
YandexAds.loadFeed();
```
***

#### Show feed
```javascript
YandexAds.showFeed();
```
***

#### Hide feed
```javascript
YandexAds.hideFeed();
```
***

### Events

Event names are defined in [the JavaScript API](../../www/yandexads.js). `load*()` promises acknowledge the native request; wait for the `loaded` or `failedToLoad` event to receive the actual loading result.
```javascript
{
  interstitial: {
    loaded: 'interstitialDidLoad',
    failedToLoad: 'interstitialFailedToLoad',
    shown: 'interstitialDidShow',
    failedToShow: 'interstitialDidFailToShowWithError',
    dismissed: 'interstitialDidDismiss',
    clicked: 'interstitialDidClick',
    impression: 'interstitialDidTrackImpressionWith',
  },
  rewarded: {
    loaded: 'rewardedDidLoad',
    failedToLoad: 'rewardedFailedToLoad',
    rewarded: 'rewardedDidReward',
    shown: 'rewardedDidShow',
    failedToShow: 'rewardedDidFailToShowWithError',
    dismissed: 'rewardedDidDismiss',
    clicked: 'rewardedDidClick',
    impression: 'rewardedDidTrackImpressionWith',
  },
  openAppAds: {
    loaded: 'appOpenDidLoad',
    failedToLoad: 'appOpenFailedToLoad',
    shown: 'appOpenDidShow',
    failedToShow: 'appOpenDidFailToShowWithError',
    dismissed: 'appOpenDidDismiss',
    clicked: 'appOpenDidClick',
    impression: 'appOpenDidTrackImpressionWith',
  },
  banner: {
    loaded: 'bannerDidLoad',
    failedToLoad: 'bannerFailedToLoad',
    clicked: 'bannerDidClick',
    impression: 'bannerDidTrackImpressionWith',
    // leftApplication removed: SDK 8 no longer emits bannerWillLeaveApplication.
  },
  feed: {
    loaded: 'feedDidLoad',
    failedToLoad: 'feedFailedToLoad',
    clicked: 'feedDidClick',
    impression: 'feedDidTrackImpressionWith',
  },
  instream: {
    loaded: 'instreamDidLoad',
    failedToLoad: 'instreamFailedToLoad',
    error: 'instreamError',
    completed: 'instreamAdCompleted',
    prepared: 'instreamAdPrepared',
  }
}
```

#### How to use events:
Register the listener before loading. The example shows AppOpen ads as soon as the `loaded` event fires.
```javascript
window.addEventListener(YandexAds.events.openAppAds.loaded, async () => {
  await YandexAds.showOpenAppAds();
});

await YandexAds.loadOpenAppAds();
```

### Additional steps

**iOS**

The plugin automatically adds the bundled list of SKAdNetwork identifiers recommended by Yandex to `SKAdNetworkItems` in the app’s `Info.plist`, preserving existing entries. Reinstall the plugin and rebuild existing projects to apply this setting.

The bundled list is a snapshot from November 1, 2026. Review the current [SKAdNetwork list and integration instructions](https://ads.yandex.com/helpcenter/en/dev/ios/skadnetwork) when adding other networks or updating dependencies.

### Contributing

Pull requests for fixes, code improvements, and new features are welcome.
