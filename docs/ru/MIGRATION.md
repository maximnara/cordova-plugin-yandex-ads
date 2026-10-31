[Русский](MIGRATION.md) | [English](../en/MIGRATION.md) | [Документация](README.md)

# Миграция 7 → 8

Переход с Yandex Mobile Ads SDK **7.16.x на 8.6.0**.

1. Обновите Cordova CLI до 13+, Cordova Android до 15+. Для iOS рекомендуется Cordova iOS 8.x.
2. Уберите старые переопределения Kotlin, Gradle, AGP и Android SDK из `config.xml` приложения: используйте настройки платформы. Kotlin плагин подключает сам.
3. Уберите явно заданный deployment target iOS ниже **15.0** либо поднимите его до 15.0+. Для нового проекта плагин задаёт 15.0 автоматически. Режим Swift 6 не требуется.
4. Замените установленную копию плагина версией с SDK 8 и подготовьте проект:

```bash
cordova plugin remove cordova-plugin-yandex-ads
cordova plugin add /path/to/updated/cordova-plugin-yandex-ads
cordova prepare
cordova build android
cordova build ios --emulator
```

Используется обычное окружение сборки Cordova: Android SDK/JDK/Gradle либо Xcode и CocoaPods. Вспомогательные скрипты приложения не нужны. CocoaPods автоматически подключает `YandexMobileAds 8.6.0`.

Удалите обращения к `YandexAds.events.banner.leftApplication`: событие и свойство удалены. Остальные основные методы и события сохранены. Promise `load*()` подтверждает запрос; перед показом дождитесь `loaded` или `failedToLoad`. Feed и InStream остаются только на Android.

Проверьте загрузку, показ, закрытие, награду и возврат из фона на устройствах. Если отдельно подключены адаптеры медиации или AppMetrica, согласуйте их версии с SDK 8. Сверьте [идентификаторы SKAdNetwork](https://ads.yandex.com/helpcenter/ru/dev/ios/skadnetwork).

Для собственного нативного кода: официальная миграция [Android](https://ads.yandex.com/helpcenter/ru/dev/android/release/8-0-0-migration) и [iOS](https://ads.yandex.com/helpcenter/ru/dev/ios/release/8-0-0-migration).
