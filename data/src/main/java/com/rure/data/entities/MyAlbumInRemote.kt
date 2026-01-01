package com.rure.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_albums")
data class MyAlbumInRemote(
    @PrimaryKey
    val albumId: String,
    val trackId: List<String>,
)
