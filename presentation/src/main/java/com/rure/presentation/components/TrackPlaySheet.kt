package com.rure.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun TrackPlaySheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    peekHeight: Int = 280,
    content: @Composable ColumnScope.() -> Unit,
) {
    val density = LocalDensity.current
    val peekHeightDp = remember(peekHeight) { peekHeight.dp }

    // 아래로 드래그해서 닫기
    var dragOffsetPx by remember { mutableFloatStateOf(0f) }
    val closeThresholdPx = with(density) { 80.dp.toPx() }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = slideInVertically(animationSpec = tween(220)) { it },
        exit = slideOutVertically(animationSpec = tween(180)) { it },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = peekHeightDp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        dragOffsetPx = (dragOffsetPx + delta).coerceAtLeast(0f)
                    },
                    onDragStopped = {
                        if (dragOffsetPx > closeThresholdPx) onDismiss()
                        dragOffsetPx = 0f
                    }
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }
}
