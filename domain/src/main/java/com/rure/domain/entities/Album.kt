package com.rure.domain.entities

data class Album(
    val id: String,

    val title: String,
    val artist: String,
    val genre: String,

    val releaseDate: String, // ISO "yyyy-MM-dd" 가정
    val description: String,

    val coverUrl: String,
    val tracks: List<Track>,

    val images: List<String>? = null,
    val videos: List<String>? = null,
)
