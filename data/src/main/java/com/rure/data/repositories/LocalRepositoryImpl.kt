package com.rure.data.repositories

import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "LocalRepositoryImpl"

class LocalRepositoryImpl @Inject constructor(
    private val localCacheDataSource: LocalCacheDataSource,
    private val downloadDataSource: DownloadDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    private val applicationScope: CoroutineScope,
): LocalRepository {

    init {
        Log.d("UpdateErrorFind", "LocalRepositoryImpl Init")
    }

    private val downloadedTrackFlow = downloadDataSource.observerTracks().map { list ->
        list.associateBy { it.id }
    }.stateIn(applicationScope, WhileSubscribed(5000), emptyMap())

    private val cachedTrackRawFlow = combine(downloadedTrackFlow,localCacheDataSource.observerTracks() ) { down, raw ->
        Log.d(TAG, "observeAlbums: ${down.toList().joinToString { it.first }}")
        raw.associate {
            it.id to it.toTrack(down.containsKey(it.id))
        }.also {
            Log.d(TAG, "observeAlbums: ${it.toList().joinToString { "${it.second.title}: ${it.second.downloaded}" }}")
        }
    }.stateIn(applicationScope, WhileSubscribed(5000), emptyMap())

    private val cachedAlbumRawFlow = combine(localCacheDataSource.observeAlbums(), cachedTrackRawFlow) { albumRaws, trackMap ->
        albumRaws.map { raw ->
            val tracksForAlbum = raw.tracksId.mapNotNull { trackMap[it] }
            raw.toAlbum(tracks = tracksForAlbum)
        }
    }.stateIn(applicationScope, WhileSubscribed(5000), emptyList())


    override fun observeAlbums(): StateFlow<List<Album>> = cachedAlbumRawFlow

    override suspend fun insertNewToLocalAlbums(album: Album): Result<Album> = withContext(ioDispatcher) {
        runCatching {
            localCacheDataSource.insertAlbum(album.toRaw())
            album.tracks.map {
                async { localCacheDataSource.insertTrack(it.toRaw()) }
            }.awaitAll()
            album
        }
    }

    override suspend fun saveTrack(albumId: String, track: Track): Result<Track> = withContext(ioDispatcher) {
        runCatching {
            val downloaded = downloadDataSource.saveMp3(track.toRaw()).getOrThrow()

            // Update Local Cache
            localCacheDataSource.insertTrack(track.toRaw().copy(uri = downloaded.uri))

            track
        }
    }

    override suspend fun eraseTrack(id: String, uri: String): Boolean = withContext(ioDispatcher) {
        runCatching {
            //downloadDataSource.removeMp3(uri)

            // Update Local Cache
            //localCacheDataSource.deleteTrack(id)


            true
        }.getOrElse { false }
    }

    override suspend fun getAlbumById(id: String): Result<Album> = withContext(ioDispatcher) {
        runCatching {
            val raw = localCacheDataSource.getAlbumById(id)
            val downloaded = downloadDataSource.observerTracks().map { tracks ->
                tracks.associateBy { it.id }
            }.first()

            // TODO: 로컬에 Track이 없다면 ?? 리팩토링 하기...
            val tracks = raw?.tracksId!!.map {
                async { localCacheDataSource.getTrackById(it)!!.toTrack(downloaded.containsKey(it)) }
            }.awaitAll()

            raw.toAlbum(tracks)
        }
    }
}