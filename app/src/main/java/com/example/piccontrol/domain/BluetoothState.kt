package com.example.piccontrol.domain

/*STATE_OFF,
  STATE_TURNING_ON,
  STATE_ON,
  STATE_TURNING_OFF*/
enum class BluetoothState {
    ON,
    TURNING_ON,
    OFF,
    TURNING_OFF,
    UNKNOWN
}