package com.example.piccontrol

import android.app.Application
import com.example.piccontrol.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PicApplication)
            modules(appModule)
        }
    }
}
