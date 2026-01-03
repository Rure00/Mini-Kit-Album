package com.rure.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rure.domain.entities.Track
import com.rure.playback.PlaybackController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackPlayViewModel @Inject constructor(
    private val playbackController: PlaybackController,
): ViewModel() {
    val controller = playbackController.controller
    val playState = playbackController.playState

    init {
        viewModelScope.launch {
            playbackController.bind()
        }
    }

    fun play(track: Track, onFail: () -> Unit) {
        viewModelScope.launch {
            Log.d("TrackPlayViewModel", "play: ${track.toString()}")

            runCatching {
                playbackController.playUrl(track.uri)
            }.onFailure {
                onFail()
            }
        }
    }

    fun pause() {
        viewModelScope.launch {
            playbackController.pause()
        }
    }

    override fun onCleared() {
        playbackController.unbind()
    }
}