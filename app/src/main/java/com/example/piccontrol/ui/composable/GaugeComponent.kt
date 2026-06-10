package com.example.piccontrol.ui.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GaugeComponent(
    label: String,
    value: Int,
    maxValue: Int,
    icon: ImageVector
) {
    val animatedValue by animateFloatAsState(targetValue = value.toFloat(), label = "gauge")
    val sweepAngle = (animatedValue / maxValue) * 240f
    val gaugeBarColor = MaterialTheme.colorScheme.primary
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
            Canvas(modifier = Modifier.size(100.dp)) {
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    startAngle = 150f,
                    sweepAngle = 240f,
                    useCenter = false,
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = gaugeBarColor,
                    startAngle = 150f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.DarkGray
                )
                Text(text = "$value", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}