package io.luzh.cordova.plugin.helpers.instream.creator

import android.content.Context
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.Util
import io.luzh.cordova.plugin.helpers.instream.cache.DiskCacheProvider

class MediaSourceCreator(
    private val context: Context,
) {

    fun createMediaSource(streamUrl: String): MediaSource {
        val cache = DiskCacheProvider.getCache(context)
        val adMediaItem = MediaItem.fromUri(streamUrl)
        val userAgent = Util.getUserAgent(context, context.packageName)
        val defaultDataSourceFactory = DefaultDataSourceFactory(context, userAgent)
        val adPlayerCacheFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(defaultDataSourceFactory)

        return DefaultMediaSourceFactory(adPlayerCacheFactory).createMediaSource(adMediaItem)
    }
}