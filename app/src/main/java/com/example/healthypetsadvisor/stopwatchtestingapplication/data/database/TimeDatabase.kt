package com.example.healthypetsadvisor.stopwatchtestingapplication.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

@Database(
    entities = [Time::class],
    autoMigrations = [AutoMigration(from = 4, to = 5, spec = TimeDatabase.MyAutoMigrationSpec::class)],
    exportSchema = true,
    version = 5
)
abstract class TimeDatabase : RoomDatabase() {
    abstract fun timeDao(): TimeDao
    @RenameColumn.Entries(
        RenameColumn(
            tableName = "previous_time",
            fromColumnName = "time",
            toColumnName = "timeStringValue"
        )
    )
    class MyAutoMigrationSpec : AutoMigrationSpec
}
