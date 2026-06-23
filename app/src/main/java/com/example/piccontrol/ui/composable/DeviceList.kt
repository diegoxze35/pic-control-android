package com.example.piccontrol.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.piccontrol.R
import com.example.piccontrol.domain.BluetoothDeviceInformation
import com.example.piccontrol.theme.PICControlTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceList(
    modifier: Modifier = Modifier,
    devices: List<BluetoothDeviceInformation>,
    onDeviceSelected: (BluetoothDeviceInformation) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(devices, key = { it.address }) { device ->
            ListItem(
                headlineContent = {
                    Text(device.name ?: stringResource(id = R.string.no_name_device))
                },
                supportingContent = { Text(device.address) },
                modifier = Modifier.clickable { onDeviceSelected(device) }
            )

        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun DeviceListPreview() {
    PICControlTheme {
        Scaffold {
            DeviceList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                devices = listOf(
                    BluetoothDeviceInformation("Device 1", "00:11:22:33:44:55"),
                    BluetoothDeviceInformation("Device 2", "66:77:88:99:AA:BB"),
                    BluetoothDeviceInformation(null, "CC:DD:EE:FF:00:11")
                ),
                onDeviceSelected = {}
            )
        }
    }
}
