package com.example.piccontrol.di

import com.example.piccontrol.data.BluetoothController
import com.example.piccontrol.ui.main.MainScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { BluetoothController(androidContext()) }
    viewModelOf(::MainScreenViewModel)
}
