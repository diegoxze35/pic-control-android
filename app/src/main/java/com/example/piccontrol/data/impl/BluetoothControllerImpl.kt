package com.example.piccontrol.data.impl

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.STATE_OFF
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF
import android.bluetooth.BluetoothAdapter.STATE_TURNING_ON
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.bluetooth.BluetoothSocketException
import android.bluetooth.BluetoothSocketException.NULL_DEVICE
import android.bluetooth.BluetoothSocketException.SOCKET_CLOSED
import android.bluetooth.BluetoothSocketException.SOCKET_MANAGER_FAILURE
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresPermission
import com.example.piccontrol.R
import com.example.piccontrol.broadcast.BluetoothStateBroadcastReceiver
import com.example.piccontrol.data.BluetoothController
import com.example.piccontrol.data.datastore.DELAY_ATTEMPTS
import com.example.piccontrol.data.datastore.RECONNECTION_ATTEMPTS
import com.example.piccontrol.data.datastore.dataStore
import com.example.piccontrol.domain.BluetoothDeviceInformation
import com.example.piccontrol.domain.BluetoothState
import com.example.piccontrol.domain.CommandType
import com.example.piccontrol.domain.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class BluetoothControllerImpl(private val context: Context) : BluetoothController() {

    private val dataStoreScope = CoroutineScope(context = Dispatchers.Default)
    private var loopJob: Job? = null
    private var attempts: Int = 3
    private var delay: Int = 1
    private val bluetoothAdapter: BluetoothAdapter? =
        context.getSystemService(BluetoothManager::class.java)?.adapter

    private val _connectionState = MutableStateFlow<ConnectionState>(
        ConnectionState.Disconnected
    )
    private val _bluetoothState = MutableStateFlow(
        when (this.bluetoothAdapter?.state) {
            STATE_OFF -> BluetoothState.OFF
            STATE_TURNING_ON -> BluetoothState.TURNING_ON
            STATE_ON -> BluetoothState.ON
            STATE_TURNING_OFF -> BluetoothState.TURNING_OFF
            else -> BluetoothState.UNKNOWN
        }
    )
    private val _incomingData = MutableSharedFlow<String>(extraBufferCapacity = 10)

    private val btStateReceiver = BluetoothStateBroadcastReceiver { newState ->
        _bluetoothState.update { newState }
    }

    init {
        context.registerReceiver(
            btStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
        dataStoreScope.launch {
            context.dataStore.data.collect {
                attempts = it[RECONNECTION_ATTEMPTS] ?: 3
                delay = it[DELAY_ATTEMPTS] ?: 1
            }
        }
    }

    private var bluetoothSocket: BluetoothSocket? = null
    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override val connectionState: Flow<ConnectionState>
        get() = _connectionState
    override val bluetoothState: StateFlow<BluetoothState>
        get() = _bluetoothState
    override val incomingData: Flow<String>
        get() = _incomingData

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun getPairedDevices(): List<BluetoothDeviceInformation> {
        return bluetoothAdapter?.bondedDevices?.map {
            BluetoothDeviceInformation(it.name, it.address)
        } ?: emptyList()
    }

    override suspend fun connect(device: BluetoothDeviceInformation) {
        withContext(Dispatchers.IO) {
            for (attempt in 1..attempts) {
                _connectionState.update { ConnectionState.Connecting }
                try {
                    val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)
                    bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(uuid)
                    bluetoothSocket?.connect()
                    break
                } catch (e: IOException) {
                    if (!currentCoroutineContext().isActive)
                        break
                    if (bluetoothAdapter?.isEnabled == false) {
                        handleBluetoothTurnedOff()
                        return@withContext
                    }
                    if (attempt <= attempts)
                        waitingToTryReconnect(attempt)
                    else
                        handleException(e)
                }
            }
            _connectionState.update { ConnectionState.Connected(device) }
            loopJob = launch { startListening() }
        }
    }

    override suspend fun startListening() {
        var attempt = 0
        val buffer = ByteArray(1024)
        var bytes: Int
        val inputStream: InputStream? = bluetoothSocket?.inputStream
        while (true) {
            try {
                withContext(Dispatchers.IO) {
                    bytes = inputStream?.read(buffer) ?: -1
                }
                if (bytes > 0) {
                    val data = String(buffer, 0, bytes)
                    _incomingData.emit(data)
                }
            } catch (e: IOException) {
                if (!currentCoroutineContext().isActive)
                    return
                if (bluetoothAdapter?.isEnabled == false) {
                    handleBluetoothTurnedOff()
                    break
                }
                if (attempt++ <= this.attempts)
                    waitingToTryReconnect(attempt)
                else
                    handleException(e)
            }
        }
    }

    override suspend fun sendCommand(
        command: CommandType,
        vararg values: Byte
    ) {
        for (attempt in 1..attempts) {
            try {
                val outputStream: OutputStream? = bluetoothSocket?.outputStream
                val payload = ByteArray(1 + values.size)
                payload[0] = command.byteValue
                values.copyInto(payload, 1)
                withContext(Dispatchers.IO) {
                    outputStream?.write(payload)
                }
                break
            } catch (e: IOException) {
                if (!currentCoroutineContext().isActive)
                    return
                if (bluetoothAdapter?.isEnabled == false) {
                    handleBluetoothTurnedOff()
                    break
                }
                if (attempt <= this.attempts)
                    waitingToTryReconnect(attempt)
                else
                    handleException(e)
            }
        }
    }

    override fun disconnect() {
        loopJob?.cancel()
        closeSocketSilently()
        _connectionState.update { ConnectionState.Disconnected }
    }

    override fun stopListeningBluetoothChanges() {
        context.unregisterReceiver(btStateReceiver)
    }

    private fun handleBluetoothTurnedOff() {
        _connectionState.update {
            ConnectionState.Error(R.string.bluetooth_has_turned_off)
        }
        _connectionState.update {
            ConnectionState.Disconnected
        }
    }

    private suspend fun waitingToTryReconnect(attempt: Int) {
        var currentSec = delay
        while (currentSec > 0) {
            _connectionState.update {
                ConnectionState.Reconnecting(
                    attempt,
                    currentSec
                )
            }
            delay((currentSec-- * 1000).milliseconds)
        }
    }

    private fun handleException(e: IOException) {
        closeSocketSilently()
        val errorMessageId = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
            && e is BluetoothSocketException
        ) {
            when (e.errorCode) {
                SOCKET_MANAGER_FAILURE -> R.string.bluetooth_socket_manager_error
                SOCKET_CLOSED -> R.string.bluetooth_socket_closed
                NULL_DEVICE -> R.string.bluetooth_null_device
                else -> R.string.bluetooth_generic_error
            }
        } else {
            R.string.bluetooth_generic_error
        }
        _connectionState.update {
            ConnectionState.Error(errorMessageId)
        }
        _connectionState.update {
            ConnectionState.Disconnected
        }
    }

    private fun closeSocketSilently() {
        try {
            bluetoothSocket?.close()
        } finally {
            bluetoothSocket = null
        }
    }

}