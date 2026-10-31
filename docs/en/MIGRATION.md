[Русский](../ru/MIGRATION.md) | [English](MIGRATION.md) | [Documentation](README.md)

# Migration 7 → 8 version

Upgrade Yandex Mobile Ads SDK from **7.16.x to 8.6.0**.

1. Update to Cordova CLI 13+ and Cordova Android 15+. Cordova iOS 8.x is recommended.
2. Remove old Kotlin, Gradle, AGP, and Android SDK overrides from the application's `config.xml`; use the platform defaults. The plugin enables Kotlin automatically.
3. Remove any explicit iOS deployment target below **15.0**, or raise it to 15.0+. The plugin supplies 15.0 for a new project. Swift 6 language mode is not required.
4. Replace the installed plugin with the SDK 8 version and prepare the project:

```bash
cordova plugin remove cordova-plugin-yandex-ads
cordova plugin add /path/to/updated/cordova-plugin-yandex-ads
cordova prepare
cordova build android
cordova build ios --emulator
```

Use the normal Cordova build toolchain: Android SDK/JDK/Gradle, or Xcode and CocoaPods. No application helper scripts are needed. CocoaPods resolves `YandexMobileAds 8.6.0` automatically.

Remove references to `YandexAds.events.banner.leftApplication`; the event and property were deleted. Other main methods and events are retained. A `load*()` promise acknowledges the request; wait for `loaded` or `failedToLoad` before showing an ad. Feed and InStream remain Android-only.

Check ad loading, display, dismissal, rewards, and background/foreground transitions on devices. If you use separate mediation adapters or AppMetrica, align their versions with SDK 8. Review [SKAdNetwork identifiers](https://ads.yandex.com/helpcenter/en/dev/ios/skadnetwork).

Custom native code: official migration guides for [Android](https://ads.yandex.com/helpcenter/en/dev/android/release/8-0-0-migration) and [iOS](https://ads.yandex.com/helpcenter/en/dev/ios/release/8-0-0-migration).
