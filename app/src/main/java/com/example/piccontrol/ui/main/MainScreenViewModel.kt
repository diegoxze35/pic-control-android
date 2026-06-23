package com.example.piccontrol.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.piccontrol.data.BluetoothController
import com.example.piccontrol.data.SensorDataRepository
import com.example.piccontrol.domain.BluetoothState
import com.example.piccontrol.domain.CommandType
import com.example.piccontrol.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val bluetoothController: BluetoothController,
    private val sensorDataRepository: SensorDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val btState: StateFlow<BluetoothState> = bluetoothController.bluetoothState

    init {
        viewModelScope.launch {
            bluetoothController.connectionState.collect { connectionState ->
                _uiState.update { it.copy(connectionState = connectionState) }
            }
        }

        viewModelScope.launch {
            sensorDataRepository.sensorData.collect { sensorData ->
                _uiState.update { it.copy(sensorData = sensorData) }
            }
        }
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
        _uiState.update {
            it.copy(motorDegrees = degrees)
        }
        sensorDataRepository.setMotorMoving()
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
