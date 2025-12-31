package com.rure.domain.usecases

import com.rure.domain.repositories.RemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject


class DownloadAlbumUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val downloadTrackUseCase: DownloadTrackUseCase,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(albumCode: String) = withContext(ioDispatcher) {
        runCatching {
            val album = remoteRepository.getAlbumByCode(albumCode).getOrThrow()
            album.tracks.map {
                async { downloadTrackUseCase(albumCode, it.id) }
            }.awaitAll()

            album
        }.getOrElse { null }
    }
}