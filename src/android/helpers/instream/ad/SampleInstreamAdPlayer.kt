package io.luzh.cordova.plugin.helpers.instream.ad

import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayer
import com.yandex.mobile.ads.instream.player.ad.InstreamAdPlayerListener
import com.yandex.mobile.ads.instream.player.ad.InstreamAdMimeTypes
import com.yandex.mobile.ads.instream.player.ad.error.InstreamAdPlayerError
import com.yandex.mobile.ads.video.playback.model.VideoAd
import io.luzh.cordova.plugin.helpers.instream.SamplePlayer

class SampleInstreamAdPlayer(
    private val exoPlayerView: PlayerView,
    private val eventListener: InstreamAdPlayerListener,
) : InstreamAdPlayer, SamplePlayer {

    private val adPlayers = mutableMapOf<VideoAd, SampleVideoAdPlayer>()

    private var currentVideoAd: VideoAd? = null
    private var adPlayerListener: InstreamAdPlayerListener? = null

    // The SDK installs its own player listener when binding. Keep Cordova events
    // as a separate observer so neither listener replaces the other.
    private val playerListener = object : InstreamAdPlayerListener {
        override fun onAdPrepared(videoAd: VideoAd) {
            adPlayerListener?.onAdPrepared(videoAd)
            eventListener.onAdPrepared(videoAd)
        }

        override fun onAdStarted(videoAd: VideoAd) {
            adPlayerListener?.onAdStarted(videoAd)
            eventListener.onAdStarted(videoAd)
        }

        override fun onAdPaused(videoAd: VideoAd) {
            adPlayerListener?.onAdPaused(videoAd)
            eventListener.onAdPaused(videoAd)
        }

        override fun onAdResumed(videoAd: VideoAd) {
            adPlayerListener?.onAdResumed(videoAd)
            eventListener.onAdResumed(videoAd)
        }

        override fun onAdBufferingStarted(videoAd: VideoAd) {
            adPlayerListener?.onAdBufferingStarted(videoAd)
            eventListener.onAdBufferingStarted(videoAd)
        }

        override fun onAdBufferingFinished(videoAd: VideoAd) {
            adPlayerListener?.onAdBufferingFinished(videoAd)
            eventListener.onAdBufferingFinished(videoAd)
        }

        override fun onAdCompleted(videoAd: VideoAd) {
            adPlayerListener?.onAdCompleted(videoAd)
            eventListener.onAdCompleted(videoAd)
        }

        override fun onAdStopped(videoAd: VideoAd) {
            adPlayerListener?.onAdStopped(videoAd)
            eventListener.onAdStopped(videoAd)
        }

        override fun onAdSkipped(videoAd: VideoAd) {
            adPlayerListener?.onAdSkipped(videoAd)
            eventListener.onAdSkipped(videoAd)
        }

        override fun onVolumeChanged(videoAd: VideoAd, volume: Float) {
            adPlayerListener?.onVolumeChanged(videoAd, volume)
            eventListener.onVolumeChanged(videoAd, volume)
        }

        override fun onError(videoAd: VideoAd, error: InstreamAdPlayerError) {
            adPlayerListener?.onError(videoAd, error)
            eventListener.onError(videoAd, error)
        }
    }

    // Keep the custom player's existing progressive video formats.
    override val supportedMimeTypes = listOf(InstreamAdMimeTypes.MP4, InstreamAdMimeTypes.WEBM)

    override fun bindPlayerView(container: FrameLayout) {
        (exoPlayerView.parent as? ViewGroup)?.removeView(exoPlayerView)
        container.addView(exoPlayerView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
    }

    override fun setInstreamAdPlayerListener(instreamAdPlayerListener: InstreamAdPlayerListener?) {
        adPlayerListener = instreamAdPlayerListener
    }

    override fun prepareAd(videoAd: VideoAd) {
        val videoAdPlayer = SampleVideoAdPlayer(videoAd, exoPlayerView)
        adPlayers[videoAd] = videoAdPlayer

        videoAdPlayer.setInstreamAdPlayerListener(playerListener)
        videoAdPlayer.prepareAd()
    }

    override fun playAd(videoAd: VideoAd) {
        currentVideoAd = videoAd
        adPlayers[videoAd]?.playAd()
    }

    override fun pauseAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.pauseAd()
    }

    override fun resumeAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.resumeAd()
    }

    override fun stopAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.stopAd()
    }

    override fun skipAd(videoAd: VideoAd) {
        adPlayers[videoAd]?.skipAd()
    }

    override fun setVolume(videoAd: VideoAd, volume: Float) {
        adPlayers[videoAd]?.setVolume(volume)
    }

    override fun getVolume(videoAd: VideoAd): Float {
        return adPlayers[videoAd]?.getVolume() ?: DEFAULT_VOLUME
    }

    override fun getAdDuration(videoAd: VideoAd): Long {
        return adPlayers[videoAd]?.adDuration ?: 0
    }

    override fun getAdPosition(videoAd: VideoAd): Long {
        return adPlayers[videoAd]?.adPosition ?: 0
    }

    override fun isPlayingAd(videoAd: VideoAd): Boolean {
        return adPlayers[videoAd]?.isPlayingAd ?: false
    }

    override fun releaseAd(videoAd: VideoAd) {
        if(videoAd == currentVideoAd) {
            currentVideoAd = null
        }

        adPlayers[videoAd]?.let { videoAdPlayer ->
            videoAdPlayer.setInstreamAdPlayerListener(null)
            videoAdPlayer.releaseAd()
        }

        adPlayers.remove(videoAd)
    }

    fun release() {
        val players = adPlayers.values.toList()
        adPlayers.clear()
        adPlayerListener = null
        currentVideoAd = null
        players.forEach {
            it.setInstreamAdPlayerListener(null)
            it.releaseAd()
        }
    }

    override fun isPlaying(): Boolean {
        return adPlayers[currentVideoAd]?.isPlayingAd ?: false
    }

    override fun resume() {
        adPlayers[currentVideoAd]?.resumeAd()
    }

    override fun pause() {
        adPlayers[currentVideoAd]?.pauseAd()
    }

    private companion object {
        private const val DEFAULT_VOLUME = 0f
    }
}
