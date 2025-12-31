package com.rure.presentation.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rure.presentation.screen.AlbumScreen
import com.rure.presentation.screen.HomeScreen
import com.rure.presentation.screen.LibraryScreen

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    composable<Destination.Home> { _ ->
        HomeScreen(
            toAlbumScreen = { navController.navigate(Destination.Album(it)) },
            toLibraryScreen = { navController.navigate(Destination.Library) }
        )
    }

    composable<Destination.Library> { _ ->
        LibraryScreen(
            toAlbumScreen = { navController.navigate(Destination.Album(it)) }
        )
    }

    composable<Destination.Album> { navBackStackEntry ->
        runCatching {
            val id = navBackStackEntry.toRoute<Destination.Album>().id
            AlbumScreen(
                id = id,
                getAlbumById = { null },
                onBackToLibrary = {  }
            )
        }.onFailure {
            navController.navigateUp()
        }
    }
}