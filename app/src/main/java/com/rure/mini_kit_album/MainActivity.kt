package com.rure.mini_kit_album

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.rure.presentation.navigation.ScreenNavigator
import com.rure.presentation.ui.theme.MiniKitAlbumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniKitAlbumTheme {
                ScreenNavigator()
            }
        }

        startService(Intent(this, PlaybackService::class.java))
    }

    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, PlaybackService::class.java))
    }
}