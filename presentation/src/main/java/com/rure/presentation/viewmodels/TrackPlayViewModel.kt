package com.rure.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.rure.playback.PlaybackController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrackPlayViewModel @Inject constructor(
    private val playbackController: PlaybackController
): ViewModel() {
    val controllerFlow = playbackController.controllerFlow

    init {
        playbackController.bind()
    }

    fun play(url: String) = playbackController.playUrl(url)

    override fun onCleared() {
        playbackController.unbind()
    }
}