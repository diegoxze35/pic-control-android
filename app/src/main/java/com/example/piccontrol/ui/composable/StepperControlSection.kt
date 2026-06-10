package com.example.piccontrol.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.piccontrol.R

@Composable
fun StepperControlSection(
    modifier: Modifier = Modifier,
    speed: Int,
    isRightDir: Boolean,
    degrees: Int,
    isMotorMoving: Boolean,
    onSpeedChange: (Float) -> Unit,
    onDirChange: (Boolean) -> Unit,
    onSendMove: (Int) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var tempDegrees by rememberSaveable { mutableStateOf(degrees.toString()) }
    var currentDegrees by rememberSaveable { mutableIntStateOf(degrees) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            title = { Text(stringResource(R.string.set_rotation_degrees)) },
            text = {
                OutlinedTextField(
                    value = tempDegrees,
                    onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) tempDegrees = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(stringResource(R.string.degrees)) },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    val deg = tempDegrees.toIntOrNull() ?: 0
                    currentDegrees = deg
                    showDialog = false
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Motor",
                        modifier = Modifier.size(32.dp),
                        tint = if (isMotorMoving) Color(0xFFE91E63) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = stringResource(R.string.step_motor), style = MaterialTheme.typography.titleMedium)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("L")
                    Switch(
                        checked = isRightDir,
                        onCheckedChange = onDirChange,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        enabled = !isMotorMoving
                    )
                    Text("R")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = stringResource(R.string.speed, speed), style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = speed.toFloat(),
                onValueChange = onSpeedChange,
                valueRange = 0f..255f,
                modifier = Modifier.padding(horizontal = 16.dp),
                enabled = !isMotorMoving
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = stringResource(R.string.degrees_to_rotate, currentDegrees), style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        tempDegrees = currentDegrees.toString()
                        showDialog = true
                    },
                    enabled = !isMotorMoving
                ) {
                    Text(stringResource(R.string.set_rotation_degrees))
                }

                Button(
                    onClick = { onSendMove(currentDegrees) },
                    enabled = !isMotorMoving && currentDegrees > 0
                ) {
                    Text(if (isMotorMoving) stringResource(R.string.moving) else stringResource(R.string.send))
                }
            }
        }
    }
}
