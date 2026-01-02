package com.rure.presentation.viewmodels

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rure.domain.entities.Track
import com.rure.domain.usecases.SelectTrackUrlUseCase
import com.rure.playback.PlaybackController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackPlayViewModel @Inject constructor(
    private val playbackController: PlaybackController,
    private val selectTrackUrlUseCase: SelectTrackUrlUseCase
): ViewModel() {
    val controller = playbackController.controller
    val playState = playbackController.playState

    init {
        playbackController.bind()
    }

    fun play(track: Track) {
        viewModelScope.launch {
            playbackController.playUrl(track.uri)
//            selectTrackUrlUseCase(track)?.let {
//                playbackController.playUrl(it.uri)
//            }
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