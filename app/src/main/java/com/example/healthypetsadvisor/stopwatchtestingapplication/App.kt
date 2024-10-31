package com.example.healthypetsadvisor.stopwatchtestingapplication

import android.app.Application
import com.example.healthypetsadvisor.stopwatchtestingapplication.di.modules.DataBaseModule
import com.example.healthypetsadvisor.stopwatchtestingapplication.di.modules.DataModule
import com.example.healthypetsadvisor.stopwatchtestingapplication.di.modules.DomainModule
import com.example.healthypetsadvisor.stopwatchtestingapplication.di.modules.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.UUID

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.DEBUG)
            modules(
                ViewModelModule.viewModelModule,
                DomainModule.domainModule,
                DataModule.dataModule,
                DataBaseModule.dataBaseModule
            )
        }
    }
}
