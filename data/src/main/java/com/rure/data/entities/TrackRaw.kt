package com.rure.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackRaw(
    @PrimaryKey
    val id: String,
    val albumId: String,
    val title: String,
    val uri: String,
    val durationSec: Int,
    val downloaded: Boolean,
)
