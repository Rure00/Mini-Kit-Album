package com.rure.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rure.domain.entities.Album
import com.rure.domain.usecases.DownloadUseCase
import com.rure.domain.usecases.EraseUseCase
import com.rure.domain.usecases.ObserveLocalAlbumsUseCase
import com.rure.domain.usecases.RegisterAlbumUseCase
import com.rure.presentation.states.AlbumIntent
import com.rure.presentation.states.UiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val observeLocalAlbumsUseCase: ObserveLocalAlbumsUseCase,
    private val registerAlbumUseCase: RegisterAlbumUseCase,
    private val downloadUseCase: DownloadUseCase,
    private val eraseUseCase: EraseUseCase
): ViewModel() {
    private val _uiResult = MutableStateFlow<UiResult>(UiResult.Idle)
    val uiResult = _uiResult.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(listOf())
    val album = _albums.asStateFlow()

    init {
        viewModelScope.launch {
            observeLocalAlbumsUseCase().collectLatest {
                _albums.value = it
            }
        }
    }

    fun emitAlbumIntent(intent: AlbumIntent) {
        _uiResult.value = UiResult.Loading

        viewModelScope.launch {
            delay(1000)
            when(intent) {
                is AlbumIntent.RegisterAlbum -> {
                    registerAlbumUseCase(intent.code)
                }
                is AlbumIntent.DownloadAlbum -> {
                    downloadUseCase(intent.album.id)
                }
                is AlbumIntent.DownloadTrack -> {
                    downloadUseCase(intent.album.id, intent.track.id)
                }
                is AlbumIntent.EraseAlbum -> {
                    eraseUseCase(intent.album)
                }
                is AlbumIntent.EraseTrack -> {
                    eraseUseCase(intent.track)
                }
            }

            _uiResult.value = UiResult.Idle
        }
    }

    fun searchAlbums(query: String): List<Album> {
        return album.value.filter {
            it.title.contains(query) || it.artist.contains(query) || it.genre.contains(query)
        }
    }
}