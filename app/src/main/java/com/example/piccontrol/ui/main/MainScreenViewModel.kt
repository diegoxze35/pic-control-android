package com.example.piccontrol.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piccontrol.data.BluetoothController
import com.example.piccontrol.domain.BluetoothState
import com.example.piccontrol.domain.CommandType
import com.example.piccontrol.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(private val bluetoothController: BluetoothController) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val btState: StateFlow<BluetoothState> = bluetoothController.bluetoothState

    private var accumulatedData = ""

    init {
        viewModelScope.launch {
            bluetoothController.connectionState.collect { connectionState ->
                _uiState.update { it.copy(connectionState = connectionState) }
            }
        }

        viewModelScope.launch {
            bluetoothController.incomingData.collect { data ->
                parseData(data)
            }
        }
    }

    private fun parseData(newData: String) {
        accumulatedData += newData
        if (accumulatedData.contains("\n")) {
            val lines = accumulatedData.split("\n")
            for (i in 0 until lines.size - 1) {
                val line = lines[i].trim()
                if (line == "M:DONE") {
                    _uiState.update { it.copy(isMotorMoving = false) }
                } else {
                    processLine(line)
                }
            }
            accumulatedData = lines.last()
        }
    }

    private fun processLine(line: String) {
        val parts = line.split(",")
        var p1 = _uiState.value.p1
        var p2 = _uiState.value.p2
        var p3 = _uiState.value.p3
        var p4 = _uiState.value.p4
        parts.forEach { part ->
            val kv = part.split(":")
            if (kv.size == 2) {
                val value = kv[1].toIntOrNull() ?: 0
                when (kv[0].trim()) {
                    "P1" -> p1 = value
                    "P2" -> p2 = value
                    "P3" -> p3 = value
                    "P4" -> p4 = value
                }
            }
        }
        _uiState.update { it.copy(p1 = p1, p2 = p2, p3 = p3, p4 = p4) }
    }

    fun setFanSpeed(speed: Int) {
        _uiState.update { it.copy(fanSpeed = speed) }
        viewModelScope.launch {
            bluetoothController.sendCommand(CommandType.FAN_SPEED, speed.toByte())
        }
    }

    fun setMotorDirection(isRight: Boolean) {
        _uiState.update { it.copy(motorDirection = isRight) }
        viewModelScope.launch {
            val dirByte: Byte = if (isRight) 1 else 0
            bluetoothController.sendCommand(CommandType.MOTOR_DIR, dirByte)
        }
    }

    fun setMotorSpeed(speed: Int) {
        _uiState.update { it.copy(motorSpeed = speed) }
        viewModelScope.launch {
            bluetoothController.sendCommand(CommandType.MOTOR_SPEED, speed.toByte())
        }
    }

    fun sendMotorMove(degrees: Int) {
        _uiState.update { it.copy(motorDegrees = degrees, isMotorMoving = true) }

        viewModelScope.launch {
            // Conversión: 360 grados = 4096 pasos (Motor 28BYJ-48 en Half-Step)
            val steps = ((degrees.toFloat() / 360f) * 4096).toInt()
            val highByte = (steps shr 8).toByte()
            val lowByte = (steps and 0xFF).toByte()

            bluetoothController.sendCommand(CommandType.MOTOR_MOVE, highByte, lowByte)
        }
    }

    fun getPairedDevices() = bluetoothController.getPairedDevices()

    override fun onCleared() {
        bluetoothController.stopListeningBluetoothChanges()
        super.onCleared()
    }

}
