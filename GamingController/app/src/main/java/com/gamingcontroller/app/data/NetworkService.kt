package com.gamingcontroller.app.data

import com.gamingcontroller.app.domain.model.ControllerState
import com.gamingcontroller.app.domain.model.DiscoveredDevice
import com.gamingcontroller.app.domain.repository.ConnectionRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkService @Inject constructor() : ConnectionRepository {

    private fun debugLog(message: String) {
        android.util.Log.d("NetworkService", message)
    }

    init {
        try {
            android.util.Log.d("NetworkService", "NetworkService initialized")
        } catch (e: Exception) {
            android.util.Log.e("NetworkService", "Init error: ${e.message}")
        }
    }

    private val scope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
    private val gson = Gson()

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    override val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _connectionStatus = MutableStateFlow("Disconnected")
    override val connectionStatus: StateFlow<String> = _connectionStatus.asStateFlow()

    private var discoveryJob: Job? = null
    private var tcpClient: Socket? = null
    private var outputStream: java.io.OutputStream? = null
    private var currentControllerState: ControllerState? = null

    private val discoveredDevicesMap = mutableMapOf<String, DiscoveredDevice>()
    private var prevButtonState = com.gamingcontroller.app.domain.model.ButtonState()
    private var prevShoulderState = com.gamingcontroller.app.domain.model.ShoulderState()
    private var prevDpadState = com.gamingcontroller.app.domain.model.DpadState()

    companion object {
        private const val DISCOVERY_PORT = 9000
        private const val TCP_PORT = 9001
        private const val BROADCAST_INTERVAL_MS = 2000L
    }

    override suspend fun startDiscovery() {
        discoveryJob?.cancel()
        discoveredDevicesMap.clear()
        _discoveredDevices.value = emptyList()

        discoveryJob = scope.launch {
            _connectionStatus.value = "Scanning..."
            while (isActive) {
                broadcastDiscovery()
                delay(BROADCAST_INTERVAL_MS)
            }
        }
    }

    private suspend fun broadcastDiscovery() {
        withContext(Dispatchers.IO) {
            debugLog("Starting UDP broadcast discovery...")
            try {
                val socket = DatagramSocket()
                socket.soTimeout = 1000
                debugLog("Socket created, sending broadcast...")

                val message = "GAMING_CONTROLLER_DISCOVER".toByteArray(StandardCharsets.UTF_8)
                val broadcastAddress = InetAddress.getByName("255.255.255.255")
                val packet = DatagramPacket(message, message.size, broadcastAddress, DISCOVERY_PORT)
                socket.send(packet)
                debugLog("Broadcast sent, waiting for response...")

                val buffer = ByteArray(1024)
                val response = DatagramPacket(buffer, buffer.size)

                try {
                    while (true) {
                        socket.receive(response)
                        val responseStr = String(response.data, 0, response.length, StandardCharsets.UTF_8)
                        parseDeviceResponse(responseStr, response.address.hostAddress)
                    }
                } catch (e: java.net.SocketTimeoutException) {
                    debugLog("Discovery timeout reached")
                }

                socket.close()
            } catch (e: Exception) {
                debugLog("Discovery error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun parseDeviceResponse(json: String, fallbackIp: String) {
        try {
            val response = gson.fromJson(json, DeviceResponse::class.java)
            if (response.type == "announce") {
                val device = DiscoveredDevice(
                    name = response.name ?: "Unknown Mac",
                    ip = response.ip ?: fallbackIp,
                    port = response.port ?: TCP_PORT
                )
                if (discoveredDevicesMap[device.ip] == null) {
                    discoveredDevicesMap[device.ip] = device
                    _discoveredDevices.value = discoveredDevicesMap.values.toList()
                    _connectionStatus.value = "Found ${_discoveredDevices.value.size} device(s)"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun stopDiscovery() {
        discoveryJob?.cancel()
        discoveryJob = null
        if (!_isConnected.value) {
            _connectionStatus.value = "Disconnected"
        }
    }

    override suspend fun connect(device: DiscoveredDevice) {
        android.util.Log.w("NetworkService", "=== connect called ===")
        withContext(Dispatchers.IO) {
            android.util.Log.w("NetworkService", "Attempting to connect to ${device.ip}:${device.port}")
            try {
                _connectionStatus.value = "Connecting to ${device.name}..."

                tcpClient = Socket(device.ip, device.port)
                android.util.Log.w("NetworkService", "TCP Socket connected successfully!")
                outputStream = tcpClient?.getOutputStream()
                android.util.Log.w("NetworkService", "outputStream obtained: ${outputStream != null}")
                tcpClient?.soTimeout = 5000

                if (outputStream == null) {
                    throw Exception("Failed to get output stream")
                }

                _isConnected.value = true
                _connectionStatus.value = "Connected to ${device.name}"

                launch {
                    listenForResponses()
                }
                android.util.Log.w("NetworkService", "=== connect complete ===")
            } catch (e: Exception) {
                android.util.Log.e("NetworkService", "Connect error: ${e.message}")
                _connectionStatus.value = "Connection failed: ${e.message}"
                _isConnected.value = false
                tcpClient = null
                outputStream = null
            }
        }
    }

    private suspend fun listenForResponses() {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = tcpClient?.getInputStream()
                val buffer = ByteArray(1024)

                while (tcpClient?.isConnected == true) {
                    try {
                        val bytesRead = inputStream?.read(buffer) ?: -1
                        if (bytesRead == -1) break

                        val response = String(buffer, 0, bytesRead, StandardCharsets.UTF_8)
                        // Handle any response from server if needed
                    } catch (e: java.net.SocketTimeoutException) {
                        // Continue listening
                    }
                }
            } catch (e: Exception) {
                if (_isConnected.value) {
                    _connectionStatus.value = "Connection lost"
                    _isConnected.value = false
                }
            }
        }
    }

    override suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            try {
                outputStream?.close()
                tcpClient?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            tcpClient = null
            outputStream = null
            _isConnected.value = false
            _connectionStatus.value = "Disconnected"
        }
    }

    override suspend fun sendControllerState(state: ControllerState) {
        currentControllerState = state

        if (!_isConnected.value || outputStream == null) {
            return
        }

        if (state.buttons != prevButtonState) {
            prevButtonState = state.buttons
            android.util.Log.w("NetworkService", "Button change: A=${state.buttons.a} B=${state.buttons.b} X=${state.buttons.x} Y=${state.buttons.y} Sel=${state.buttons.select} Start=${state.buttons.start}")
        }
        if (state.shoulders != prevShoulderState) {
            prevShoulderState = state.shoulders
            android.util.Log.w("NetworkService", "Shoulder change: L1=${state.shoulders.l1} R1=${state.shoulders.r1}")
        }
        if (state.dpad != prevDpadState) {
            prevDpadState = state.dpad
            android.util.Log.w("NetworkService", "Dpad change: U=${state.dpad.up} D=${state.dpad.down} L=${state.dpad.left} R=${state.dpad.right}")
        }

        sendControllerStateInternal(state)
    }

    private var sendCount = 0

    private suspend fun sendControllerStateInternal(state: ControllerState) {
        withContext(Dispatchers.IO) {
            try {
                val dataMap = mapOf(
                    "leftStickX" to state.leftStick.x,
                    "leftStickY" to state.leftStick.y,
                    "rightStickX" to state.rightStick.x,
                    "rightStickY" to state.rightStick.y,
                    "buttonA" to state.buttons.a,
                    "buttonB" to state.buttons.b,
                    "buttonX" to state.buttons.x,
                    "buttonY" to state.buttons.y,
                    "buttonSelect" to state.buttons.select,
                    "buttonStart" to state.buttons.start,
                    "buttonEsc" to state.buttons.esc,
                    "l1" to state.shoulders.l1,
                    "r1" to state.shoulders.r1,
                    "l2" to state.triggers.l2,
                    "r2" to state.triggers.r2,
                    "dpadUp" to state.dpad.up,
                    "dpadDown" to state.dpad.down,
                    "dpadLeft" to state.dpad.left,
                    "dpadRight" to state.dpad.right
                )
                val packet = mapOf(
                    "type" to "input",
                    "data" to dataMap
                )
                val json = gson.toJson(packet) + "\n"
                val bytes = json.toByteArray(StandardCharsets.UTF_8)
                outputStream?.write(bytes)
                outputStream?.flush()
                sendCount++
                if (sendCount % 300 == 0) {
                    android.util.Log.w("NetworkService", "=== DATA SENT: ${bytes.size} bytes ===")
                }
                Unit
            } catch (e: Exception) {
                android.util.Log.e("NetworkService", "Send error: ${e.message}")
            }
        }
    }
}

private data class DeviceResponse(
    val type: String?,
    val name: String?,
    val ip: String?,
    val port: Int?
)

private data class ControllerPacket(
    val type: String,
    val data: ControllerData
)

private data class ControllerData(
    val leftStick: StickData,
    val rightStick: StickData,
    val buttons: ButtonsData,
    val shoulders: ShouldersData,
    val triggers: TriggersData,
    val dpad: DpadData
)

private data class StickData(
    val x: Float,
    val y: Float
)

private data class ButtonsData(
    val a: Boolean,
    val b: Boolean,
    val x: Boolean,
    val y: Boolean
)

private data class ShouldersData(
    val l1: Boolean,
    val r1: Boolean
)

private data class TriggersData(
    val l2: Boolean,
    val r2: Boolean
)

private data class DpadData(
    val up: Boolean,
    val down: Boolean,
    val left: Boolean,
    val right: Boolean
)