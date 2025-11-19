/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package __PACKAGE_NAME__;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import org.apache.cordova.*;

import io.luzh.cordova.plugin.YandexAdsPlugin;

/**
 * MainActivity с поддержкой D-pad для Yandex Instream Ads на Android TV
 *
 * Этот файл автоматически копируется плагином cordova-plugin-yandex-ads
 * при установке плагина или добавлении платформы Android.
 */
public class MainActivity extends CordovaActivity
{
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // enable Cordova apps to be started in the background
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("cdvStartInBackground", false)) {
            moveTaskToBack(true);
        }

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);

        Log.d(TAG, "MainActivity created with D-pad support for Yandex Ads");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Проверяем, является ли это D-pad событием
        boolean isDpadEvent = event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER ||
                              event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP ||
                              event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN ||
                              event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT ||
                              event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT;

        if (isDpadEvent) {
            Log.d(TAG, "MainActivity: D-pad event keyCode=" + event.getKeyCode());

            // Получаем YandexAdsPlugin из pluginManager
            try {
                CordovaPlugin plugin = this.appView.getPluginManager().getPlugin("YandexAdsPlugin");
                if (plugin instanceof YandexAdsPlugin) {
                    YandexAdsPlugin yandexPlugin = (YandexAdsPlugin) plugin;
                    if (yandexPlugin.handleDpadEvent(event.getKeyCode(), event)) {
                        Log.d(TAG, "MainActivity: Event handled by plugin");
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error handling D-pad event: " + e.getMessage());
            }
        }

        // Если событие не обработано плагином, передаем дальше
        return super.dispatchKeyEvent(event);
    }
}