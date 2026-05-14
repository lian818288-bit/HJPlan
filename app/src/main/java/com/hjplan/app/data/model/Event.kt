package com.hjplan.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val note: String = "",
    val location: String = "",
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val isAllDay: Boolean = false,
    val color: String = "#1A73E8",   // hex color string
    val reminderMinutes: Int = 15,   // 0 = no reminder
    val isCompleted: Boolean = false,
    val repeatType: String = "NONE", // NONE / DAILY / WEEKLY / MONTHLY
    val createdAt: Long = System.currentTimeMillis()
)
