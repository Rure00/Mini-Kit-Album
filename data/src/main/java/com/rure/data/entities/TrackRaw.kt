package com.rure.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tracks")
data class TrackRaw(
    @PrimaryKey
    val id: String,
    val albumId: String,
    val title: String,
    val uri: String,
    val durationSec: Int,
): Parcelable
