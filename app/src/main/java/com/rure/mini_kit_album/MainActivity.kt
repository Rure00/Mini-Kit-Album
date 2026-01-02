package com.rure.mini_kit_album

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rure.presentation.navigation.ScreenNavigator
import com.rure.presentation.ui.theme.MiniKitAlbumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniKitAlbumTheme {
                ScreenNavigator()
            }
        }
    }
}