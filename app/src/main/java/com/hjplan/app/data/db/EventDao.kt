package com.hjplan.app.data.db

import androidx.room.*
import com.hjplan.app.data.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY startTimeMillis ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("""
        SELECT * FROM events 
        WHERE startTimeMillis >= :dayStart AND startTimeMillis < :dayEnd 
        ORDER BY startTimeMillis ASC
    """)
    fun getEventsByDay(dayStart: Long, dayEnd: Long): Flow<List<Event>>

    @Query("""
        SELECT * FROM events 
        WHERE startTimeMillis >= :monthStart AND startTimeMillis < :monthEnd 
        ORDER BY startTimeMillis ASC
    """)
    fun getEventsByMonth(monthStart: Long, monthEnd: Long): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Long): Event?

    @Query("""
        SELECT * FROM events 
        WHERE title LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%'
        ORDER BY startTimeMillis ASC
    """)
    fun searchEvents(query: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE isCompleted = 0 ORDER BY startTimeMillis ASC")
    fun getPendingEvents(): Flow<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event): Long

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Long)

    @Query("UPDATE events SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)
}
