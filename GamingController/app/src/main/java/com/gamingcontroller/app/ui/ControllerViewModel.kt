package com.gamingcontroller.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamingcontroller.app.data.LayoutRepository
import com.gamingcontroller.app.domain.model.ButtonState
import com.gamingcontroller.app.domain.model.ControllerLayout
import com.gamingcontroller.app.domain.model.ControllerState
import com.gamingcontroller.app.domain.model.DpadState
import com.gamingcontroller.app.domain.model.DiscoveredDevice
import com.gamingcontroller.app.domain.model.JoystickSettings
import com.gamingcontroller.app.domain.model.KeyAction
import com.gamingcontroller.app.domain.model.LayoutPresets
import com.gamingcontroller.app.domain.model.ShoulderState
import com.gamingcontroller.app.domain.model.StickPosition
import com.gamingcontroller.app.domain.model.TriggerState
import com.gamingcontroller.app.domain.repository.ConnectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import android.util.Log
import javax.inject.Inject

@HiltViewModel
class ControllerViewModel @Inject constructor(
    private val connectionRepository: ConnectionRepository,
    private val layoutRepository: LayoutRepository
) : ViewModel() {

    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = connectionRepository.discoveredDevices
    val isConnected: StateFlow<Boolean> = connectionRepository.isConnected
    val connectionStatus: StateFlow<String> = connectionRepository.connectionStatus

    private val _controllerState = MutableStateFlow(ControllerState())
    val controllerState: StateFlow<ControllerState> = _controllerState.asStateFlow()

    private val _currentLayout = MutableStateFlow<ControllerLayout?>(null)
    val currentLayout: StateFlow<ControllerLayout?> = _currentLayout.asStateFlow()

    private val _availableLayouts = MutableStateFlow<List<ControllerLayout>>(emptyList())
    val availableLayouts: StateFlow<List<ControllerLayout>> = _availableLayouts.asStateFlow()

    private var sendJob: Job? = null

    init {
        try {
            loadLayouts()
        } catch (e: Exception) {
            android.util.Log.e("ControllerViewModel", "Init crash: ${e.message}", e)
            _availableLayouts.value = listOf(
                ControllerLayout(
                    id = "emergency",
                    name = "Emergency Layout",
                    elements = emptyList()
                )
            )
        }
    }

    private fun loadLayouts() {
        try {
            viewModelScope.launch {
                try {
                    val layouts = layoutRepository.getAllLayouts()
                    _availableLayouts.value = layouts.ifEmpty {
                        listOf(LayoutPresets.STANDARD)
                    }
                    val current = layoutRepository.getCurrentLayout()
                    _currentLayout.value = current
                } catch (e: Exception) {
                    android.util.Log.e("ControllerViewModel", "loadLayouts error: ${e.message}")
                    _availableLayouts.value = listOf(LayoutPresets.STANDARD)
                    _currentLayout.value = LayoutPresets.STANDARD
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ControllerViewModel", "loadLayouts outer error: ${e.message}")
        }
    }

    fun setLayout(layoutId: String) {
        viewModelScope.launch {
            val layout = _availableLayouts.value.find { it.id == layoutId }
            layout?.let {
                layoutRepository.setCurrentLayout(layoutId)
                _currentLayout.value = it
                sendLayoutConfig(it)
            }
        }
    }

    fun updateKeyMapping(actionId: String, keyAction: KeyAction) {
        _currentLayout.value?.let { layout ->
            val updatedMappings = layout.keyMappings.toMutableMap()
            updatedMappings[actionId] = keyAction
            _currentLayout.value = layout.copy(keyMappings = updatedMappings)
        }
    }

    fun updateButtonMapping(actionId: String, keyAction: KeyAction) {
        updateKeyMapping(actionId, keyAction)
    }

    fun updateJoystickSettings(settings: JoystickSettings) {
        _currentLayout.value = _currentLayout.value?.copy(joystickSettings = settings)
    }

    fun saveLayout(layout: ControllerLayout) {
        viewModelScope.launch {
            layoutRepository.saveLayout(layout)
            _currentLayout.value = layout
            loadLayouts()
            if (connectionRepository.isConnected.value) {
                sendLayoutConfig(layout)
            }
        }
    }

    private fun sendLayoutConfig(layout: ControllerLayout) {
        val packet = _controllerState.value.copy(layoutConfig = layout)
        viewModelScope.launch {
            connectionRepository.sendControllerState(packet)
        }
    }

    fun startDiscovery() {
        viewModelScope.launch {
            connectionRepository.startDiscovery()
        }
    }

    fun stopDiscovery() {
        viewModelScope.launch {
            connectionRepository.stopDiscovery()
        }
    }

    fun connect(device: DiscoveredDevice) {
        android.util.Log.w("ControllerViewModel", "=== CONNECT CALLED ===")
        viewModelScope.launch {
            try {
                connectionRepository.connect(device)
                android.util.Log.i("ControllerViewModel", "=== connect completed ===")
            } catch (e: Exception) {
                android.util.Log.e("ControllerViewModel", "=== connect error: ${e.message} ===")
            }
        }
        startSendingControllerState()
    }

    fun disconnect() {
        sendJob?.cancel()
        viewModelScope.launch {
            connectionRepository.disconnect()
        }
    }

    private fun startSendingControllerState() {
        sendJob?.cancel()
        sendJob = viewModelScope.launch {
            android.util.Log.w("ControllerViewModel", "=== SEND LOOP STARTED ===")
            var sendCount = 0
            while (isActive) {
                sendCount++
                if (sendCount % 300 == 0) {
                    android.util.Log.w("ControllerViewModel", "=== SEND LOOP RUNNING ===")
                }
                try {
                    connectionRepository.sendControllerState(_controllerState.value)
                } catch (e: Exception) {
                    android.util.Log.e("ControllerViewModel", "=== send error: ${e.message} ===")
                }
                delay(16)
            }
        }
    }

    fun updateLeftStick(x: Float, y: Float) {
        Log.d("ControllerViewModel", "LEFT STICK: x=$x y=$y")
        _controllerState.value = _controllerState.value.copy(
            leftStick = StickPosition(x, y)
        )
    }

    fun updateRightStick(x: Float, y: Float) {
        Log.d("ControllerViewModel", "RIGHT STICK: x=$x y=$y")
        _controllerState.value = _controllerState.value.copy(
            rightStick = StickPosition(x, y)
        )
    }

    fun updateButtons(button: String, pressed: Boolean) {
        when (button) {
            "a" -> _controllerState.value = _controllerState.value.copy(
                buttons = _controllerState.value.buttons.copy(a = pressed))
            "b" -> _controllerState.value = _controllerState.value.copy(
                buttons = _controllerState.value.buttons.copy(b = pressed))
            "x" -> _controllerState.value = _controllerState.value.copy(
                buttons = _controllerState.value.buttons.copy(x = pressed))
            "y" -> _controllerState.value = _controllerState.value.copy(
                buttons = _controllerState.value.buttons.copy(y = pressed))
            "select" -> _controllerState.value = _controllerState.value.copy(
                buttons = _controllerState.value.buttons.copy(select = pressed))
            "start" -> _controllerState.value = _controllerState.value.copy(
                buttons = _controllerState.value.buttons.copy(start = pressed))
            "esc" -> {
                Log.w("ControllerViewModel", "=== ESC BUTTON pressed=$pressed ===")
                _controllerState.value = _controllerState.value.copy(
                    buttons = _controllerState.value.buttons.copy(esc = pressed))
            }
            "dpad_left" -> updateDpad("left", pressed)
            "dpad_right" -> updateDpad("right", pressed)
            "dpad_up" -> updateDpad("up", pressed)
            "dpad_down" -> updateDpad("down", pressed)
            "l1" -> updateShoulders("l1", pressed)
            "r1" -> updateShoulders("r1", pressed)
        }
    }

    fun updateShoulders(shoulder: String, pressed: Boolean) {
        val currentShoulders = _controllerState.value.shoulders
        val newShoulders = when (shoulder) {
            "l1" -> currentShoulders.copy(l1 = pressed)
            "r1" -> currentShoulders.copy(r1 = pressed)
            else -> currentShoulders
        }
        _controllerState.value = _controllerState.value.copy(shoulders = newShoulders)
    }

    fun updateTriggers(trigger: String, pressed: Boolean) {
        val currentTriggers = _controllerState.value.triggers
        val newTriggers = when (trigger) {
            "l2" -> currentTriggers.copy(l2 = pressed)
            "r2" -> currentTriggers.copy(r2 = pressed)
            else -> currentTriggers
        }
        _controllerState.value = _controllerState.value.copy(triggers = newTriggers)
    }

    fun updateDpad(direction: String, pressed: Boolean) {
        val currentDpad = _controllerState.value.dpad
        val newDpad = when (direction) {
            "up" -> currentDpad.copy(up = pressed)
            "down" -> currentDpad.copy(down = pressed)
            "left" -> currentDpad.copy(left = pressed)
            "right" -> currentDpad.copy(right = pressed)
            else -> currentDpad
        }
        _controllerState.value = _controllerState.value.copy(dpad = newDpad)
    }

    fun resetAllInputs() {
        _controllerState.value = ControllerState()
    }

    override fun onCleared() {
        super.onCleared()
        sendJob?.cancel()
        viewModelScope.launch {
            connectionRepository.disconnect()
        }
    }
}