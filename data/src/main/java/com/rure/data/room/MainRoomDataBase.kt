package com.rure.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rure.data.data_sources.LocalDataSource
import com.rure.data.entities.AlbumRaw
import com.rure.data.entities.MyAlbumInRemote
import com.rure.data.entities.TrackRaw

@Database(entities = [AlbumRaw::class, TrackRaw::class, MyAlbumInRemote::class], version = 1)
abstract class MainRoomDataBase: RoomDatabase() {
    abstract val localDataSource: LocalDataSource
}