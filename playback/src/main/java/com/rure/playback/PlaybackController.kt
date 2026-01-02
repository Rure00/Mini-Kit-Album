package com.rure.playback

import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.StateFlow

interface PlaybackController {
    val controller: StateFlow<MediaController?>
    val playState: StateFlow<PlayState>

    fun bind()
    fun unbind()

    fun playUrl(url: String)
    fun pause()
}