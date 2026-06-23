package com.example.piccontrol.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.piccontrol.domain.BluetoothDeviceInformation
import com.example.piccontrol.domain.ConnectionState
import com.example.piccontrol.domain.SensorData
import com.example.piccontrol.theme.PICControlTheme
import com.example.piccontrol.ui.state.UiState

@Composable
fun ConnectedScreen(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onFanSpeedChange: (Int) -> Unit,
    onMotorSpeedChange: (Int) -> Unit,
    onMotorDirChange: (Boolean) -> Unit,
    onMotorMoveSend: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT, Configuration.ORIENTATION_UNDEFINED -> Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GaugeComponent("Pot 1", uiState.sensorData.p1, 1023, Icons.Default.ElectricBolt)
                    GaugeComponent("Pot 2", uiState.sensorData.p2, 1023, Icons.Default.ElectricBolt)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GaugeComponent("Pot 3", uiState.sensorData.p3, 1023, Icons.Default.ElectricBolt)
                    GaugeComponent("Pot 4", uiState.sensorData.p4, 1023, Icons.Default.ElectricBolt)
                }
                Spacer(modifier = Modifier.height(24.dp))

                FanControlSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    speed = uiState.fanSpeed,
                    onSpeedChange = { onFanSpeedChange(it.toInt()) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                StepperControlSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    speed = uiState.motorSpeed,
                    isRightDir = uiState.motorDirection,
                    degrees = uiState.motorDegrees,
                    isMotorMoving = uiState.sensorData.isMotorMoving,
                    onSpeedChange = { onMotorSpeedChange(it.toInt()) },
                    onDirChange = { onMotorDirChange(it) },
                    onSendMove = { onMotorMoveSend(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Configuration.ORIENTATION_LANDSCAPE -> Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
                    .weight(0.5f)
            ) {
                GaugeComponent("Pot 1", uiState.sensorData.p1, 1023, Icons.Default.ElectricBolt)
                GaugeComponent("Pot 2", uiState.sensorData.p2, 1023, Icons.Default.ElectricBolt)
                GaugeComponent("Pot 3", uiState.sensorData.p3, 1023, Icons.Default.ElectricBolt)
                GaugeComponent("Pot 4", uiState.sensorData.p4, 1023, Icons.Default.ElectricBolt)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .weight(2f)
                    .verticalScroll(rememberScrollState())
                    .padding(end = 16.dp)
            ) {
                FanControlSection(
                    modifier = Modifier.fillMaxWidth(),
                    speed = uiState.fanSpeed,
                    onSpeedChange = { onFanSpeedChange(it.toInt()) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                StepperControlSection(
                    modifier = Modifier.padding(8.dp),
                    speed = uiState.motorSpeed,
                    isRightDir = uiState.motorDirection,
                    degrees = uiState.motorDegrees,
                    isMotorMoving = uiState.sensorData.isMotorMoving,
                    onSpeedChange = { onMotorSpeedChange(it.toInt()) },
                    onDirChange = { onMotorDirChange(it) },
                    onSendMove = { onMotorMoveSend(it) }
                )
            }
        }

        else -> Unit
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ConnectedPreview() {
    PICControlTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            ConnectedScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                uiState = UiState(
                    sensorData = SensorData(
                        p1 = 512,
                        p2 = 256,
                        p3 = 768,
                        p4 = 1023,
                        isMotorMoving = false,
                    ),
                    fanSpeed = 128,
                    motorDirection = true,
                    motorSpeed = 64,
                    motorDegrees = 90,
                    connectionState = ConnectionState.Connected(
                        BluetoothDeviceInformation(
                            name = "Mock Device",
                            address = "00:11:22:33:44:55"
                        )
                    )
                ),
                onFanSpeedChange = {},
                onMotorSpeedChange = {},
                onMotorDirChange = {},
                onMotorMoveSend = {},
            )
        }
    }

}