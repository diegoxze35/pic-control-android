package com.example.piccontrol.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.piccontrol.R
import com.example.piccontrol.domain.BluetoothState
import com.example.piccontrol.domain.ConnectionState
import com.example.piccontrol.ui.composable.ConnectedScreen
import com.example.piccontrol.ui.composable.ConnectingScreen
import com.example.piccontrol.ui.composable.DeviceList
import com.example.piccontrol.ui.composable.GrantPermissionScreen
import com.example.piccontrol.ui.composable.ReconnectionScreen
import com.example.piccontrol.ui.state.AppBarState
import com.example.piccontrol.utils.ConnectedDeviceAppBar
import com.example.piccontrol.utils.Empty
import com.example.piccontrol.utils.SimpleTitleBar
import com.example.piccontrol.utils.requestTurnOnBluetooth
import com.example.piccontrol.utils.startBluetoothCommunicationService
import com.example.piccontrol.utils.titleWithIcon
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenRoute() {
    val context = LocalContext.current
    val activity = LocalActivity.current

    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
        if (allGranted) {
            activity?.requestTurnOnBluetooth()
        }
        hasPermissions = allGranted
    }
    var appBarState by remember { mutableStateOf(AppBarState()) }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { appBarState.title?.invoke() },
                actions = { appBarState.actions?.invoke(this) }
            )
        }
    ) { paddingValues ->
        if (hasPermissions) {
            val viewModel = koinViewModel<MainScreenViewModel>()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            when (val connectionState = uiState.connectionState) {
                is ConnectionState.Connected -> {
                    appBarState = connectionState.device.ConnectedDeviceAppBar
                    ConnectedScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        uiState = uiState,
                        onFanSpeedChange = viewModel::setFanSpeed,
                        onMotorSpeedChange = viewModel::setMotorSpeed,
                        onMotorDirChange = viewModel::setMotorDirection,
                        onMotorMoveSend = viewModel::sendMotorMove
                    )
                }

                ConnectionState.Connecting -> {
                    appBarState = AppBarState.Empty
                    ConnectingScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }

                ConnectionState.Disconnected -> {
                    appBarState = R.string.select_hc_05.SimpleTitleBar
                    val btState by viewModel.btState.collectAsStateWithLifecycle()
                    when (btState) {
                        BluetoothState.ON -> {
                            val pairedDevices = viewModel.getPairedDevices()
                            DeviceList(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                                devices = pairedDevices
                            ) { device ->
                                context.startBluetoothCommunicationService(device)
                            }
                        }

                        BluetoothState.TURNING_ON, BluetoothState.TURNING_OFF -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        BluetoothState.OFF -> Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BluetoothDisabled,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.bluetooth_off_message),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { activity?.requestTurnOnBluetooth() }) {
                                Text(text = stringResource(R.string.turn_on_bluetooth))
                            }
                        }

                        BluetoothState.UNKNOWN -> Unit
                    }
                }

                is ConnectionState.Error -> {
                    val message = stringResource(connectionState.messageId)
                    LaunchedEffect(Unit) {
                        snackbarHostState.showSnackbar(
                            message = message
                        )
                    }
                }

                is ConnectionState.Reconnecting -> {
                    LaunchedEffect(Unit) {
                        var icon = Icons.Default.Bluetooth
                        while (true) {
                            appBarState = AppBarState.titleWithIcon(
                                title = R.string.reconnecting,
                                icon = icon
                            )
                            icon = if (icon == Icons.Default.Bluetooth)
                                Icons.Default.BluetoothConnected
                            else
                                Icons.Default.Bluetooth
                            delay(500.milliseconds)
                        }
                    }
                    val (delay, attempt) = connectionState
                    ReconnectionScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        attempt = attempt,
                        nextAttemptSec = delay
                    )
                }
            }
        } else {
            appBarState = AppBarState.Empty
            GrantPermissionScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                permissionLauncher.launch(requiredPermissions)
            }
        }
    }
}
