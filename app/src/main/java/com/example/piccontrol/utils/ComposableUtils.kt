package com.example.piccontrol.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.piccontrol.R
import com.example.piccontrol.domain.BluetoothDeviceInformation
import com.example.piccontrol.ui.state.AppBarState

val BluetoothDeviceInformation.ConnectedDeviceAppBar: AppBarState
    get() = AppBarState(
        title = {
            Column {
                Row {
                    Text(
                        stringResource(
                            R.string.connected_to,
                            this@ConnectedDeviceAppBar.name
                                ?: stringResource(R.string.no_name_device)
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Default.BluetoothConnected,
                        contentDescription = null
                    )
                }
                Text(
                    this@ConnectedDeviceAppBar.address,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        },
        actions = {
            val context = LocalContext.current
            Button(onClick = { context.stopBluetoothCommunicationService() }) {
                Text(stringResource(R.string.disconnect))
                Icon(
                    imageVector = Icons.Default.BluetoothDisabled,
                    contentDescription = null
                )
            }
        }
    )

val AppBarState.Companion.Empty: AppBarState
    get() = AppBarState(title = null, actions = null)

val (@receiver:StringRes Int).SimpleTitleBar: AppBarState
    get() = AppBarState(
        title = {
            Text(
                text = stringResource(this),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = null
    )

fun AppBarState.Companion.titleWithIcon(@StringRes title: Int, icon: ImageVector): AppBarState {
    return AppBarState(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(title))
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            }
        }
    )
}

