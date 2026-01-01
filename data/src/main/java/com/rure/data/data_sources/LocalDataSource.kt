package com.rure.data.data_sources

import com.rure.data.entities.AlbumRaw
import com.rure.data.entities.TrackRaw
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun observeAlbums(): Flow<List<AlbumRaw>>
    fun observerTracks(): Flow<List<TrackRaw>>

    suspend fun insertAlbum(raw: AlbumRaw)
    suspend fun insertTrack(raw: TrackRaw)

    suspend fun getAlbumById(id: String): AlbumRaw?
    suspend fun getTrackById(id: String): TrackRaw?

    suspend fun deleteTrack(id: String): Boolean
}