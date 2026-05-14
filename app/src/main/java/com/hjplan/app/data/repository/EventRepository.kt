package com.hjplan.app.data.repository

import com.hjplan.app.data.db.EventDao
import com.hjplan.app.data.model.Event
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val dao: EventDao
) {
    fun getAllEvents(): Flow<List<Event>> = dao.getAllEvents()

    fun getEventsByDay(dayStart: Long, dayEnd: Long): Flow<List<Event>> =
        dao.getEventsByDay(dayStart, dayEnd)

    fun getEventsByMonth(monthStart: Long, monthEnd: Long): Flow<List<Event>> =
        dao.getEventsByMonth(monthStart, monthEnd)

    fun searchEvents(query: String): Flow<List<Event>> = dao.searchEvents(query)

    fun getPendingEvents(): Flow<List<Event>> = dao.getPendingEvents()

    suspend fun getEventById(id: Long): Event? = dao.getEventById(id)

    suspend fun insertEvent(event: Event): Long = dao.insertEvent(event)

    suspend fun updateEvent(event: Event) = dao.updateEvent(event)

    suspend fun deleteEvent(event: Event) = dao.deleteEvent(event)

    suspend fun setCompleted(id: Long, completed: Boolean) = dao.setCompleted(id, completed)
}
