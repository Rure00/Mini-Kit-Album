package com.rure.domain.entities

data class Track(
    val id: String,
    val albumId: String,
    val title: String,
    val uri: String,
    val durationSec: Int,
    val downloaded: Boolean,
)