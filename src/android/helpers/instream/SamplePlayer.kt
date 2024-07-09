package io.luzh.cordova.plugin.helpers.instream

interface SamplePlayer {

    fun isPlaying(): Boolean

    fun resume()

    fun pause()
}