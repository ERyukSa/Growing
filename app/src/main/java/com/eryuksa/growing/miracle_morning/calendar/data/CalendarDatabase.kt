package com.eryuksa.growing.miracle_morning.calendar.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.eryuksa.growing.miracle_morning.model.MiracleStamp

@Database(entities = [MiracleStamp::class], version = 1)
abstract class CalendarDatabase : RoomDatabase() {

    abstract fun calendarDao(): CalendarDao

}

/*
val migration_3_4 = object: Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}
*/