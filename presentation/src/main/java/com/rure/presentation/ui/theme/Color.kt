package com.rure.presentation.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF000000)
val White  = Color(0xFFFFFFFF)
val LightGray = Color(0xFFD3D3D3)

val primary = Color(0xFFcf22c5)
val secondary = Color(0xFF857ad0)
val surface = Color(0xFF18122b)

//val mainGradient = listOf(Color(0xFFb227e7), primary, Color(0xFF857ad0), White)
val mainGradient = listOf(Color(0xFFb129e7), Color(0xFFf307b2))

val mainGradientBrush = Brush.linearGradient(
    colors = mainGradient,
    start = Offset.Zero,
    end = Offset.Infinite
)


