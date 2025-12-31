package com.rure.domain.usecases

import com.rure.domain.repositories.LocalRepository
import com.rure.domain.repositories.RemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "DownloadTrackUseCase"

class DownloadTrackUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(albumId: String, trackId: String) = withContext(ioDispatcher) {
        runCatching {
            val track = remoteRepository.getTrackById(trackId)
            localRepository.saveTrack(albumId, track.getOrNull()!!)
            track.getOrNull()
        }.getOrElse { null }
    }
}