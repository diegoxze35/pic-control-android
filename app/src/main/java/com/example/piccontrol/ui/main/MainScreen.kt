package com.example.piccontrol.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.piccontrol.R
import com.example.piccontrol.theme.PICControlTheme
import com.example.piccontrol.ui.composable.FanControlSection
import com.example.piccontrol.ui.composable.GaugeComponent
import com.example.piccontrol.ui.composable.StepperControlSection
import com.example.piccontrol.ui.composable.dialog.DeviceListDialog
import com.example.piccontrol.ui.state.UiState

@Composable
fun MainScreenRoute(viewModel: MainScreenViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    var hasPermissions by rememberSaveable {
        mutableStateOf(requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        })
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        hasPermissions = allGranted
    }

    if (hasPermissions) {
        val pairedDevices = viewModel.getPairedDevices()
        MainScreen(
            uiState = uiState,
            pairedDevices = pairedDevices,
            onDeviceSelected = { viewModel.connectToDevice(it) },
            onFanSpeedChange = { viewModel.setFanSpeed(it) },
            onMotorDirChange = { viewModel.setMotorDirection(it) },
            onMotorSpeedChange = { viewModel.setMotorSpeed(it) },
            onMotorMoveSend = { viewModel.sendMotorMove(it) },
            onDismissDeviceList = { viewModel.hideDeviceList() },
            onRequestDeviceList = { viewModel.showDeviceList() }
        )
    } else {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.grant_bluetooth_permissions), fontWeight = FontWeight.Bold)
                Button(onClick = { permissionLauncher.launch(requiredPermissions) }) {
                    Text(stringResource(R.string.grant_permissions))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.user_advice),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

}

@SuppressLint("SwitchIntDef")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: UiState,
    pairedDevices: List<BluetoothDevice>,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onFanSpeedChange: (Int) -> Unit,
    onMotorDirChange: (Boolean) -> Unit,
    onMotorSpeedChange: (Int) -> Unit,
    onMotorMoveSend: (Int) -> Unit,
    onDismissDeviceList: () -> Unit,
    onRequestDeviceList: () -> Unit
) {

    if (uiState.showDeviceList && !uiState.isConnected) {
        DeviceListDialog(
            isConnecting = uiState.isConnecting,
            devices = pairedDevices,
            onDeviceSelected = onDeviceSelected,
            onDismiss = onDismissDeviceList
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("PIC Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onRequestDeviceList) {
                        Icon(
                            imageVector = if (uiState.isConnected) Icons.Default.BluetoothConnected else Icons.Default.Bluetooth,
                            contentDescription = "Bluetooth",
                            tint = if (uiState.isConnected) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val configuration = LocalConfiguration.current
        val modifierToApply = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT, Configuration.ORIENTATION_UNDEFINED -> Column(
                modifier = modifierToApply,
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
                        GaugeComponent("Pot 1", uiState.p1, 1023, Icons.Default.ElectricBolt)
                        GaugeComponent("Pot 2", uiState.p2, 1023, Icons.Default.ElectricBolt)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GaugeComponent("Pot 3", uiState.p3, 1023, Icons.Default.ElectricBolt)
                        GaugeComponent("Pot 4", uiState.p4, 1023, Icons.Default.ElectricBolt)
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
                        isMotorMoving = uiState.isMotorMoving,
                        onSpeedChange = { onMotorSpeedChange(it.toInt()) },
                        onDirChange = { onMotorDirChange(it) },
                        onSendMove = { onMotorMoveSend(it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Configuration.ORIENTATION_LANDSCAPE -> Row(
                modifier = modifierToApply,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                        .weight(0.5f)
                ) {
                    GaugeComponent("Pot 1", uiState.p1, 1023, Icons.Default.ElectricBolt)
                    GaugeComponent("Pot 2", uiState.p2, 1023, Icons.Default.ElectricBolt)
                    GaugeComponent("Pot 3", uiState.p3, 1023, Icons.Default.ElectricBolt)
                    GaugeComponent("Pot 4", uiState.p4, 1023, Icons.Default.ElectricBolt)
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
                        isMotorMoving = uiState.isMotorMoving,
                        onSpeedChange = { onMotorSpeedChange(it.toInt()) },
                        onDirChange = { onMotorDirChange(it) },
                        onSendMove = { onMotorMoveSend(it) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "portrait")
@Composable
fun MainScreenPreview() {
    PICControlTheme {
        MainScreen(
            uiState = UiState(
                p1 = 512,
                p2 = 256,
                p3 = 800,
                p4 = 100,
                isConnected = true,
                fanSpeed = 120,
                motorDirection = true,
                motorSpeed = 50
            ),
            pairedDevices = emptyList(),
            onDeviceSelected = {},
            onFanSpeedChange = {},
            onMotorDirChange = {},
            onMotorSpeedChange = {},
            onMotorMoveSend = {},
            onDismissDeviceList = {},
            onRequestDeviceList = {}
        )
    }
}

@Preview(
    showBackground = true, showSystemUi = true, name = "landscape",
    device = "spec:parent=pixel_5,orientation=landscape"
)
@Composable
fun MainScreenLandPreview() {
    PICControlTheme {
        MainScreen(
            uiState = UiState(
                p1 = 512,
                p2 = 256,
                p3 = 800,
                p4 = 100,
                isConnected = true,
                fanSpeed = 120,
                motorDirection = true,
                motorSpeed = 50
            ),
            pairedDevices = emptyList(),
            onDeviceSelected = {},
            onFanSpeedChange = {},
            onMotorDirChange = {},
            onMotorSpeedChange = {},
            onMotorMoveSend = {},
            onDismissDeviceList = {},
            onRequestDeviceList = {}
        )
    }
}
