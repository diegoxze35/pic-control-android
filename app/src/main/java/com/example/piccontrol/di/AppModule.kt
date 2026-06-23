package com.example.piccontrol.di

import com.example.piccontrol.data.BluetoothController
import com.example.piccontrol.data.SensorDataRepository
import com.example.piccontrol.data.impl.BluetoothControllerImpl
import com.example.piccontrol.data.impl.SensorDataRepositoryImpl
import com.example.piccontrol.ui.main.MainScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.binds
import org.koin.dsl.module

val appModule = module {
    single { BluetoothControllerImpl(androidContext()) } binds arrayOf(BluetoothController::class)
    single { SensorDataRepositoryImpl(get(), CoroutineScope(Dispatchers.IO + SupervisorJob())) } binds arrayOf(SensorDataRepository::class)
    viewModelOf(::MainScreenViewModel)
}
