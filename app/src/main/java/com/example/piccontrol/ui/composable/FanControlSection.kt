package com.example.piccontrol.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.piccontrol.R
import com.example.piccontrol.ui.icon.mode_fan


@Composable
fun FanControlSection(modifier: Modifier = Modifier, speed: Int, onSpeedChange: (Float) -> Unit) {
    val angularVelocity = (speed.toFloat() / 255f) * 360f
    val currentVelocity by rememberUpdatedState(angularVelocity)
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var startTimeNanos = withFrameNanos { it }
        while (true) {
            val currentTimeNanos = withFrameNanos { it }
            val dtSeconds = (currentTimeNanos - startTimeNanos) / 1_000_000_000f
            rotationAngle = (rotationAngle + currentVelocity * dtSeconds) % 360f
            startTimeNanos = currentTimeNanos
        }
    }
    Card(
        modifier = modifier.then(Modifier.padding(8.dp).height(100.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = mode_fan,
                    contentDescription = "Fan",
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(rotationAngle)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.fan, speed),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Slider(
                value = speed.toFloat(),
                onValueChange = onSpeedChange,
                valueRange = 0f..255f,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}