package com.rure.domain.usecases

import com.rure.domain.repositories.LocalRepository
import javax.inject.Inject

class ObserveLocalAlbumsUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    operator fun invoke() = localRepository.observeAlbums()
}