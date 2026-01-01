package com.rure.domain.usecases

import com.rure.domain.entities.Album
import com.rure.domain.entities.Track
import com.rure.domain.repositories.LocalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EraseUseCase @Inject constructor(
    private val localRepository: LocalRepository,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(album: Album) = withContext(ioDispatcher) {
        runCatching {
            album.tracks.map {
                async { invoke(it) }
            }.awaitAll().any { !it }
        }.onFailure {
            println("EraseUseCase album Failed: ${it.message}")
        }.getOrElse { false }
    }
    suspend operator fun invoke(track: Track) = withContext(ioDispatcher) {
        runCatching {
            localRepository.eraseTrack(track.id)
        }.onFailure {
            println("EraseUseCase track Failed: ${it.message}")
        }.getOrElse { false }
    }
}