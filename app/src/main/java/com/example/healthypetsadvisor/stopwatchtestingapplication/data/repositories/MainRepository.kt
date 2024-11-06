package com.example.healthypetsadvisor.stopwatchtestingapplication.data.repositories

import com.example.healthypetsadvisor.stopwatchtestingapplication.data.database.Time
import com.example.healthypetsadvisor.stopwatchtestingapplication.data.database.TimeDatabase
import com.example.healthypetsadvisor.stopwatchtestingapplication.data.model.PreviousTime

class MainRepository(private val dataBase: TimeDatabase) {

    suspend fun insertNewTimeToDb(timeStringValue: String, timeIntValue: Int) =
        dataBase.timeDao().insert(Time(0, timeStringValue, timeIntValue))

    suspend fun getPreviousTimeFromDb(): List<PreviousTime> =
        dataBase.timeDao().getAll().map { PreviousTime(it.timeStringValue, it.timeIntValue) }
    
    suspend fun clearTimeTableInDb() =
        dataBase.timeDao().deleteAllFromDb()
}
