package com.gamingcontroller.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gamingcontroller.app.ui.components.LayoutRenderer
import com.gamingcontroller.app.ui.components.LayoutSelectionDialog
import com.gamingcontroller.app.ui.screens.LayoutEditorScreen
import com.gamingcontroller.app.ui.screens.SettingsScreen

@Composable
fun ControllerScreen(
    viewModel: ControllerViewModel
) {
    val currentLayout by viewModel.currentLayout.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    var showEditor by remember { mutableStateOf(false) }
    var showLayoutPicker by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(onBack = { showSettings = false })
        return
    }
    if (showEditor) {
        LayoutEditorScreen(
            onBack = { showEditor = false },
            onSave = { viewModel.saveLayout(it) }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0D0D),
                        Color(0xFF1A1A2E),
                        Color(0xFF121212)
                    )
                )
            )
    ) {
        if (!isConnected) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFFF44336).copy(alpha = 0.15f))
                        .border(2.dp, Color(0xFFF44336).copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("!", color = Color(0xFFF44336), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Not connected", color = Color(0xFF888888), fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tap device in connection screen", color = Color(0xFF555555), fontSize = 13.sp)
            }
        } else {
            val layout = currentLayout
            if (layout != null) {
                LayoutRenderer(
                    layout = layout,
                    onButtonPressed = { actionId, pressed ->
                        viewModel.updateButtons(actionId, pressed)
                    },
                    onJoystickMoved = { id, x, y ->
                        if (id == "left_stick") viewModel.updateLeftStick(x, y)
                        else viewModel.updateRightStick(x, y)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 28.dp, end = 4.dp)
                    .background(Color(0xFF0D0D0D).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 3.dp, vertical = 2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TopIcon(icon = Icons.Default.GridView, onClick = { showLayoutPicker = true })
                    TopIcon(icon = Icons.Default.Add, onClick = { showEditor = true })
                    TopIcon(icon = Icons.Default.Settings, onClick = { showSettings = true })
                    TopIcon(icon = Icons.Default.Close, tint = Color(0xFFE57373), onClick = { viewModel.disconnect() })
                }
            }
        }

        if (showLayoutPicker) {
            LayoutSelectionDialog(
                layouts = viewModel.availableLayouts.collectAsState().value,
                currentLayoutId = currentLayout?.id ?: "",
                onDismiss = { showLayoutPicker = false },
                onLayoutSelected = { layoutId ->
                    viewModel.setLayout(layoutId)
                    showLayoutPicker = false
                }
            )
        }
    }
}

@Composable
private fun TopIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color = Color(0xFF90CAF9),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .clickable(onClick = onClick)
            .padding(5.dp)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
    }
}
