package com.example.piccontrol.di

import com.example.piccontrol.data.BluetoothController
import com.example.piccontrol.data.impl.BluetoothControllerImpl
import com.example.piccontrol.ui.main.MainScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.binds
import org.koin.dsl.module

val appModule = module {
    single { BluetoothControllerImpl(androidContext()) } binds arrayOf(BluetoothController::class)
    viewModelOf(::MainScreenViewModel)
}
