package com.example.piccontrol.ui.state

import com.example.piccontrol.domain.ConnectionState
import com.example.piccontrol.domain.SensorData

data class UiState(
    /*val p1: Int = 0,
    val p2: Int = 0,
    val p3: Int = 0,
    val p4: Int = 0,*/
    val sensorData: SensorData = SensorData(),
    val fanSpeed: Int = 0,
    val motorDirection: Boolean = true,  // true = Derecha, false = Izquierda
    val motorSpeed: Int = 0,             // 0 = Detenido, 1-255 = Velocidad
    val motorDegrees: Int = 0,
    //val isMotorMoving: Boolean = false,
    val connectionState: ConnectionState = ConnectionState.Disconnected,
)

