package com.rure.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rure.domain.entities.Album
import com.rure.domain.usecases.GetAlbumDetailUseCase
import com.rure.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val getAlbumDetailUseCase: GetAlbumDetailUseCase,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val id = savedStateHandle.toRoute<Destination.Album>().id

    private val _selectedAlbum = MutableStateFlow<Album?>(null)
    val selectedAlbum = _selectedAlbum.asStateFlow()

    init {
        viewModelScope.launch {
            getAlbumDetailUseCase(id).collectLatest { flow ->
                _selectedAlbum.value = flow.find { it.id == id }
            }
        }
    }
}