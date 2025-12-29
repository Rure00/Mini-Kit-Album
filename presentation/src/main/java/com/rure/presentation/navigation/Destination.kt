package com.rure.presentation.navigation

sealed class Destination(
    val label: String,
    val route: String,
) {
    data object Home : Destination(
        "Home", "home",
    )

    data object Library : Destination(
        "Library", "library",
    )

    data object Album : Destination(
        "Album", "album",
    )
}