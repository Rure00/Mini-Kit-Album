package com.rure.mini_kit_album.di


import android.content.Context
import androidx.room.Room
import com.rure.data.room.MainRoomDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonProvideModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): MainRoomDataBase {
        return  Room.databaseBuilder(
            context,
            MainRoomDataBase::class.java,
            "app_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(roomDataBase: MainRoomDataBase) = roomDataBase.localDataSource

    @Provides
    @Singleton
    fun provideIoDispatcher() = Dispatchers.IO
}