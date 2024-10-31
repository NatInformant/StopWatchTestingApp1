package com.example.healthypetsadvisor.stopwatchtestingapplication.di.modules

import androidx.room.Room
import com.example.healthypetsadvisor.stopwatchtestingapplication.data.database.TimeDatabase
import org.koin.dsl.module

object DataBaseModule {
    val dataBaseModule = module {
        single<TimeDatabase> {
            Room.databaseBuilder(
                context = get(),
                TimeDatabase::class.java, "database-name"
            ).build()
        }
    }
}
