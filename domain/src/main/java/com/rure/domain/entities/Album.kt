package com.rure.domain.entities

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val genre: String,
    val cover: String,      // URL (또는 file path)
    val downloaded: Boolean,
)
