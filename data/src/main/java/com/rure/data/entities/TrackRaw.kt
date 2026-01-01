package com.rure.data.entities

data class TrackRaw(
    val id: String,
    val albumId: String,
    val title: String,
    val uri: String,
    val durationSec: Int,
    val downloaded: Boolean,
)
