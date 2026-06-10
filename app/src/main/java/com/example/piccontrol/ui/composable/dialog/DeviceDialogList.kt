package com.example.piccontrol.ui.composable.dialog

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.piccontrol.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListDialog(
    isConnecting: Boolean,
    devices: List<BluetoothDevice>,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_hc_05)) },
        text = {
            if (isConnecting) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (devices.isEmpty()) {
                        Text(stringResource(R.string.no_devices))
                    } else {
                        devices.forEach { device ->
                            @SuppressLint("MissingPermission")
                            ListItem(
                                headlineContent = {
                                    Text(
                                        device.name ?: stringResource(R.string.unknown_device)
                                    )
                                },
                                supportingContent = { Text(device.address) },
                                modifier = Modifier.clickable { onDeviceSelected(device) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isConnecting)
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) }
            else
                Text(stringResource(R.string.connecting))
        }
    )
}
