package com.rure.data.repositories

import com.rure.data.data_sources.LocalCacheDataSource
import com.rure.data.data_sources.DownloadDataSource
import com.rure.data.entities.AlbumRaw
import com.rure.data.entities.DownloadedTrack
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
    private val localCacheDataSource: LocalCacheDataSource,
    private val downloadDataSource: DownloadDataSource,
    private val ioDispatcher: CoroutineDispatcher
): LocalRepository {
    override fun observeAlbums(): Flow<List<Album>> {
        val downloadedTrackFlow = downloadDataSource.observerTracks().map { list ->
            list.associateBy { it.id }
        }
        val trackMapFlow = combine(downloadedTrackFlow,localCacheDataSource.observerTracks() ) { down, raw ->
            raw.associate {
                it.id to it.toTrack(down.containsKey(it.id))
            }
        }
        val albumRawFlow = localCacheDataSource.observeAlbums()

        return combine(albumRawFlow, trackMapFlow) { albumRaw, trackMap ->
            val tracks = albumRaw.map { trackMap[it.id]!! }     // TODO: 로컬에 Track이 없다면 ?? 리팩토링 하기...
            albumRaw.map { raw ->
                raw.toAlbum(tracks = tracks)
            }
        }.flowOn(ioDispatcher)
    }

    override fun observeDownloadedTrack(): Flow<List<Track>> {
        val downloadedTrackFlow = downloadDataSource.observerTracks().map { list ->
            list.associateBy { it.id }
        }
        return combine(downloadedTrackFlow,localCacheDataSource.observerTracks() ) { down, raw ->
            raw.map { it.toTrack(down.containsKey(it.id)) }
        }
    }


    override suspend fun insertNewToLocalAlbums(album: Album): Result<Album> = withContext(ioDispatcher) {
        runCatching {
            localCacheDataSource.insertAlbum(album.toRaw())
            album.tracks.map {
                async { localCacheDataSource.insertTrack(it.toRaw()) }
            }.awaitAll()
            album
        }
    }

    // TODO: Download
    override suspend fun saveTrack(albumId: String, track: Track): Result<Track> = withContext(ioDispatcher) {
        runCatching {
            localCacheDataSource.insertTrack(track.toRaw())
            track
        }
    }

    // TODO: Download
    override suspend fun eraseTrack(id: String): Boolean = withContext(ioDispatcher) {
        runCatching {
            localCacheDataSource.deleteTrack(id)
            true
        }.getOrElse { false }
    }

    override suspend fun getAlbumById(id: String): Result<Album> = withContext(ioDispatcher) {
        runCatching {
            val raw = localCacheDataSource.getAlbumById(id)
            val tracks = raw?.tracksId!!.map {
                async { localCacheDataSource.getTrackById(it)!!.toTrack(false) }     // TODO: 추가
            }.awaitAll()

            raw.toAlbum(tracks)
        }
    }
}