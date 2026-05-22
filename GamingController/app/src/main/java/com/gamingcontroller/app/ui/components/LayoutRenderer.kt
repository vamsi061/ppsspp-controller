package com.gamingcontroller.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamingcontroller.app.domain.model.ButtonShape
import com.gamingcontroller.app.domain.model.DpadDirection
import com.gamingcontroller.app.domain.model.LayoutElement
import kotlin.math.roundToInt

@Composable
fun LayoutRenderer(
    layout: com.gamingcontroller.app.domain.model.ControllerLayout,
    onButtonPressed: (String, Boolean) -> Unit,
    onJoystickMoved: (String, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()

        if (maxWidthPx <= 0 || maxHeightPx <= 0) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D)),
                contentAlignment = Alignment.Center
            ) {
                Text("Invalid layout dimensions", color = Color.Red)
            }
            return@BoxWithConstraints
        }

        layout.elements.forEach { element ->
            val centerX = element.x * maxWidthPx
            val centerY = element.y * maxHeightPx
            val elementSizePx = element.size * maxWidthPx
            val elementSizeDp = (elementSizePx / density.density).dp

            val elemWidthPx = when (element) {
                is LayoutElement.Button -> (element.width ?: element.size) * maxWidthPx
                else -> elementSizePx
            }
            val left = (centerX - elemWidthPx / 2).roundToInt()
            val top = (centerY - elementSizePx / 2).roundToInt()

            when (element) {
                is LayoutElement.Button -> {
                    var isPressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.88f else 1f,
                        animationSpec = tween(durationMillis = 60),
                        label = "scale"
                    )

                    val buttonWidthPx = (element.width ?: element.size) * maxWidthPx
                    val buttonWidthDp = (buttonWidthPx / density.density).dp
                    val elementHeightDp = elementSizeDp

                    val shape = when (element.shape) {
                        ButtonShape.CIRCLE -> CircleShape
                        ButtonShape.RECTANGLE -> RoundedCornerShape(0.dp)
                        ButtonShape.ROUNDED_RECTANGLE -> RoundedCornerShape(6.dp)
                    }

                    val parsedColor = element.color?.let { parseColor(it) }
                    val isPspButton = parsedColor != null

                    val displayColor = parsedColor ?: Color(0xFF444466)
                    val brush = if (isPressed) {
                        Brush.verticalGradient(
                            colors = listOf(
                                displayColor.copy(alpha = 0.9f),
                                displayColor.copy(red = (displayColor.red * 0.7f).coerceIn(0f, 1f), alpha = 0.9f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                displayColor.copy(alpha = 0.75f),
                                displayColor.copy(alpha = 0.5f)
                            )
                        )
                    }

                    val borderBrush = if (isPressed) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.2f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.3f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        )
                    }

                    val isWide = element.width != null
                    val buttonHeight = if (isWide) elementHeightDp * 0.65f else elementHeightDp

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(left, top) }
                            .size(if (isWide) buttonWidthDp else elementHeightDp, if (isWide) buttonHeight else elementHeightDp)
                            .scale(scale)
                            .shadow(
                                if (isPressed) 2.dp else 6.dp,
                                shape,
                                ambientColor = displayColor.copy(alpha = if (isPspButton) 0.3f else 0.1f),
                                spotColor = displayColor.copy(alpha = if (isPspButton) 0.4f else 0.15f)
                            )
                            .clip(shape)
                            .background(brush, shape)
                            .border(1.5.dp, borderBrush, shape)
                            .pointerInput(element.id) {
                                detectTapGestures(
                                    onPress = {
                                        isPressed = true
                                        onButtonPressed(element.id, true)
                                        tryAwaitRelease()
                                        isPressed = false
                                        onButtonPressed(element.id, false)
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        element.label?.let { label ->
                            Text(
                                text = label,
                                color = Color.White,
                                fontSize = (buttonHeight.value * 0.5f).sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                is LayoutElement.Joystick -> {
                    val joystickColor = element.color?.let { parseColor(it) } ?: Color(0xFF444466)

                    VirtualJoystick(
                        modifier = Modifier
                            .offset { IntOffset(left, top) }
                            .size(elementSizeDp),
                        baseColor = joystickColor,
                        deadzone = layout.joystickSettings.leftDeadzone,
                        sensitivity = layout.joystickSettings.leftSensitivity,
                        onPositionChanged = { x, y ->
                            onJoystickMoved(element.id, x, y)
                        }
                    )
                }

                is LayoutElement.Dpad -> {
                    val dpadColor = element.color?.let { parseColor(it) } ?: Color(0xFF444466)
                    val dpadButtonSize = (elementSizeDp.value * 0.35f).dp
                    val dpadSpacing = (elementSizeDp.value * 0.3f).dp

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(left, top) }
                            .size(elementSizeDp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(elementSizeDp * 0.4f)
                                .shadow(4.dp, RoundedCornerShape(6.dp))
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            dpadColor.copy(alpha = 0.2f),
                                            dpadColor.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .border(1.dp, dpadColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        )

                        element.directions.forEach { direction ->
                            val (dx, dy) = when (direction) {
                                DpadDirection.UP -> 0f to -1f
                                DpadDirection.DOWN -> 0f to 1f
                                DpadDirection.LEFT -> -1f to 0f
                                DpadDirection.RIGHT -> 1f to 0f
                            }

                            val actionId = "dpad_${direction.name.lowercase()}"
                            var isPressed by remember { mutableStateOf(false) }
                            val pressedBrush = Brush.verticalGradient(
                                colors = listOf(
                                    dpadColor.copy(alpha = 0.85f),
                                    dpadColor.copy(alpha = 0.65f)
                                )
                            )
                            val normalBrush = Brush.verticalGradient(
                                colors = listOf(
                                    dpadColor.copy(alpha = 0.5f),
                                    dpadColor.copy(alpha = 0.35f)
                                )
                            )
                            val buttonBrush = if (isPressed) pressedBrush else normalBrush

                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = (dx * dpadSpacing.value).dp,
                                        y = (dy * dpadSpacing.value).dp
                                    )
                                    .size(dpadButtonSize)
                                    .shadow(if (isPressed) 1.dp else 4.dp, RoundedCornerShape(5.dp))
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(buttonBrush)
                                    .border(
                                        1.dp,
                                        Color.White.copy(alpha = if (isPressed) 0.3f else 0.12f),
                                        RoundedCornerShape(5.dp)
                                    )
                                    .pointerInput(actionId) {
                                        detectTapGestures(
                                            onPress = {
                                                isPressed = true
                                                onButtonPressed(actionId, true)
                                                tryAwaitRelease()
                                                isPressed = false
                                                onButtonPressed(actionId, false)
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                val arrow = when (direction) {
                                    DpadDirection.UP -> "\u25b2"
                                    DpadDirection.DOWN -> "\u25bc"
                                    DpadDirection.LEFT -> "\u25c0"
                                    DpadDirection.RIGHT -> "\u25b6"
                                }
                                Text(
                                    text = arrow,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = (dpadButtonSize.value * 0.45f).sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseColor(colorString: String): Color? {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        null
    }
}
