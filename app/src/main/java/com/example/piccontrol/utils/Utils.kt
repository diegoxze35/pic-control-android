package com.example.piccontrol.utils

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import com.example.piccontrol.domain.BluetoothDeviceInformation
import com.example.piccontrol.service.BluetoothService

@RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
fun Activity.requestTurnOnBluetooth() {
    val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    if (bluetoothAdapter?.isEnabled == false) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, 0)
    }
}

fun Context.startBluetoothCommunicationService(device: BluetoothDeviceInformation) {
    val intent = Intent(this, BluetoothService::class.java).apply {
        putExtra(BluetoothService.EXTRA_DEVICE_NAME, device.name)
        putExtra(BluetoothService.EXTRA_DEVICE_ADDRESS, device.address)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}

fun Context.stopBluetoothCommunicationService() {
    val intent = Intent(this, BluetoothService::class.java)
    stopService(intent)
}