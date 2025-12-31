package com.rure.presentation.states

import com.rure.domain.entities.Album
import com.rure.domain.entities.Track


sealed class AlbumIntent {
    data class RegisterAlbum(val code: String): AlbumIntent()

    data class DownloadAlbum(val album: Album): AlbumIntent()
    data class DownloadTrack(val album: Album, val track: Track): AlbumIntent()

    data class EraseAlbum(val album: Album): AlbumIntent()
    data class EraseTrack(val track: Track): AlbumIntent()
}