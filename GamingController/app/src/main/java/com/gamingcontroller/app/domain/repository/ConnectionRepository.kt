package com.gamingcontroller.app.domain.repository

import com.gamingcontroller.app.domain.model.ControllerState
import com.gamingcontroller.app.domain.model.DiscoveredDevice
import kotlinx.coroutines.flow.StateFlow

interface ConnectionRepository {
    val discoveredDevices: StateFlow<List<DiscoveredDevice>>
    val isConnected: StateFlow<Boolean>
    val connectionStatus: StateFlow<String>

    suspend fun startDiscovery()
    suspend fun stopDiscovery()
    suspend fun connect(device: DiscoveredDevice)
    suspend fun disconnect()
    suspend fun sendControllerState(state: ControllerState)
}