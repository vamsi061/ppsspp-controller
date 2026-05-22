package com.gamingcontroller.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToInt

private fun Float.clamp(min: Float, max: Float): Float {
    return if (this < min) min else if (this > max) max else this
}

@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF424242),
    thumbColor: Color = Color(0xFF1565C0),
    deadzone: Float = 0.1f,
    sensitivity: Float = 1.0f,
    onPositionChanged: (Float, Float) -> Unit = { _, _ -> }
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val baseSizePx = constraints.maxWidth.toFloat()
        val thumbSizePx = baseSizePx * 0.45f
        val maxOffset = (baseSizePx - thumbSizePx) / 2
        val baseDp = (baseSizePx / density.density).dp

        Box(
            modifier = Modifier
                .size(baseDp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color(0x1A000000)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(baseDp * 0.92f)
                    .clip(CircleShape)
                    .background(baseColor.copy(alpha = 0.35f))
                    .border(1.5.dp, baseColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(baseDp * 0.6f)
                        .clip(CircleShape)
                        .background(baseColor.copy(alpha = 0.2f))
                        .border(1.dp, baseColor.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size((thumbSizePx / density.density).dp)
                            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(thumbColor)
                            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        offsetX = 0f
                                        offsetY = 0f
                                        onPositionChanged(0f, 0f)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val newOffsetX = (offsetX + dragAmount.x).clamp(-maxOffset, maxOffset)
                                        val newOffsetY = (offsetY + dragAmount.y).clamp(-maxOffset, maxOffset)
                                        offsetX = newOffsetX
                                        offsetY = newOffsetY

                                        val normalizedX = if (maxOffset != 0f) offsetX / maxOffset else 0f
                                        val normalizedY = if (maxOffset != 0f) -(offsetY / maxOffset) else 0f

                                        if (abs(normalizedX) > deadzone || abs(normalizedY) > deadzone) {
                                            onPositionChanged(normalizedX * sensitivity, normalizedY * sensitivity)
                                        } else {
                                            onPositionChanged(0f, 0f)
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size((thumbSizePx * 0.4f / density.density).dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleJoystick(
    modifier: Modifier = Modifier,
    onPositionChanged: (Float, Float) -> Unit = { _, _ -> }
) {
    VirtualJoystick(
        modifier = modifier,
        onPositionChanged = onPositionChanged
    )
}
