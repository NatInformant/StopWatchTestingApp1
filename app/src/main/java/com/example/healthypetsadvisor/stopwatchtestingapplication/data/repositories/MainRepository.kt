package com.example.healthypetsadvisor.stopwatchtestingapplication.data.repositories

import com.example.healthypetsadvisor.stopwatchtestingapplication.data.database.Time
import com.example.healthypetsadvisor.stopwatchtestingapplication.data.database.TimeDatabase

class MainRepository(private val dataBase: TimeDatabase) {

    suspend fun insertNewTimeToDb(timeValue: String) =
        dataBase.timeDao().insert(Time(0, timeValue))

    suspend fun getPreviousTimeFromDb(): List<String> =
        dataBase.timeDao().getAll().map { it.time }

    suspend fun clearTimeTableInDb() =
        dataBase.timeDao().deleteAllFromDb()
}
