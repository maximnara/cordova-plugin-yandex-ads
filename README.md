# Yandex Ads for Cordova apps
![NPM Downloads](https://img.shields.io/npm/dt/cordova-plugin-yandex-ads) ![NPM Version](https://img.shields.io/npm/v/cordova-plugin-yandex-ads)

--------
## Support plugin [https://boosty.to/maximnara](https://boosty.to/maximnara)

### [Demo Video](https://youtube.com/watch?v=GN_R5Am5hbI)

## Table of Contents

- [State of Development](#state-of-development)
- [Install](#install)
- [Usage](#usage)
- [Official Yandex Documentation](https://yandex.ru/dev/mobile-ads/doc/intro/about.html)


## State of Development
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Rewarded%20Video%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Interstitial%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Banner%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=App%20Open%20Ads%20Support&style=flat-square">

-------- 

## Install

```bash
npm i cordova-plugin-yandex-ads --save
```

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
  - [Banner Events](#banner-events)
  
  
All methods support optional `onSuccess` and `onFailure` parameters

### Initialization

```javascript
import * as YandexAds from 'cordova-plugin-yandex-ads/www/yandexads';
await YandexAds.init({ 
  rewardedBlockId: 'YOUR_REWARDER_BLOCK_ID',
  interstitialBlockId: 'YOUR_INTERSTITIAL_ID',
  bannerBlockId: 'YOOUR_BANNER_ID',
  openAppBlockId: 'YOUR_OPEN_APP_ADS_ID',
  options: { // This is for banner ads
    bannerAtTop: true, // Show banner on top of screen, otherwise on bottom
    bannerSize: { width: 468, height: 100 }, // Your banner size
    // You can skip bannerSize option and width will be as big as possible
  },
});
```

### Set user consent for GDPR
Call this on every app launch. More info: https://yandex.ru/dev/mobile-ads/doc/android/quick-start/gdpr-about.html

```javascript
YandexAds.setUserConsent(true);
```
***
### Rewarded Videos

https://github.com/maximnara/cordova-plugin-yandex-ads/assets/2614172/520052cd-48ae-4db7-b888-6344fc83b54f


#### Load Rewarded Video

```javascript
YandexAds.loadRewardedVideo({
  onSuccess: function () {

  },
  onFailure: function () {

  },
});
```

#### Show Rewarded Video

```javascript
YandexAds.showRewardedVideo();
```
***
### Interstitial

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
#### Hide Banner

```javascript
YandexAds.hideBanner();
```
***

### Events

Also you can find them [here](www/yandexads.js)
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
    leftApplication: 'bannerWillLeaveApplication',
  }
}
```

#### How to use events:
Here we start listen ad loaded event, when it is fired we call `showOpenAppAds` method.
```javascript
window.addEventListener(YandexAds.events.openAppAds.loaded, async () => {
  await YandexAds.showOpenAppAds();
});
```

### Additional steps

**iOS**

Add this to your Info.plist <br>
Please check official documentation in case of some breaking changes

```bash
<key>SKAdNetworkItems</key>
<array>
  <dict>
    <key>SKAdNetworkIdentifier</key>
    <string>zq492l623r.skadnetwork</string>
  </dict>
</array>
```

### Feel free to make your PRs for code structure or new functions
