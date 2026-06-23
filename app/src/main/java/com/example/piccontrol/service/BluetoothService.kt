package com.example.piccontrol.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.piccontrol.R
import com.example.piccontrol.data.BluetoothController
import com.example.piccontrol.domain.BluetoothDeviceInformation
import com.example.piccontrol.domain.BluetoothState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class BluetoothService : Service() {

    private val bluetoothController: BluetoothController by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        serviceScope.launch {
            bluetoothController.bluetoothState.collect { state ->
                if (state == BluetoothState.TURNING_OFF || state == BluetoothState.OFF)
                    stopSelf()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.bluetooth_service_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val EXTRA_DEVICE_ADDRESS = "device_address"
        const val EXTRA_DEVICE_NAME = "device_name"
        private const val CHANNEL_ID = "bluetooth_service_channel"
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME)
        val deviceAddress = intent.getStringExtra(EXTRA_DEVICE_ADDRESS).orEmpty()
        val deviceInformation = BluetoothDeviceInformation(
            name = deviceName,
            address = deviceAddress
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setContentTitle(getString(R.string.connected_to, deviceName))
            .setOngoing(true)
            .build()
        ServiceCompat.startForeground(
            /* service = */ this,
            /* id = */ 100, // Cannot be 0
            /* notification = */ notification,
            /* foregroundServiceType = */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            else
                0
        )
        serviceScope.launch {
            bluetoothController.connect(deviceInformation)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothController.disconnect()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

}
