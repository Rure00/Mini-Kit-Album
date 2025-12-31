package com.rure.domain.repositories

import com.rure.domain.entities.Album
import com.rure.domain.entities.Track
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun observeAlbums(): Flow<List<Album>>
    suspend fun insertNewToLocalAlbums(album: Album): Result<Album>

    suspend fun saveTrack(albumId: String, track: Track): Result<Track>
    suspend fun eraseTrack(id: String): Boolean // TODO: 해당 ID가 없어도 true return
}