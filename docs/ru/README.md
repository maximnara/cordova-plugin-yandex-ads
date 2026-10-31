[Русский](README.md) | [English](../en/README.md) | [Проект](../../README.md)

# Yandex Ads для приложений Cordova
[![NPM Downloads](https://img.shields.io/npm/dt/cordova-plugin-yandex-ads)](https://www.npmjs.com/package/cordova-plugin-yandex-ads) [![NPM Version](https://img.shields.io/npm/v/cordova-plugin-yandex-ads)](https://www.npmjs.com/package/cordova-plugin-yandex-ads)

### [Демонстрационное видео](https://youtube.com/watch?v=GN_R5Am5hbI)

## Содержание

- [Поддерживаемые форматы](#поддерживаемые-форматы)
- [Установка](#установка)
- [Требования](#требования)
- [Миграция 7 → 8](MIGRATION.md)
- [Использование](#использование)
- [Официальная документация Android](https://ads.yandex.com/helpcenter/ru/dev/android)
- [Официальная документация iOS](https://ads.yandex.com/helpcenter/ru/dev/ios)


## Поддерживаемые форматы
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Rewarded%20Video%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Interstitial%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Banner%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=App%20Open%20Ads%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Instream%20Support&style=flat-square">
- [x] <img src="https://img.shields.io/badge/-Complete-brightgreen.svg?label=Feed%20Support&style=flat-square">

--------

## Установка

```bash
cordova plugin add cordova-plugin-yandex-ads
```

## Требования

Плагин использует Yandex Mobile Ads **8.6.0** на Android и iOS.

| Платформа | Требования |
| --- | --- |
| Android | Cordova Android 15+ и стандартное окружение этой платформы: Android SDK, JDK и Gradle |
| iOS | Cordova iOS 7.0.1+ (рекомендуется 8.x), iOS 15+, рекомендуется Xcode 26.2+, CocoaPods |

Используйте Cordova CLI 13+ и поддерживаемую версию Node.js. Плагин сам подключает Kotlin версии, поставляемой с Cordova, и настраивает deployment target iOS. Для нового проекта не нужны дополнительные build preferences или вспомогательные скрипты приложения.

Для существующих приложений с SDK 7: [Миграция 7 → 8](MIGRATION.md).

--------
## Использование

- [Инициализация](#инициализация)
- [Реклама с вознаграждением](#реклама-с-вознаграждением)
  - [Загрузка рекламы с вознаграждением](#загрузка-рекламы-с-вознаграждением)
  - [Показ рекламы с вознаграждением](#показ-рекламы-с-вознаграждением)
- [Межстраничная реклама](#межстраничная-реклама)
  - [Загрузка межстраничной рекламы](#загрузка-межстраничной-рекламы)
  - [Показ межстраничной рекламы](#показ-межстраничной-рекламы)
- [Реклама при открытии приложения](#реклама-при-открытии-приложения)
  - [Загрузка рекламы при открытии приложения](#загрузка-рекламы-при-открытии-приложения)
  - [Показ рекламы при открытии приложения](#показ-рекламы-при-открытии-приложения)
- [События](#события)
- [Баннеры](#баннеры)
  - [Загрузка баннера](#загрузка-баннера)
  - [Показ баннера](#показ-баннера)
  - [Повторная загрузка баннера](#повторная-загрузка-баннера)
- [InStream](#instream)
  - [Загрузка InStream](#загрузка-instream)
  - [Показ InStream](#показ-instream)
  - [Скрытие InStream](#скрытие-instream)
- [Feed](#feed)
  - [Загрузка Feed](#загрузка-feed)
  - [Показ Feed](#показ-feed)
  - [Скрытие Feed](#скрытие-feed)

### Инициализация

```javascript
document.addEventListener('deviceready', async () => {
  await YandexAds.init({
    rewardedBlockId: 'YOUR_REWARDED_BLOCK_ID',
    interstitialBlockId: 'YOUR_INTERSTITIAL_ID',
    bannerBlockId: 'YOUR_BANNER_ID',
    openAppBlockId: 'YOUR_OPEN_APP_ADS_ID',
    instreamBlockId: 'YOUR_INSTREAM_ID',
    feedBlockId: 'YOUR_FEED_ID',
    options: { // Настройки баннерной рекламы
      bannerAtTop: true, // Баннер вверху экрана; false — внизу
      bannerSize: { width: 468, height: 100 }, // Размер баннера
      // Без bannerSize баннер использует максимально доступную ширину
    },
  });
});
```

### Согласие пользователя на обработку данных
При каждом запуске приложения перед запросом рекламы передавайте выбор пользователя через `setUserConsent()`. Значение ниже — пример; используйте фактическое согласие, полученное в приложении.

```javascript
YandexAds.setUserConsent(true);
```
***
### Реклама с вознаграждением

https://github.com/maximnara/cordova-plugin-yandex-ads/assets/2614172/520052cd-48ae-4db7-b888-6344fc83b54f


#### Загрузка рекламы с вознаграждением

```javascript
YandexAds.loadRewardedVideo();
```

#### Показ рекламы с вознаграждением

```javascript
YandexAds.showRewardedVideo();
```
***
### Межстраничная реклама

https://github.com/maximnara/cordova-plugin-yandex-ads/assets/2614172/2eb27491-e36d-4b2f-bd78-3020b1d86d86

#### Загрузка межстраничной рекламы
Вызовите перед `showInterstitial`.

```javascript
YandexAds.loadInterstitial();
```
***
#### Показ межстраничной рекламы

```javascript
YandexAds.showInterstitial();
```
***
### Реклама при открытии приложения

https://github.com/maximnara/cordova-plugin-yandex-ads/assets/2614172/e6761531-05c7-4699-a996-a940a75efee9

#### Загрузка рекламы при открытии приложения
Вызовите перед `showOpenAppAds`.

```javascript
YandexAds.loadOpenAppAds();
```
***
#### Показ рекламы при открытии приложения

```javascript
YandexAds.showOpenAppAds();
```
***
### Баннеры

#### Загрузка баннера
Вызовите перед `showBanner`.

```javascript
YandexAds.loadBanner();
```
***
#### Показ баннера

```javascript
YandexAds.showBanner();
```
***
#### Повторная загрузка баннера
Если задан `bannerSize`, баннер размещается в отдельном контейнере, который изменяет доступную область web view. При повторных загрузках используйте `reloadBanner()`, чтобы сохранить контейнер и избежать скачков вёрстки.

```javascript
YandexAds.reloadBanner();
```
***
#### Скрытие баннера

```javascript
YandexAds.hideBanner();
```
***
### InStream
InStream пока поддерживается только на Android. Плагин использует встроенный файл `jc.mp4` как контент видеоплеера и поддерживает навигацию D-pad на Android TV.

#### Загрузка InStream
```javascript
YandexAds.loadInstream();
```
***

#### Показ InStream
```javascript
YandexAds.showInstream();
```
***

#### Скрытие InStream
```javascript
YandexAds.hideInstream();
```
***

### Feed
Feed пока поддерживается только на Android. Это вертикально прокручиваемая лента рекламных карточек.

#### Загрузка Feed
```javascript
YandexAds.loadFeed();
```
***

#### Показ Feed
```javascript
YandexAds.showFeed();
```
***

#### Скрытие Feed
```javascript
YandexAds.hideFeed();
```
***

### События

Имена событий определены в [JavaScript API](../../www/yandexads.js). Promise методов `load*()` подтверждает передачу запроса нативной части; результат загрузки приходит в событии `loaded` или `failedToLoad`.
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
    // leftApplication удалено: SDK 8 больше не отправляет bannerWillLeaveApplication.
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

#### Использование событий
Добавляйте обработчик до начала загрузки. В примере реклама AppOpen показывается после события `loaded`.
```javascript
window.addEventListener(YandexAds.events.openAppAds.loaded, async () => {
  await YandexAds.showOpenAppAds();
});

await YandexAds.loadOpenAppAds();
```

### Дополнительные настройки

**iOS**

Плагин автоматически добавляет встроенный список идентификаторов SKAdNetwork, рекомендованный Яндексом, в `SKAdNetworkItems` в `Info.plist` приложения, сохраняя существующие записи. Для существующих проектов переустановите плагин и пересоберите приложение.

Встроенный список актуален на 1 ноября 2026 года. При подключении других сетей и обновлении зависимостей сверяйте его с актуальным [списком SKAdNetwork и инструкцией по подключению](https://ads.yandex.com/helpcenter/ru/dev/ios/skadnetwork).

### Участие в разработке

Принимаются pull request с исправлениями, улучшением кода и новыми возможностями.

[GitHub Actions](../../.github/workflows/install-smoke.yml) проверяет чистую установку и сборку Android/iOS в pull request, при push в `master`/`main` и при ручном запуске. Логи и результаты сборки сохраняются в артефактах. Локально: `npm install`, затем `npm run test:install -- android` или `npm run test:install -- ios` со стандартным окружением платформы.
