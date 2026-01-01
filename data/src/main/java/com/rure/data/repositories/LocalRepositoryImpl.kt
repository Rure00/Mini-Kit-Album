package com.rure.data.repositories

import com.rure.data.data_sources.LocalDataSource
import com.rure.data.entities.AlbumRaw
import com.rure.data.entities.TrackRaw
import com.rure.data.toAlbum
import com.rure.data.toRaw
import com.rure.data.toTrack
import com.rure.domain.entities.Album
import com.rure.domain.entities.Track
import com.rure.domain.repositories.LocalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher
): LocalRepository {
    override fun observeAlbums(): Flow<List<Album>> {
        val albumsRawFlow: Flow<List<AlbumRaw>> = localDataSource.observeAlbums()
        val trackMapFlow: Flow<Map<String, Track>> = localDataSource.observerTracks()
            .map { tracks -> tracks.associate { it.id to it.toTrack() } }
            .distinctUntilChanged()

        return combine(albumsRawFlow, trackMapFlow) { albumRaw, trackMap ->
            albumRaw.map { raw ->
                raw.toAlbum(
                    tracks = raw.tracksId.mapNotNull { trackMap[it] }
                )
            }
        }.flowOn(ioDispatcher)
    }


    override suspend fun insertNewToLocalAlbums(album: Album): Result<Album> = withContext(ioDispatcher) {
        runCatching {
            localDataSource.insertAlbum(album.toRaw())
            album.tracks.map {
                async { localDataSource.insertTrack(it.toRaw()) }
            }.awaitAll()
            album
        }
    }

    override suspend fun saveTrack(albumId: String, track: Track): Result<Track> = withContext(ioDispatcher) {
        runCatching {
            localDataSource.insertTrack(track.toRaw())
            track
        }
    }

    override suspend fun eraseTrack(id: String): Boolean = withContext(ioDispatcher) {
        runCatching {
            localDataSource.deleteTrack(id)
        }.getOrElse { false }
    }

    override suspend fun getAlbumById(id: String): Result<Album> = withContext(ioDispatcher) {
        runCatching {
            val raw = localDataSource.getAlbumById(id)
            val tracks = raw?.tracksId!!.map {
                async { localDataSource.getTrackById(it)!!.toTrack() }
            }.awaitAll()

            raw.toAlbum(tracks)
        }
    }
}