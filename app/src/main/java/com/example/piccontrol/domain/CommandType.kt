package com.example.piccontrol.domain

enum class CommandType(val byteValue: Byte) {
    FAN_SPEED(0x01),
    MOTOR_DIR(0x02),
    MOTOR_SPEED(0x03),
    MOTOR_MOVE(0x04)
}