package com.rure.domain.usecases

import com.rure.domain.entities.Track
import com.rure.domain.repositories.LocalRepository
import com.rure.domain.repositories.RemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ObserveDownloadedTrackUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    suspend operator fun invoke() = localRepository.observeDownloadedTrack()
}