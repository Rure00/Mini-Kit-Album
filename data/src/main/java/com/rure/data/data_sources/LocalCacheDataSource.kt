package com.rure.data.data_sources

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rure.data.entities.AlbumRaw
import com.rure.data.entities.MyAlbumInRemote
import com.rure.data.entities.TrackRaw
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalCacheDataSource {
    @Query("SELECT * FROM albums")
    fun observeAlbums(): Flow<List<AlbumRaw>>
    @Query("SELECT * FROM tracks")
    fun observerTracks(): Flow<List<TrackRaw>>

    @Insert
    suspend fun insertAlbum(raw: AlbumRaw)
    @Insert
    suspend fun insertTrack(raw: TrackRaw)
    @Insert
    suspend fun registerAlbum(myAlbumInRemote: MyAlbumInRemote)

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getAlbumById(id: String): AlbumRaw?
    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: String): TrackRaw?

    @Query("DELETE FROM tracks WHERE id = :id")
    suspend fun deleteTrack(id: String): Int
}