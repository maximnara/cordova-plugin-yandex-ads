#!/usr/bin/env node

/**
 * Hook для копирования кастомной MainActivity с поддержкой D-pad
 * для Yandex Instream Ads на Android TV
 */

const fs = require('fs');
const path = require('path');

module.exports = function(context) {
    const platformRoot = path.join(context.opts.projectRoot, 'platforms/android');

    // Проверяем, существует ли платформа Android
    if (!fs.existsSync(platformRoot)) {
        console.log('Android platform not found, skipping MainActivity copy');
        return;
    }

    // Получаем имя пакета из config.xml
    const ConfigParser = context.requireCordovaModule('cordova-common').ConfigParser;
    const config = new ConfigParser(path.join(context.opts.projectRoot, 'config.xml'));
    const packageName = config.android_packageName() || config.packageName();

    if (!packageName) {
        console.error('Package name not found in config.xml');
        return;
    }

    const packagePath = packageName.replace(/\./g, '/');
    const targetDir = path.join(platformRoot, 'app/src/main/java', packagePath);
    const targetFile = path.join(targetDir, 'MainActivity.java');

    // Исходный файл MainActivity из плагина
    const sourceFile = path.join(context.opts.plugin.dir, 'src/android/MainActivity.java');

    // Проверяем, существует ли исходный файл
    if (!fs.existsSync(sourceFile)) {
        console.error('Source MainActivity.java not found at:', sourceFile);
        return;
    }

    // Читаем содержимое исходного файла
    let content = fs.readFileSync(sourceFile, 'utf8');

    // Заменяем package name на актуальный
    content = content.replace(/package\s+__PACKAGE_NAME__;/, 'package ' + packageName + ';');

    // Создаем директорию если её нет
    if (!fs.existsSync(targetDir)) {
        fs.mkdirSync(targetDir, { recursive: true });
    }

    // Записываем файл
    fs.writeFileSync(targetFile, content, 'utf8');

    console.log('✓ MainActivity with D-pad support copied to:', targetFile);
    console.log('✓ Package name:', packageName);
};