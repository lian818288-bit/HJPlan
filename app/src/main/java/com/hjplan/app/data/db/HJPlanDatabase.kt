package com.hjplan.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hjplan.app.data.model.Event

@Database(
    entities = [Event::class],
    version = 1,
    exportSchema = false
)
abstract class HJPlanDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        const val DATABASE_NAME = "hjplan.db"
    }
}
