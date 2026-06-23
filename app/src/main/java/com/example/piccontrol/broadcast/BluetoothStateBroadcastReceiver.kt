package com.example.piccontrol.broadcast

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.STATE_OFF
import android.bluetooth.BluetoothAdapter.STATE_ON
import android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF
import android.bluetooth.BluetoothAdapter.STATE_TURNING_ON
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.piccontrol.domain.BluetoothState

class BluetoothStateBroadcastReceiver(
    private val onStateChanged: (BluetoothState) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val newState =
                    intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                val domainState = when (newState) {
                    STATE_OFF -> BluetoothState.OFF
                    STATE_TURNING_ON -> BluetoothState.TURNING_ON
                    STATE_ON -> BluetoothState.ON
                    STATE_TURNING_OFF -> BluetoothState.TURNING_OFF
                    else -> BluetoothState.UNKNOWN
                }
                onStateChanged(domainState)
            }
        }
    }
}