package com.example.piccontrol.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothController(context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? =
        context.getSystemService(BluetoothManager::class.java)?.adapter
    private var bluetoothSocket: BluetoothSocket? = null
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _incomingData = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val incomingData: SharedFlow<String> = _incomingData.asSharedFlow()

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): List<BluetoothDevice> {
        return try {
            bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun connect(device: BluetoothDevice) {
        return withContext(Dispatchers.IO) {
            try {
                bluetoothAdapter?.cancelDiscovery()
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket?.connect()
                _isConnected.value = true
                launch {
                    startListening()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun startListening() {
        val buffer = ByteArray(1024)
        var bytes: Int
        val inputStream: InputStream? = bluetoothSocket?.inputStream

        while (_isConnected.value) {
            try {
                withContext(Dispatchers.IO) {
                    bytes = inputStream?.read(buffer) ?: -1
                }
                if (bytes > 0) {
                    val data = String(buffer, 0, bytes)
                    _incomingData.tryEmit(data)
                }
            } catch (e: IOException) {
                _isConnected.value = false
                break
            }
        }
    }

    suspend fun sendCommand(command: CommandType, vararg values: Byte) {
        withContext(Dispatchers.IO) {
            try {
                val outputStream: OutputStream? = bluetoothSocket?.outputStream
                val payload = ByteArray(1 + values.size)
                payload[0] = command.byteValue
                values.copyInto(payload, 1)
                outputStream?.write(payload)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun disconnect() {
        try {
            bluetoothSocket?.close()
            _isConnected.value = false
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}