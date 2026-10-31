# Change Log
All notable changes to this project will be documented in this file.

## Unreleased

### Breaking changes

* Upgrade Yandex Mobile Ads from 7.16.x to 8.6.0 on Android and iOS.
* Require Cordova CLI 13+, Cordova Android 15+, and iOS 15+. See the [Migration 7 → 8 version guide](docs/en/MIGRATION.md) for application build preferences.
* Remove `events.banner.leftApplication`: SDK 8 no longer emits its native callback. Remove application listeners for this event.

### Changed

* Migrate Android requests, ad loaders, banners, Feed, and the custom InStream player to SDK 8 APIs.
* Migrate iOS requests, completion-based loading, banner and fullscreen delegates, and main actor isolation; initialize the SDK before resolving `init()`.
* Keep existing JavaScript methods and the remaining event names.
* Enable Kotlin inside the plugin Gradle extension, using Cordova’s Kotlin and build tool versions. Fresh projects need no application build preferences.
* Add a clean installation regression test: create an empty Cordova app, install the npm archive, and build with an unchanged config.xml.

### Fixed

* Add the SKAdNetwork list recommended by Yandex to the iOS app automatically, including the identifier required for ad loading in a fresh Cordova project.
* Remove an unused Android manifest edit that caused Cordova to fail when reinstalling the plugin.
* Emit the iOS AppOpen failure event on a failed load and serialize error event payloads as JSON objects.
* Resolve Android `hideFeed()` and run Feed operations on the UI thread.
* Release custom InStream player resources when hiding/reloading the format.
* Remove Android manifest entries for activities not supplied by the plugin; the existing MainActivity hook handles D-pad navigation.
* Use CocoaPods alone to link the iOS SDK, without a duplicate framework entry.

# Version 2.0.0

### ⚠️ BREAKING CHANGES

Check all events that you're listening to, they are changed. 

#### Added
* Yandex sdk moved to 6.x.x version
* Added app open ads format
* Now you can set banner size with just integer values, not preset values

# Version 1.1.0

#### Added
* Added setUserConsent method for GDPR

# Version 1.0.1

#### Added
* Fix banner fail to load event

# Version 1.0.0

#### Added
* Added banners support

# Version 0.0.3

#### Added
* Some small fixes.

# Version 0.0.2

#### Added
* Some small fixes.

# Version 0.0.1

#### Added
* Added interstitial, rewarded ads support. Initial commit.
