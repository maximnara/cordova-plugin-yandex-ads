package io.luzh.cordova.plugin.helpers.instream.cache

import android.content.Context
import android.os.Environment
import java.io.File

class DiskCachePathProvider {

    fun getDiskCacheDirectory(context: Context, cacheDirName: String): File {
        val externalCacheDir = getExternalCacheDir(context)
        val cacheDir = externalCacheDir ?: context.cacheDir

        return File(cacheDir.path + File.separator + cacheDirName)
    }

    private fun getExternalCacheDir(context: Context): File? {
        var externalCacheDir: File? = null
        try {
            if (isExternalStorageStateAccessible()) {
                val cacheDir = context.externalCacheDir
                if (cacheDir != null && cacheDir.canWrite()) {
                    externalCacheDir = cacheDir
                }
            }
        } catch (ignore: Exception) {
        }

        return externalCacheDir
    }

    private fun isExternalStorageStateAccessible(): Boolean {
        val storageState = Environment.getExternalStorageState()
        val mounted = Environment.MEDIA_MOUNTED == storageState
        val mountedReadOnly = Environment.MEDIA_MOUNTED_READ_ONLY == storageState
        val storageRemovable = Environment.isExternalStorageRemovable()

        return mounted || storageRemovable.not() && mountedReadOnly.not()
    }
}