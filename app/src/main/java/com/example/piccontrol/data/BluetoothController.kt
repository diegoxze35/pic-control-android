package com.example.piccontrol.data

import com.example.piccontrol.domain.BluetoothDeviceInformation
import com.example.piccontrol.domain.BluetoothState
import com.example.piccontrol.domain.CommandType
import com.example.piccontrol.domain.ConnectionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class BluetoothController {
    abstract val connectionState: Flow<ConnectionState>
    abstract val bluetoothState: StateFlow<BluetoothState>
    abstract val incomingData: Flow<String>
    abstract fun getPairedDevices(): List<BluetoothDeviceInformation>
    abstract suspend fun connect(device: BluetoothDeviceInformation)
    protected abstract suspend fun startListening()
    abstract suspend fun sendCommand(command: CommandType, vararg values: Byte)
    abstract fun disconnect()
    abstract fun stopListeningBluetoothChanges()
}