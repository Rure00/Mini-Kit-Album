package com.rure.presentation.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.rure.presentation.screen.HomeScreen
import com.rure.presentation.screen.LibraryScreen

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    navigation(
        route = "main/",
        startDestination = Destination.Home.route
    ) {
        composable(route = Destination.Home.route) {
            HomeScreen(
                toAlbumScreen = {},
                toLibraryScreen = { navController.navigate(Destination.Library.route) }
            )
        }

        composable(route = Destination.Library.route) {
            LibraryScreen(
                { listOf() },
                {}
            )
        }

        composable(
            route = Destination.Album.route + "/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) {
            runCatching {
                val id = it.arguments?.getString("id") ?: throw  Exception("No Arguments For id.")

            }.onFailure {
                navController.navigateUp()
            }
        }
    }
}