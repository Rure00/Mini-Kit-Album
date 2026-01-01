package com.rure.data.repositories

import com.rure.data.data_sources.RemoteDataSource
import com.rure.domain.entities.Album
import com.rure.domain.entities.Track
import com.rure.domain.repositories.RemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
): RemoteRepository {
    override suspend fun getAlbumByCode(albumCode: String): Result<Album> = withContext(ioDispatcher) {
        runCatching {
            remoteDataSource.getAlbumByCode(albumCode)
        }
    }

    override suspend fun getTrackById(trackId: String): Result<Track> = withContext(ioDispatcher) {
        runCatching {
            remoteDataSource.getTrackById(trackId)
        }
    }

    override suspend fun registerAlbum(albumCode: String): Result<Album> = withContext(ioDispatcher) {
        runCatching {
            if (remoteDataSource.registerAlbum(albumCode)) {
                getAlbumByCode(albumCode).getOrThrow()
            } else {
                throw Exception("Failed to register album")
            }
        }
    }
}