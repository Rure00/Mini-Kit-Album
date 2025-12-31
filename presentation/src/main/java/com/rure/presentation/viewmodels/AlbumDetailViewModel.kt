package com.rure.presentation.viewmodels

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.rure.domain.entities.Album
import com.rure.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val id = savedStateHandle.toRoute<Destination.Album>().id

    private val _selectedAlbum = MutableStateFlow<Album?>(null)
    val selectedAlbum = _selectedAlbum.asStateFlow()
}