package com.example.piccontrol.data

import com.example.piccontrol.domain.SensorData
import kotlinx.coroutines.flow.Flow

interface SensorDataRepository {
    fun setMotorMoving()

    val sensorData: Flow<SensorData>
}