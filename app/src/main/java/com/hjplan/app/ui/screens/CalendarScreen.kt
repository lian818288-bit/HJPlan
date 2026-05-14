package com.hjplan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hjplan.app.data.model.Event
import com.hjplan.app.ui.theme.HJBackground
import com.hjplan.app.ui.theme.HJBlue
import com.hjplan.app.ui.theme.HJLabel
import com.hjplan.app.ui.theme.HJSecondaryLabel
import com.hjplan.app.viewmodel.EventViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(
    onEventClick: (Long) -> Unit,
    viewModel: EventViewModel = hiltViewModel()
) {
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }
    val selectedDate by viewModel.selectedDate.collectAsState()
    val monthEvents by viewModel.getEventsForMonth(
        displayedMonth.year, displayedMonth.monthValue
    ).collectAsState(initial = emptyList())
    val todayEvents by viewModel.todayEvents.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HJBackground)
    ) {
        // Month navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "上月", tint = HJBlue)
            }
            Text(
                text = "${displayedMonth.year}年${displayedMonth.monthValue}月",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = HJLabel
            )
            IconButton(onClick = { displayedMonth = displayedMonth.plusMonths(1) }) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "下月", tint = HJBlue)
            }
        }

        // Weekday headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("一", "二", "三", "四", "五", "六", "日").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = HJSecondaryLabel,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Calendar grid
        val firstDay = displayedMonth.atDay(1)
        val firstDayOfWeek = firstDay.dayOfWeek.value // 1=Mon, 7=Sun
        val daysInMonth = displayedMonth.lengthOfMonth()
        val cells = firstDayOfWeek - 1 + daysInMonth

        // Map dates with events
        val datesWithEvents = monthEvents.map { event ->
            java.time.Instant.ofEpochMilli(event.startTimeMillis)
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        }.toSet()

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.padding(horizontal = 8.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            items(cells + (if (cells % 7 == 0) 0 else 7 - cells % 7)) { index ->
                val dayIndex = index - (firstDayOfWeek - 1)
                if (dayIndex < 0 || dayIndex >= daysInMonth) {
                    Box(modifier = Modifier.size(44.dp))
                } else {
                    val date = displayedMonth.atDay(dayIndex + 1)
                    val isSelected = date == selectedDate
                    val isToday = date == LocalDate.now()
                    val hasEvents = datesWithEvents.contains(date)

                    Column(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> HJBlue
                                    isToday -> HJBlue.copy(alpha = 0.15f)
                                    else -> Color.Transparent
                                }
                            )
                            .clickable {
                                viewModel.selectDate(date)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${date.dayOfMonth}",
                            fontSize = 14.sp,
                            fontWeight = if (isToday || isSelected) FontWeight.Medium else FontWeight.Normal,
                            color = when {
                                isSelected -> Color.White
                                isToday -> HJBlue
                                else -> HJLabel
                            }
                        )
                        if (hasEvents) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White else HJBlue)
                            )
                        }
                    }
                }
            }
        }

        Divider(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            color = Color(0xFFC6C6C8),
            thickness = 0.5.dp
        )

        // Events for selected day
        Text(
            text = "选定日期的日程",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            fontSize = 13.sp,
            color = HJSecondaryLabel,
            fontWeight = FontWeight.Medium
        )

        if (todayEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("当天没有日程", color = HJSecondaryLabel, fontSize = 14.sp)
            }
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                todayEvents.forEach { event ->
                    EventCard(
                        event = event,
                        onClick = { onEventClick(event.id) },
                        onToggleComplete = { viewModel.toggleCompleted(event) }
                    )
                }
            }
        }
    }
}
