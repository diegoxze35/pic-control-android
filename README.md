# PIC Control Android App

An Android application built with Jetpack Compose to control and monitor a PIC microcontroller via Classic Bluetooth (RFCOMM).

## Features

- **Bluetooth Connectivity:** Pair and connect with Bluetooth modules (e.g., HC-05, HC-06) connected to a PIC microcontroller. Includes modern Android 12+ runtime permission handling.
- **Real-time Monitoring:** View live readings from up to 4 potentiometers (Pot 1 - 4) with visual gauge indicators.
- **Fan Control:** Control the speed of a cooling fan.
- **Stepper Motor Control:**
  - Control motor speed and direction.
  - Send specific move commands (in degrees) to a stepper motor.
- **Adaptive UI:** Responsive layout adapting to both Portrait and Landscape orientations.

## Demo
https://github.com/user-attachments/assets/9a0cdeec-df6b-483a-9340-731da9cb6147

## Tech Stack

- **UI:** Jetpack Compose, Material Design 3
- **Architecture:** MVVM (Model-View-ViewModel)
- **Dependency Injection:** Koin
- **Asynchronous Programming:** Kotlin Coroutines & Flow
- **Bluetooth:** Android Classic Bluetooth API (RFCOMM)

## Setup and Requirements

1. **Android Studio:** Recommended to use the latest version.
2. **Min SDK:** Android 7.0 (API 24)
3. **Target SDK:** Android 15 (API 36)
4. **Permissions:** The app requests `BLUETOOTH_CONNECT` and `BLUETOOTH_SCAN` on Android 12+ or `ACCESS_FINE_LOCATION` on older versions to discover and connect to paired devices.

## Running the Project

1. Clone this repository.
2. Open the project in Android Studio.
3. Build and run the project on a physical Android device (Bluetooth does not work on standard emulators).
4. Pair your Android device with the Bluetooth module (e.g., HC-05) via the system settings before launching the app or connecting.

## Hardware (Microcontroller side)

This application expects the microcontroller to communicate via a serial Bluetooth module using comma-separated key-value pairs for incoming data and specific byte protocols for outgoing commands.

- **Incoming Data Format:** `P1:512,P2:256,P3:800,P4:100\n`
- **Outgoing Commands:**
  - Fan Speed: `[0x01][SpeedByte]`
  - Motor Dir: `[0x02][DirByte]`
  - Motor Speed: `[0x03][SpeedByte]`
  - Motor Move: `[0x04][HighByte][LowByte]`
