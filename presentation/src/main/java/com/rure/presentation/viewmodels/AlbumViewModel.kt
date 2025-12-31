package com.rure.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rure.domain.entities.Album
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

): ViewModel() {
    private val _uiResult = MutableStateFlow<UiResult>(UiResult.Idle)
    val uiResult = _uiResult.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(listOf())
    val album = _albums.asStateFlow()



    init {
        viewModelScope.launch {
            // TODO: usecase 연결하기
            flow<List<Album>> {  }.collectLatest {
                _albums.value = it
            }
        }
    }

    fun emitAlbumIntent(intent: AlbumIntent) {
        _uiResult.value = UiResult.Loading

        viewModelScope.launch {
            delay(1000)
            when(intent) {
                is AlbumIntent.ObserverMyAlbums -> {

                }
                is AlbumIntent.DownloadAlbum -> {

                }
                is AlbumIntent.DownloadTrack -> {

                }
                is AlbumIntent.EraseAlbum -> {

                }
                is AlbumIntent.EraseTrack -> {

                }
                is AlbumIntent.RegisterAlbum -> {

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