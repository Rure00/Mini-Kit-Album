package com.rure.data.entities

import com.rure.domain.entities.Track

data class AlbumRaw(
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
