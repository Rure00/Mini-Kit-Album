package com.rure.domain.repositories

import com.rure.domain.entities.Album
import com.rure.domain.entities.Track
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun observeAlbums(): Flow<List<Album>>
    fun observeDownloadedTrack(): Flow<List<Track>>
    suspend fun insertNewToLocalAlbums(album: Album): Result<Album>

    suspend fun saveTrack(albumId: String, track: Track): Result<Track>
    suspend fun eraseTrack(id: String, uri: String): Boolean // 해당 ID가 없어도 true return
    suspend fun getAlbumById(id: String): Result<Album>
}