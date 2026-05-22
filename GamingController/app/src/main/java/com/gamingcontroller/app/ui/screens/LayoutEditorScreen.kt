package com.gamingcontroller.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamingcontroller.app.domain.model.ButtonShape
import com.gamingcontroller.app.domain.model.ControllerLayout
import com.gamingcontroller.app.domain.model.LayoutElement
import kotlin.math.roundToInt

@Composable
fun LayoutEditorScreen(
    onBack: () -> Unit,
    onSave: (ControllerLayout) -> Unit
) {
    var layoutName by remember { mutableStateOf("Custom Layout") }
    var elements by remember { mutableStateOf<List<LayoutElement>>(emptyList()) }
    var selectedElementId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0D0D),
                        Color(0xFF1A1A2E),
                        Color(0xFF0D0D0D)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141428))
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF64B5F6)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Layout Editor",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        val layout = ControllerLayout(
                            id = "custom_${System.currentTimeMillis()}",
                            name = layoutName,
                            elements = elements
                        )
                        onSave(layout)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF16213E).copy(alpha = 0.6f))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AddElementButton(
                    icon = Icons.Default.Add,
                    label = "Button",
                    color = Color(0xFF42A5F5),
                    onClick = {
                        val newElement = LayoutElement.Button(
                            id = "button_${System.currentTimeMillis()}",
                            x = 0.5f,
                            y = 0.5f,
                            size = 80f,
                            label = "Btn"
                        )
                        elements = elements + newElement
                    },
                    modifier = Modifier.weight(1f)
                )
                AddElementButton(
                    icon = Icons.Default.Circle,
                    label = "Joystick",
                    color = Color(0xFF66BB6A),
                    onClick = {
                        val newElement = LayoutElement.Joystick(
                            id = "joystick_${System.currentTimeMillis()}",
                            x = 0.3f,
                            y = 0.6f,
                            size = 120f
                        )
                        elements = elements + newElement
                    },
                    modifier = Modifier.weight(1f)
                )
                AddElementButton(
                    icon = Icons.Default.GridView,
                    label = "D-Pad",
                    color = Color(0xFFEF5350),
                    onClick = {
                        val newElement = LayoutElement.Dpad(
                            id = "dpad_${System.currentTimeMillis()}",
                            x = 0.7f,
                            y = 0.6f,
                            size = 100f
                        )
                        elements = elements + newElement
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF111118), RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFF2A2A4E), RoundedCornerShape(14.dp))
            ) {
                if (elements.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Empty Canvas",
                                color = Color(0xFF444466),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap Button, Joystick, or D-Pad above to start",
                                color = Color(0xFF333355),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                elements.forEach { element ->
                    when (element) {
                        is LayoutElement.Button -> {
                            DraggableElement(
                                element = element,
                                isSelected = selectedElementId == element.id,
                                onSelect = { selectedElementId = if (it == selectedElementId) null else it },
                                onPositionChange = { newX, newY ->
                                    elements = elements.map {
                                        if (it.id == element.id) element.copy(x = newX, y = newY)
                                        else it
                                    }
                                },
                                onDelete = {
                                    elements = elements.filter { it.id != element.id }
                                    selectedElementId = null
                                }
                            )
                        }
                        is LayoutElement.Joystick -> {
                            DraggableJoystick(
                                element = element,
                                isSelected = selectedElementId == element.id,
                                onSelect = { selectedElementId = if (it == selectedElementId) null else it },
                                onPositionChange = { newX, newY ->
                                    elements = elements.map {
                                        if (it.id == element.id) element.copy(x = newX, y = newY)
                                        else it
                                    }
                                },
                                onDelete = {
                                    elements = elements.filter { it.id != element.id }
                                    selectedElementId = null
                                }
                            )
                        }
                        is LayoutElement.Dpad -> {
                            DraggableDpad(
                                element = element,
                                isSelected = selectedElementId == element.id,
                                onSelect = { selectedElementId = if (it == selectedElementId) null else it },
                                onPositionChange = { newX, newY ->
                                    elements = elements.map {
                                        if (it.id == element.id) element.copy(x = newX, y = newY)
                                        else it
                                    }
                                },
                                onDelete = {
                                    elements = elements.filter { it.id != element.id }
                                    selectedElementId = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddElementButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DraggableElement(
    element: LayoutElement.Button,
    isSelected: Boolean,
    onSelect: (String) -> Unit,
    onPositionChange: (Float, Float) -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    val buttonSize = with(density) { element.size.toDp() }
    val brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF42A5F5), Color(0xFF1E88E5))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(buttonSize)
            .shadow(if (isSelected) 8.dp else 3.dp, CircleShape)
            .clip(CircleShape)
            .background(brush)
            .then(
                if (isSelected) Modifier.border(2.dp, Color(0xFF90CAF9), CircleShape)
                else Modifier.border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
            )
            .clickable { onSelect(element.id) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val containerSize = this.size
                        val newX = (offsetX + containerSize.width / 2) / containerSize.width
                        val newY = (offsetY + containerSize.height / 2) / containerSize.height
                        onPositionChange(newX.coerceIn(0f, 1f), newY.coerceIn(0f, 1f))
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = element.label ?: "Btn",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(22.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5252))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Text("\u2716", color = Color.White, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun DraggableJoystick(
    element: LayoutElement.Joystick,
    isSelected: Boolean,
    onSelect: (String) -> Unit,
    onPositionChange: (Float, Float) -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    val joystickSize = with(density) { element.size.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(joystickSize)
            .shadow(if (isSelected) 8.dp else 3.dp, CircleShape)
            .clip(CircleShape)
            .background(Color(0xFF1B5E20).copy(alpha = 0.35f))
            .then(
                if (isSelected) Modifier.border(2.dp, Color(0xFF81C784), CircleShape)
                else Modifier.border(1.dp, Color(0xFF66BB6A).copy(alpha = 0.3f), CircleShape)
            )
            .clickable { onSelect(element.id) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val containerSize = this.size
                        val newX = (offsetX + containerSize.width / 2) / containerSize.width
                        val newY = (offsetY + containerSize.height / 2) / containerSize.height
                        onPositionChange(newX.coerceIn(0f, 1f), newY.coerceIn(0f, 1f))
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(joystickSize / 2)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF66BB6A), Color(0xFF388E3C))
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\u25cf",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = (element.size / 8).sp
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(22.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5252))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Text("\u2716", color = Color.White, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun DraggableDpad(
    element: LayoutElement.Dpad,
    isSelected: Boolean,
    onSelect: (String) -> Unit,
    onPositionChange: (Float, Float) -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    val dpadSize = with(density) { element.size.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(dpadSize)
            .shadow(if (isSelected) 8.dp else 3.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF4E342E).copy(alpha = 0.4f))
            .then(
                if (isSelected) Modifier.border(2.dp, Color(0xFFEF9A9A), RoundedCornerShape(10.dp))
                else Modifier.border(1.dp, Color(0xFFEF5350).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            )
            .clickable { onSelect(element.id) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val containerSize = this.size
                        val newX = (offsetX + containerSize.width / 2) / containerSize.width
                        val newY = (offsetY + containerSize.height / 2) / containerSize.height
                        onPositionChange(newX.coerceIn(0f, 1f), newY.coerceIn(0f, 1f))
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("\u25b2", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\u25c0", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("\u25b6", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
            Text("\u25bc", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(22.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5252))
                    .clickable { onDelete() },
                contentAlignment = Alignment.Center
            ) {
                Text("\u2716", color = Color.White, fontSize = 10.sp)
            }
        }
    }
}
