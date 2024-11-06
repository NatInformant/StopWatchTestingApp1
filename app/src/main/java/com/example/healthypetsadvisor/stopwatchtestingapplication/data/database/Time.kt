package com.example.healthypetsadvisor.stopwatchtestingapplication.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "previous_time")
data class Time(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val timeStringValue: String,
    @ColumnInfo(defaultValue = "0")
    val timeIntValue: Int
)
