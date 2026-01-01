package com.rure.presentation.navigation

import androidx.annotation.OptIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rure.presentation.components.TrackPlaySheet
import com.rure.presentation.ui.theme.Black
import com.rure.presentation.ui.theme.White
import com.rure.presentation.viewmodels.TrackPlayViewModel

@OptIn(UnstableApi::class)
@Composable
fun ScreenNavigator(
    trackPlayViewModel: TrackPlayViewModel = hiltViewModel()
) {
    val controller by trackPlayViewModel.controllerFlow.collectAsState()
    val navController = rememberNavController()

    var openControlView by remember { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding),
        ) {
            NavHost(
                navController = navController,
                startDestination = Destination.Home,
                modifier = Modifier
                    .background(Black)
                    .fillMaxSize()
                    //.padding(innerPadding)
            ) {
                mainNavGraph(navController)
            }

            val boxHeight = 96
            TrackPlaySheet(
                visible = openControlView, //controller?.isPlaying ?: false,
                onDismiss = { openControlView = false },
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                peekHeight = boxHeight
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp),
                    factory = { ctx ->
                        PlayerControlView(ctx).apply {
                            showTimeoutMs = 0               // 항상 보이게
                            setShowNextButton(false)
                            setShowPreviousButton(false)
                            setShowShuffleButton(false)
                            showSubtitleButton = false
                            showVrButton = false
                            setShowFastForwardButton(true)  // 원하면 false
                            setShowRewindButton(true)
                        }
                    },
                    update = { it.player = controller }
                )
            }
        }
    }
}