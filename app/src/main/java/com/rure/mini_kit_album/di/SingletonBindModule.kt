package com.rure.mini_kit_album.di

import com.rure.data.repositories.LocalRepositoryImpl
import com.rure.data.repositories.RemoteRepositoryImpl
import com.rure.domain.repositories.LocalRepository
import com.rure.domain.repositories.RemoteRepository
import com.rure.playback.PlaybackController
import com.rure.playback.PlaybackControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonBindModule {
    @Binds
    @Singleton
    abstract fun bindLocalRepository(
        localRepository: LocalRepositoryImpl
    ): LocalRepository

    @Binds
    @Singleton
    abstract fun bindRemoteRepository(
        remoteRepository: RemoteRepositoryImpl
    ): RemoteRepository

    @Binds
    @Singleton
    abstract fun bindPlaybackController(
        impl: PlaybackControllerImpl
    ): PlaybackController
}