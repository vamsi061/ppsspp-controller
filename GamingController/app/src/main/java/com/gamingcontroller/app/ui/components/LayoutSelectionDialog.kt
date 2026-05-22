package com.gamingcontroller.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamingcontroller.app.domain.model.ControllerLayout

@Composable
fun LayoutSelectionDialog(
    layouts: List<ControllerLayout>,
    currentLayoutId: String,
    onLayoutSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedLayoutId by remember { mutableStateOf(currentLayoutId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Layout",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                layouts.forEach { layout ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLayoutId = layout.id }
                            .background(
                                color = if (selectedLayoutId == layout.id) {
                                    Color(0xFF3D5AFE).copy(alpha = 0.1f)
                                } else {
                                    Color.Transparent
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLayoutId == layout.id,
                            onClick = { selectedLayoutId = layout.id }
                        )
                        Text(
                            text = layout.name,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onLayoutSelected(selectedLayoutId)
                    onDismiss()
                }
            ) {
                Text("Select", color = Color(0xFF64B5F6))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF888888))
            }
        },
        containerColor = Color(0xFF1A1A2E),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun LayoutOptionsRow(
    currentLayoutName: String,
    onLayoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E2E))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Layout:",
            color = Color.White,
            fontSize = 14.sp
        )
        Row(
            modifier = Modifier
                .clickable { onLayoutClick() }
                .background(
                    Color(0xFF3D5AFE).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentLayoutName,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LayoutQuickSelector(
    layouts: List<ControllerLayout>,
    currentLayoutId: String,
    onLayoutSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .background(Color(0xFF2D2D44))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Layout: ${layouts.find { it.id == currentLayoutId }?.name ?: "Select"}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (expanded) "▲" else "▼",
                color = Color.White,
                fontSize = 12.sp
            )
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E2E))
            ) {
                layouts.forEach { layout ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLayoutSelected(layout.id)
                                expanded = false
                            }
                            .background(
                                if (currentLayoutId == layout.id) {
                                    Color(0xFF3D5AFE).copy(alpha = 0.3f)
                                } else {
                                    Color.Transparent
                                }
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = layout.name,
                            color = if (currentLayoutId == layout.id) {
                                Color(0xFF64B5F6)
                            } else {
                                Color.White
                            },
                            fontSize = 14.sp,
                            fontWeight = if (currentLayoutId == layout.id) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }
        }
    }
}