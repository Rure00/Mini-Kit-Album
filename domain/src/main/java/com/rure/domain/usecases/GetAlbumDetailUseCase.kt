package com.rure.domain.usecases

import com.rure.domain.repositories.LocalRepository
import com.rure.domain.repositories.RemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAlbumDetailUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(albumId: String) = withContext(ioDispatcher) {
        runCatching {
            val onLocal = localRepository.getAlbumById(albumId).getOrNull()
            if (onLocal == null) {
                val newOne = remoteRepository.getAlbumByCode(albumId).getOrThrow()
                localRepository.insertNewToLocalAlbums(newOne).getOrThrow()
            } else {
                onLocal
            }
        }.getOrElse { null }
    }
}