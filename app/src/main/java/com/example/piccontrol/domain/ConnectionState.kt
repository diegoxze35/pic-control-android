package com.example.piccontrol.domain

import androidx.annotation.StringRes

sealed interface ConnectionState {
    data object Connecting : ConnectionState
    data class Connected(val device: BluetoothDeviceInformation) : ConnectionState
    data object Disconnected : ConnectionState
    data class Reconnecting(val attempt: Int, val delay: Int) : ConnectionState
    data class Error(@get:StringRes val messageId: Int) : ConnectionState
}
