package com.example.healthypetsadvisor.stopwatchtestingapplication.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TimeDao {
    @Query("SELECT * FROM previous_time")
    suspend fun getAll(): List<Time>

    @Insert
    suspend fun insert(time: Time)

    @Query("DELETE FROM previous_time")
    suspend fun deleteAllFromDb()
}
