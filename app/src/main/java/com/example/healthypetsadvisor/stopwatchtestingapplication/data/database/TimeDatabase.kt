package com.example.healthypetsadvisor.stopwatchtestingapplication.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [Time::class],
    /*autoMigrations = [AutoMigration(from = 3, to = 4,spec = TimeDatabase.MyAutoGenerationSpec::class)],*/
    exportSchema = true,
    version = 4
)
abstract class TimeDatabase : RoomDatabase() {
    abstract fun timeDao(): TimeDao
    /*@DeleteColumn.Entries(
        DeleteColumn(
            tableName = "previous_time",
            columnName = "uselessValue"
        ),
        DeleteColumn("previous_time", "uselessValue2")
    )
    class MyAutoGenerationSpec : AutoMigrationSpec*/
}
