package com.rure.domain.usecases

import com.rure.domain.repositories.LocalRepository
import com.rure.domain.repositories.RemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject


class DownloadUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(albumCode: String) = withContext(ioDispatcher) {
        runCatching {
            val album = remoteRepository.getAlbumByCode(albumCode).getOrThrow()
            album.tracks.map {
                async { invoke(albumCode, it.id) }
            }.awaitAll()

            album
        }.getOrElse { null }
    }

    suspend operator fun invoke(albumId: String, trackId: String) = withContext(ioDispatcher) {
        runCatching {
            val track = remoteRepository.getTrackById(trackId)
            localRepository.saveTrack(albumId, track.getOrNull()!!)
            track.getOrNull()
        }.getOrElse { null }
    }
}