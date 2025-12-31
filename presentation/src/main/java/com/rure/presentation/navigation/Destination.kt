package com.rure.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination(
    val label: String,
    val route: String,
) {
    @Serializable
    data object Home : Destination(
        "Home", "home",
    )

    @Serializable
    data object Library : Destination(
        "Library", "library",
    )

    @Serializable
    data class Album(val id: String) : Destination(
        "Album", "album",
    )
}