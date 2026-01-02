package com.rure.playback

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerView
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "PlaybackControllerImpl"

class PlaybackControllerImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val applicationScope: CoroutineScope
): PlaybackController {
    private val _controller = MutableStateFlow<MediaController?>(null)
    private var controllerFuture: ListenableFuture<MediaController>? = null


    override var controller = _controller.asStateFlow()
    override var playState: MutableStateFlow<PlayState> = MutableStateFlow(PlayState.Loading)



    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) = refresh(controller.value)
        override fun onPlaybackStateChanged(playbackState: Int) = refresh(controller.value)
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) = refresh(controller.value)
    }

    private fun refresh(player: Player?) {
        playState.value = when {
            player == null -> PlayState.Loading
            player.playbackState == Player.STATE_IDLE -> PlayState.Idle
            player.isPlaying -> PlayState.Playing
            else -> PlayState.Paused
        }
    }


    @OptIn(UnstableApi::class)
    override fun bind() {
        if (_controller.value != null || controllerFuture != null) return

        appContext.startService(Intent(appContext, PlaybackService::class.java))

        val token = SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
        val future = MediaController.Builder(appContext, token).buildAsync()
        controllerFuture = future

        future.addListener(
            {
                runCatching { future.get() }
                    .onSuccess {
                        _controller.value = it
                        _controller.value?.addListener(listener)
                    }
                    .onFailure { controllerFuture = null }
            },
            MoreExecutors.directExecutor()
        )


    }

    override fun unbind() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null

        _controller.value?.release()
        _controller.value = null

        _controller.value?.removeListener(listener)
    }

    override fun playUrl(url: String) {
        _controller.value?.apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            play()
        }
    }

    override fun pause() {
        runCatching {
            _controller.value?.pause()
        }

    }
}