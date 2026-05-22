package com.gamingcontroller.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val PSPFaceColors = mapOf(
    "triangle" to Color(0xFF4CAF50),
    "circle" to Color(0xFFF44336),
    "cross" to Color(0xFF2196F3),
    "square" to Color(0xFFE91E63)
)

@Composable
fun FaceButton(
    modifier: Modifier = Modifier,
    label: String,
    color: Color,
    onPressed: (Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(if (isPressed) 2.dp else 6.dp, CircleShape)
            .clip(CircleShape)
            .background(if (isPressed) color else color.copy(alpha = 0.75f))
            .border(1.5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressed(true)
                        tryAwaitRelease()
                        isPressed = false
                        onPressed(false)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ShoulderButton(
    modifier: Modifier = Modifier,
    label: String,
    onPressed: (Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(width = 80.dp, height = 36.dp)
            .shadow(if (isPressed) 1.dp else 4.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPressed) Color(0xFF1A237E) else Color(0xFF1E1E2E))
            .border(1.dp, if (isPressed) Color(0xFF3F51B5) else Color(0xFF424242), RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressed(true)
                        tryAwaitRelease()
                        isPressed = false
                        onPressed(false)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isPressed) Color(0xFF90CAF9) else Color(0xFFB0B0B0),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TriggerButton(
    modifier: Modifier = Modifier,
    label: String,
    onPressed: (Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(width = 80.dp, height = 36.dp)
            .shadow(if (isPressed) 1.dp else 4.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPressed) Color(0xFF4A148C) else Color(0xFF1E1E2E))
            .border(1.dp, if (isPressed) Color(0xFF7B1FA2) else Color(0xFF424242), RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressed(true)
                        tryAwaitRelease()
                        isPressed = false
                        onPressed(false)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isPressed) Color(0xFFCE93D8) else Color(0xFFB0B0B0),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DpadButton(
    modifier: Modifier = Modifier,
    direction: String,
    onPressed: (Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val color = if (isPressed) Color(0xFF42A5F5) else Color(0xFF37474F)

    Box(
        modifier = modifier
            .size(40.dp)
            .shadow(if (isPressed) 1.dp else 3.dp, RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            .background(color)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressed(true)
                        tryAwaitRelease()
                        isPressed = false
                        onPressed(false)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = direction,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
