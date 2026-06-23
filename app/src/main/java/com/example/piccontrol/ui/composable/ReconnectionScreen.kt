package com.example.piccontrol.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.piccontrol.R
import com.example.piccontrol.theme.PICControlTheme

@Composable
fun ReconnectionScreen(
    modifier: Modifier = Modifier,
    attempt: Int,
    nextAttemptSec: Int
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.reconnecting_message),
            style = typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.attempt_and_seconds, attempt, nextAttemptSec),
            style = typography.bodyMedium
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ReconnectionScreenPreview() {
    PICControlTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {
            ReconnectionScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                attempt = 1,
                nextAttemptSec = 5
            )
        }
    }
}