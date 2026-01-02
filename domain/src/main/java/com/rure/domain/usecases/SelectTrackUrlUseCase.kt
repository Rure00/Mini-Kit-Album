package com.rure.domain.usecases

import com.rure.domain.entities.Track
import com.rure.domain.repositories.LocalRepository
import com.rure.domain.repositories.RemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SelectTrackUrlUseCase @Inject constructor(
    //private val downloadRepository: DownloadRepository,
    private val remoteRepository: RemoteRepository,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(track: Track) = withContext(ioDispatcher) {
        runCatching {
//            downloadRepository.hasTrack(track).getOrNull()?.url
//                ?: remoteRepository.getTrackById(track.id).getOrNull()?.url
        }.onFailure {

        }.getOrNull()
    }
}