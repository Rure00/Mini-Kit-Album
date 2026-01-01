package com.rure.playback

import android.app.Activity
import android.app.Application
import android.view.View
import androidx.media3.session.MediaController
import kotlinx.coroutines.flow.StateFlow

interface PlaybackController {
    val controllerFlow: StateFlow<MediaController?>

    fun bind()
    fun unbind()

    fun playUrl(url: String)
}