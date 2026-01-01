package com.rure.domain.usecases

import com.rure.domain.repositories.LocalRepository
import com.rure.domain.repositories.RemoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterAlbumUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(albumCode: String) = withContext(ioDispatcher) {
        runCatching {
            val registered = remoteRepository.registerAlbum(albumCode).getOrThrow()
            localRepository.insertNewToLocalAlbums(registered).getOrThrow()
        }.getOrElse { null }
    }
}