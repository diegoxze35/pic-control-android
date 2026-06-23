package com.example.piccontrol.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsBluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.piccontrol.R
import com.example.piccontrol.theme.PICControlTheme

@Composable
fun GrantPermissionScreen(
    modifier: Modifier = Modifier,
    onGrantPermission: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(64.dp),
            imageVector = Icons.Default.SettingsBluetooth,
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.grant_bluetooth_permissions),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onGrantPermission) {
            Text(text = stringResource(id = R.string.grant_permissions))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GrantPermissionScreenPreview() {
    PICControlTheme {
        Scaffold {
            GrantPermissionScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {}
        }
    }
}
