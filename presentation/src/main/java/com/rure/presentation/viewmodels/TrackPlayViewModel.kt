package com.rure.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rure.domain.entities.Track
import com.rure.domain.usecases.ObserveDownloadedTrackUseCase
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
    private val observeDownloadedTrackUseCase: ObserveDownloadedTrackUseCase
): ViewModel() {
    val controller = playbackController.controller
    val playState = playbackController.playState

    private val _downloadTracks = MutableStateFlow<List<Track>>(listOf())
    val downloadTracks = _downloadTracks.asStateFlow()

    init {
        viewModelScope.launch {
            observeDownloadedTrackUseCase().collectLatest {
                _downloadTracks.value = it
            }
        }
    }

    fun play(track: Track) {
        viewModelScope.launch {
            _downloadTracks.value.find { it.id == track.id }?.let {
                playbackController.playUrl(it.uri)
            } ?: playbackController.playUrl(track.uri)
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