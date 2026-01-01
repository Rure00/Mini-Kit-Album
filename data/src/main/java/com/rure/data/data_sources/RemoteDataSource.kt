package com.rure.data.data_sources

import com.rure.domain.entities.Album
import com.rure.domain.entities.Track

interface RemoteDataSource {
    suspend fun getAlbumByCode(albumCode: String): Album
    suspend fun getTrackById(trackId: String): Track
    suspend fun registerAlbum(albumCode: String): Boolean
}