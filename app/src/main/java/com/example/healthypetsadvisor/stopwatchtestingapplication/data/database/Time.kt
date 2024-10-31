package com.example.healthypetsadvisor.stopwatchtestingapplication.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "previous_time")
data class Time(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val time: String
)
