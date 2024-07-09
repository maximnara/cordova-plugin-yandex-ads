package io.luzh.cordova.plugin.utils

import android.content.Context
import kotlin.math.roundToInt

internal object ScreenUtil {

    val Context.screenWidth: Int
        get() = resources.displayMetrics.run { widthPixels / density }.roundToInt()

    val Context.screenHeight: Int
        get() = resources.displayMetrics.run { heightPixels / density }.roundToInt()
}