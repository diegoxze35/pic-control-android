package com.example.piccontrol.data.impl

import com.example.piccontrol.data.BluetoothController
import com.example.piccontrol.data.SensorDataRepository
import com.example.piccontrol.domain.SensorData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SensorDataRepositoryImpl(
    private val bluetoothController: BluetoothController,
    private val coroutineScope: CoroutineScope
) : SensorDataRepository {

    private var accumulatedData = ""

    init {
        coroutineScope.launch {
            bluetoothController.incomingData.collect { data ->
                parseData(data)
            }
        }
    }

    private val _sensorData = MutableStateFlow(SensorData())

    override fun setMotorMoving() {
        _sensorData.update { it.copy(isMotorMoving = true) }
    }

    override val sensorData: Flow<SensorData>
        get() = _sensorData.asStateFlow()

    private fun parseData(newData: String) {
        accumulatedData += newData
        if (accumulatedData.contains("\n")) {
            val lines = accumulatedData.split("\n")
            for (i in 0 until lines.size - 1) {
                val line = lines[i].trim()
                if (line == "M:DONE") {
                    _sensorData.update { it.copy(isMotorMoving = false) }
                } else {
                    processLine(line)
                }
            }
            accumulatedData = lines.last()
        }
    }

    private fun processLine(line: String) {
        val parts = line.split(",")
        var p1 = _sensorData.value.p1
        var p2 = _sensorData.value.p2
        var p3 = _sensorData.value.p3
        var p4 = _sensorData.value.p4
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
        _sensorData.update { it.copy(p1 = p1, p2 = p2, p3 = p3, p4 = p4) }
    }

}