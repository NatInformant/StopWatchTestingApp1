package com.example.healthypetsadvisor.stopwatchtestingapplication.di.modules

import com.example.healthypetsadvisor.stopwatchtestingapplication.data.repositories.MainRepository
import org.koin.dsl.module

object DataModule {
    val dataModule = module {
        single<MainRepository> { MainRepository(dataBase = get()) }
    }
}
