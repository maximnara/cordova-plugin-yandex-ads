# Yandex Ads for Cordova apps

--------

## Table of Contents

- [State of Development](#state-of-development)
- [Install](#install)
- [Usage](#usage)
- [Official IronSource Documentation](https://yandex.ru/dev/mobile-ads/doc/intro/about.html)


## State of Development
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Rewarded%20Video%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Interstitial%20Support&style=flat-square">
- [ ] <img src="https://img.shields.io/badge/-In%20Development-yellow.svg?label=Banner%20Support&style=flat-square">

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
  - [Rewarded Video Events](#rewarded-video-events)
- [Interstitials](#interstitials)
  - [Load Interstitial](#load-interstitial)
  - [Show Interstitial](#show-interstitial)
  - [Interstitial Events](#interstitial-events)
  
  
All methods support optional `onSuccess` and `onFailure` parameters

### Initialization

```javascript
import * as YandexAds from 'cordova-plugin-yandex-ads/www/yandexads';
await YandexAds.init({ 
  rewardedBlockId: 'YOUR_BANNER_ID',
  interstitialBlockId: 'YOUR_ID',
});
```
***
### Rewarded Videos

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

#### Rewarded Video Events
**Rewarded Video Loaded**
```javascript
window.addEventListener("rewardedVideoLoaded", function(){
  YandexAds.showRewardedVideo();
});
```
**Rewarded Video Rewarded**
```javascript
window.addEventListener("rewardedVideoRewardReceived", function(){
  // some logics
});
```
**Rewarded Video Started**
```javascript
window.addEventListener("rewardedVideoStarted", function(){

});
```
**Rewarded Video Closed**
```javascript
window.addEventListener("rewardedVideoClosed", function(){

});
```
**Rewarded Video Failed**
```javascript
window.addEventListener("rewardedVideoFailed", function(){

});
```
***
### Interstitial

#### Load Interstitial
_Must be called before `showInterstitial`

```javascript
YandexAds.loadInterstitial();
```
***
#### Show Interstitial

```javascript
YandexAds.showInterstitial();
```
***
#### Interstitial Events

**Interstitial Loaded**
```javascript
window.addEventListener("interstitialLoaded", function(){
  YandexAds.showInterstitial();
});
```
**Interstitial Shown**
```javascript
window.addEventListener("interstitialShown", function(){

});
```
**Interstitial Closed**
```javascript
window.addEventListener("interstitialClosed", function(){

});
```
**Interstitial Failed To Load**
```javascript
window.addEventListener("interstitialFailedToLoad", function(){

});
```
***

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
