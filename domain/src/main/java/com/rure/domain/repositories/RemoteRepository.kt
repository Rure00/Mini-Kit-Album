package com.rure.domain.repositories

import com.rure.domain.entities.Album
import com.rure.domain.entities.Track

interface RemoteRepository {
    suspend fun getAlbumByCode(albumCode: String): Result<Album>
    suspend fun getTrackById(trackId: String): Result<Track>
    suspend fun registerAlbum(albumCode: String): Result<Album>

}