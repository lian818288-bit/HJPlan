package com.hjplan.app.di

import android.content.Context
import androidx.room.Room
import com.hjplan.app.data.db.EventDao
import com.hjplan.app.data.db.HJPlanDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HJPlanDatabase {
        return Room.databaseBuilder(
            context,
            HJPlanDatabase::class.java,
            HJPlanDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideEventDao(database: HJPlanDatabase): EventDao = database.eventDao()
}
