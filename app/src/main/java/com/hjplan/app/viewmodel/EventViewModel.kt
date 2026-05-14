package com.hjplan.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjplan.app.data.model.Event
import com.hjplan.app.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayEvents: StateFlow<List<Event>> = _selectedDate
        .flatMapLatest { date ->
            val start = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            repository.getEventsByDay(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<Event>> = repository.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<Event>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else repository.searchEvents(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insertEvent(event: Event) = viewModelScope.launch {
        repository.insertEvent(event)
    }

    fun updateEvent(event: Event) = viewModelScope.launch {
        repository.updateEvent(event)
    }

    fun deleteEvent(event: Event) = viewModelScope.launch {
        repository.deleteEvent(event)
    }

    fun toggleCompleted(event: Event) = viewModelScope.launch {
        repository.setCompleted(event.id, !event.isCompleted)
    }

    fun getEventsForMonth(year: Int, month: Int): Flow<List<Event>> {
        val start = LocalDate.of(year, month, 1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val end = LocalDate.of(year, month, 1).plusMonths(1)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return repository.getEventsByMonth(start, end)
    }
}
