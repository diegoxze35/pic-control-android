package com.example.piccontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.piccontrol.theme.PICControlTheme
import com.example.piccontrol.ui.main.MainScreenRoute

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PICControlTheme {
                MainScreenRoute()
            }
        }
    }

}
