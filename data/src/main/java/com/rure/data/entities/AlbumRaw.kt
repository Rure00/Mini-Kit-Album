package com.rure.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rure.domain.entities.Track

@Entity(tableName = "albums")
data class AlbumRaw(
    @PrimaryKey
    val id: String,

    val title: String,
    val artist: String,
    val genre: String,

    val releaseDate: String,
    val description: String,

    val coverUrl: String,
    val tracksId: List<String>,

    val images: List<String>? = null,
    val videos: List<String>? = null,
)
