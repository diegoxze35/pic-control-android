package com.example.piccontrol.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
fun ConnectingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.connecting),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ConnectingPreview() {
    PICControlTheme {
        Scaffold {
            ConnectingScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            )
        }
    }
}