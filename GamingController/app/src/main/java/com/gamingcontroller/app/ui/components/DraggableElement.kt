package com.gamingcontroller.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamingcontroller.app.domain.model.LayoutElement
import kotlin.math.roundToInt

private val PrimaryColor = Color(0xFF6366F1)

@Composable
fun DraggableButton(
    element: LayoutElement.Button,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPositionChange: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var canvasWidth by remember { mutableStateOf(0) }
    var canvasHeight by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    val buttonSize = with(density) { (element.size * 100).toDp() }

    Box(
        modifier = modifier
            .size(buttonSize)
            .onSizeChanged { size ->
                canvasWidth = size.width
                canvasHeight = size.height
            }
            .offset {
                IntOffset(
                    ((element.x * canvasWidth) + offsetX).roundToInt(),
                    ((element.y * canvasHeight) + offsetY).roundToInt()
                )
            }
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, PrimaryColor, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .clip(
                when (element.shape) {
                    com.gamingcontroller.app.domain.model.ButtonShape.CIRCLE -> CircleShape
                    com.gamingcontroller.app.domain.model.ButtonShape.RECTANGLE -> RoundedCornerShape(0.dp)
                    com.gamingcontroller.app.domain.model.ButtonShape.ROUNDED_RECTANGLE -> RoundedCornerShape(8.dp)
                }
            )
            .background(
                element.color?.let { Color(android.graphics.Color.parseColor(it)) }
                    ?: Color(0xFF374151)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSelect() }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onSelect() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        val newX = ((element.x * canvasWidth + offsetX) / canvasWidth).coerceIn(0f, 1f)
                        val newY = ((element.y * canvasHeight + offsetY) / canvasHeight).coerceIn(0f, 1f)
                        onPositionChange(newX, newY)
                        offsetX = 0f
                        offsetY = 0f
                        onDragEnd()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        element.label?.let { label ->
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DraggableJoystick(
    element: LayoutElement.Joystick,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPositionChange: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var canvasWidth by remember { mutableStateOf(0) }
    var canvasHeight by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    val joystickSize = with(density) { (element.size * 100).toDp() }

    Box(
        modifier = modifier
            .size(joystickSize)
            .onSizeChanged { size ->
                canvasWidth = size.width
                canvasHeight = size.height
            }
            .offset {
                IntOffset(
                    ((element.x * canvasWidth) + offsetX).roundToInt(),
                    ((element.y * canvasHeight) + offsetY).roundToInt()
                )
            }
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, PrimaryColor, CircleShape)
                } else {
                    Modifier
                }
            )
            .clip(CircleShape)
            .background(
                element.color?.let { Color(android.graphics.Color.parseColor(it)) }
                    ?: Color(0xFF1F2937)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSelect() }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onSelect() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        val newX = ((element.x * canvasWidth + offsetX) / canvasWidth).coerceIn(0f, 1f)
                        val newY = ((element.y * canvasHeight + offsetY) / canvasHeight).coerceIn(0f, 1f)
                        onPositionChange(newX, newY)
                        offsetX = 0f
                        offsetY = 0f
                        onDragEnd()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (element.mode == com.gamingcontroller.app.domain.model.JoystickMode.MOVEMENT) "L" else "R",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DraggableDpad(
    element: LayoutElement.Dpad,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPositionChange: (Float, Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var canvasWidth by remember { mutableStateOf(0) }
    var canvasHeight by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    val dpadSize = with(density) { (element.size * 100).toDp() }
    val buttonSize = dpadSize * 0.3f

    Box(
        modifier = modifier
            .size(dpadSize)
            .onSizeChanged { size ->
                canvasWidth = size.width
                canvasHeight = size.height
            }
            .offset {
                IntOffset(
                    ((element.x * canvasWidth) + offsetX).roundToInt(),
                    ((element.y * canvasHeight) + offsetY).roundToInt()
                )
            }
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, PrimaryColor, RoundedCornerShape(4.dp))
                } else {
                    Modifier
                }
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onSelect() }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onSelect() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        val newX = ((element.x * canvasWidth + offsetX) / canvasWidth).coerceIn(0f, 1f)
                        val newY = ((element.y * canvasHeight + offsetY) / canvasHeight).coerceIn(0f, 1f)
                        onPositionChange(newX, newY)
                        offsetX = 0f
                        offsetY = 0f
                        onDragEnd()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val dpadColor = element.color?.let { Color(android.graphics.Color.parseColor(it)) }
            ?: Color(0xFF374151)

        if (element.directions.contains(com.gamingcontroller.app.domain.model.DpadDirection.UP)) {
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(4.dp))
                    .background(dpadColor)
            )
        }
        if (element.directions.contains(com.gamingcontroller.app.domain.model.DpadDirection.DOWN)) {
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(4.dp))
                    .background(dpadColor)
            )
        }
        if (element.directions.contains(com.gamingcontroller.app.domain.model.DpadDirection.LEFT)) {
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(4.dp))
                    .background(dpadColor)
            )
        }
        if (element.directions.contains(com.gamingcontroller.app.domain.model.DpadDirection.RIGHT)) {
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(4.dp))
                    .background(dpadColor)
            )
        }
        if (element.directions.contains(com.gamingcontroller.app.domain.model.DpadDirection.UP) ||
            element.directions.contains(com.gamingcontroller.app.domain.model.DpadDirection.DOWN) ||
            element.directions.contains(com.gamingcontroller.app.domain.model.DpadDirection.LEFT) ||
            element.directions.contains(com.gamingcontroller.app.domain.model.DpadDirection.RIGHT)
        ) {
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(4.dp))
                    .background(dpadColor.copy(alpha = 0.8f))
            )
        }
    }
}

@Composable
fun ResizeHandle(
    onResize: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .size(16.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .clip(RoundedCornerShape(2.dp))
            .background(PrimaryColor)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        onResize(offsetX, offsetY)
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            }
    )
}