package com.rure.presentation.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rure.presentation.ui.theme.Black
import com.rure.presentation.ui.theme.White

@Composable
fun ScreenNavigator() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val screen = backStackEntry?.destination.toDestination() ?: Destination.Home

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "main/",
            modifier = Modifier
                .background(Black)
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            mainNavGraph(navController)
        }
    }
}

private fun NavDestination?.toDestination(): Destination? {
    if (this == null) return null

    val routes = hierarchy.mapNotNull { it.route }.toList()

    return when {
        routes.any { it.startsWith(Destination.Home.route) } -> Destination.Home
        routes.any { it.startsWith(Destination.Library.route) } -> Destination.Library
        routes.any { it.startsWith(Destination.Album.route) } -> Destination.Album
        else -> null
    }
}