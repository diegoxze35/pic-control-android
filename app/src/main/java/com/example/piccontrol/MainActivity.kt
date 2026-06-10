package com.example.piccontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.piccontrol.theme.PICControlTheme
import com.example.piccontrol.ui.main.MainScreenRoute
import com.example.piccontrol.ui.main.MainScreenViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainScreenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PICControlTheme {
                MainScreenRoute(viewModel = viewModel)
            }
        }
    }

    override fun finish() {
        super.finish()
        viewModel.disconnect()
    }
}
