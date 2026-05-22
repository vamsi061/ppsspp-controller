package com.gamingcontroller.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ElementType {
    BUTTON,
    JOYSTICK,
    DPAD,
    SHOULDER_BUTTON,
    TRIGGER
}

enum class ShapeType {
    CIRCLE,
    RECTANGLE,
    ROUNDED
}

@Composable
fun ElementPalette(
    onAddElement: (ElementType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Elements",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ElementTypeButton(
            label = "Button",
            onClick = { onAddElement(ElementType.BUTTON) }
        )

        ElementTypeButton(
            label = "Joystick",
            onClick = { onAddElement(ElementType.JOYSTICK) }
        )

        ElementTypeButton(
            label = "D-pad",
            onClick = { onAddElement(ElementType.DPAD) }
        )

        ElementTypeButton(
            label = "Shoulder",
            onClick = { onAddElement(ElementType.SHOULDER_BUTTON) }
        )

        ElementTypeButton(
            label = "Trigger",
            onClick = { onAddElement(ElementType.TRIGGER) }
        )
    }
}

@Composable
private fun ElementTypeButton(
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4A4A4A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label.take(1),
                    color = Color.White,
                    fontSize = 10.sp
                )
            }
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ColorPickerRow(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val presetColors = listOf(
        0xFFFF0000L, // Red
        0xFF00FF00L, // Green
        0xFF0000FFL, // Blue
        0xFFFFFF00L, // Yellow
        0xFF800080L, // Purple
        0xFFFFFFFFL, // White
        0xFF808080L  // Gray
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presetColors.forEach { colorValue ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(if (colorValue == 0xFFFFFFFFL) RoundedCornerShape(4.dp) else CircleShape)
                    .background(Color(colorValue))
                    .border(
                        width = if (selectedColor == colorValue) 2.dp else 0.dp,
                        color = if (selectedColor == colorValue) Color.White else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(colorValue) }
            )
        }
    }
}

@Composable
fun ShapeSelector(
    selectedShape: ShapeType,
    onShapeSelected: (ShapeType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShapeType.entries.forEach { shape ->
            Card(
                modifier = Modifier
                    .clickable { onShapeSelected(shape) }
                    .border(
                        width = if (selectedShape == shape) 2.dp else 0.dp,
                        color = if (selectedShape == shape) Color(0xFF6B4EFF) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (shape) {
                        ShapeType.CIRCLE -> {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                        ShapeType.RECTANGLE -> {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.White)
                            )
                        }
                        ShapeType.ROUNDED -> {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionIdPicker(
    actionId: String,
    onActionIdChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val actionOptions = listOf(
        "button_a", "button_b", "button_x", "button_y",
        "l1", "r1", "l2", "r2",
        "dpad_up", "dpad_down", "dpad_left", "dpad_right",
        "left_stick", "right_stick", "start", "select"
    )

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = actionId,
            onValueChange = onActionIdChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Action ID") },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Select action",
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            singleLine = true
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            actionOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onActionIdChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}