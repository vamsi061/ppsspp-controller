package com.gamingcontroller.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gamingcontroller.app.domain.model.AvailableKeys
import com.gamingcontroller.app.domain.model.KeyAction
import com.gamingcontroller.app.domain.model.KeyActionType

@Composable
fun KeyMappingDialog(
    buttonId: String,
    currentMapping: KeyAction?,
    availableKeys: List<String>,
    onMappingSaved: (actionId: String, keyAction: KeyAction) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Map Key for $buttonId",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            KeyMappingContent(
                actionId = buttonId,
                currentMapping = currentMapping,
                onMappingSaved = onMappingSaved
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun KeyMappingContent(
    actionId: String,
    currentMapping: KeyAction?,
    onMappingSaved: (actionId: String, keyAction: KeyAction) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Keyboard", "Mouse", "Modifiers")

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> KeyboardSection(actionId, currentMapping, onMappingSaved)
            1 -> MouseSection(actionId, currentMapping, onMappingSaved)
            2 -> ModifierSection(actionId, currentMapping, onMappingSaved)
        }
    }
}

@Composable
fun KeyboardSection(
    actionId: String,
    currentMapping: KeyAction?,
    onMappingSaved: (actionId: String, keyAction: KeyAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(AvailableKeys.keyboardKeys) { keyName ->
            KeyItem(
                keyName = keyName,
                isSelected = currentMapping?.let {
                    it.type == KeyActionType.KEYBOARD && it.value == keyName
                } ?: false,
                onClick = {
                    onMappingSaved(actionId, KeyAction(type = KeyActionType.KEYBOARD, value = keyName))
                }
            )
        }
    }
}

@Composable
fun MouseSection(
    actionId: String,
    currentMapping: KeyAction?,
    onMappingSaved: (actionId: String, keyAction: KeyAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(AvailableKeys.mouseActions) { mouseAction ->
            KeyItem(
                keyName = mouseAction,
                isSelected = currentMapping?.let {
                    it.type == KeyActionType.MOUSE_CLICK && it.value == mouseAction
                } ?: false,
                onClick = {
                    onMappingSaved(actionId, KeyAction(type = KeyActionType.MOUSE_CLICK, value = mouseAction))
                }
            )
        }
    }
}

@Composable
fun ModifierSection(
    actionId: String,
    currentMapping: KeyAction?,
    onMappingSaved: (actionId: String, keyAction: KeyAction) -> Unit
) {
    val modifierOptions = listOf("Shift", "Ctrl", "Alt", "Cmd")
    var selectedModifiers by remember {
        mutableStateOf(
            currentMapping?.modifiers?.toMutableSet() ?: mutableSetOf<String>()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Select modifiers (combine with keyboard key):",
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        modifierOptions.forEach { modifier ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedModifiers = if (selectedModifiers.contains(modifier)) {
                            mutableSetOf<String>().apply { addAll(selectedModifiers); remove(modifier) }
                        } else {
                            mutableSetOf<String>().apply { addAll(selectedModifiers); add(modifier) }
                        }
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedModifiers.contains(modifier),
                    onCheckedChange = { checked ->
                        selectedModifiers = if (checked) {
                            mutableSetOf<String>().apply { addAll(selectedModifiers); add(modifier) }
                        } else {
                            mutableSetOf<String>().apply { addAll(selectedModifiers); remove(modifier) }
                        }
                    }
                )
                Text(
                    text = modifier,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (selectedModifiers.isNotEmpty()) {
            Text(
                text = "Selected: ${selectedModifiers.joinToString(" + ")}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun KeyItem(
    keyName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) Color(0xFFE3F2FD) else Color.Transparent,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = keyName,
            fontSize = 14.sp
        )
        if (isSelected) {
            Text(
                text = "✓",
                color = Color(0xFF1976D2),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModifierSelector(
    selectedModifiers: Set<String>,
    onModifiersChanged: (Set<String>) -> Unit
) {
    val modifierOptions = listOf("Shift", "Ctrl", "Alt", "Cmd")

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Modifiers",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            modifierOptions.forEach { modifier ->
                val isSelected = selectedModifiers.contains(modifier)
                Row(
                    modifier = Modifier
                        .background(
                            if (isSelected) Color(0xFF1976D2) else Color(0xFFE0E0E0),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            onModifiersChanged(
                                if (isSelected) {
                                    selectedModifiers - modifier
                                } else {
                                    selectedModifiers + modifier
                                }
                            )
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = modifier,
                        color = if (isSelected) Color.White else Color.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}