package com.hjplan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddEvent: (Long?) -> Unit,
    viewModel: EventViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val todayEvents by viewModel.todayEvents.collectAsState()

    val completedCount = todayEvents.count { it.isCompleted }
    val pendingCount = todayEvents.count { !it.isCompleted }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HJBackground)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val isToday = selectedDate == LocalDate.now()
                    Text(
                        text = if (isToday) "今天" else {
                            selectedDate.format(DateTimeFormatter.ofPattern("M月d日"))
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium,
                        color = HJLabel
                    )
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("M月d日 ")) +
                                selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINESE),
                        fontSize = 14.sp,
                        color = HJSecondaryLabel
                    )
                }
                // Avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC7C7CC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("H", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
        }

        // Week row
        WeekBar(
            selectedDate = selectedDate,
            onDateSelected = { viewModel.selectDate(it) }
        )

        // Stats row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatChip(label = "待完成", count = pendingCount, modifier = Modifier.weight(1f))
            StatChip(label = "已完成", count = completedCount, modifier = Modifier.weight(1f))
            StatChip(label = "合计", count = todayEvents.size, modifier = Modifier.weight(1f))
        }

        // Event list
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (todayEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("今天没有日程", color = HJSecondaryLabel, fontSize = 16.sp)
                            Text("点击右下角 + 添加", color = HJSecondaryLabel, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(todayEvents, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        onClick = { onAddEvent(event.id) },
                        onToggleComplete = { viewModel.toggleCompleted(event) }
                    )
                }
            }
        }

        // FAB area placeholder (FAB is in Scaffold)
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun WeekBar(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    val weekDays = listOf("一", "二", "三", "四", "五", "六", "日")
    // Start from Monday of selected week
    val startOfWeek = selectedDate.minusDays((selectedDate.dayOfWeek.value - 1).toLong())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (i in 0..6) {
            val date = startOfWeek.plusDays(i.toLong())
            val isSelected = date == selectedDate
            val isToday = date == today

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) HJBlue else Color.Transparent)
                    .clickable { onDateSelected(date) }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = weekDays[i],
                    fontSize = 10.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else HJSecondaryLabel
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${date.dayOfMonth}",
                    fontSize = 16.sp,
                    fontWeight = if (isToday || isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isSelected) Color.White else HJLabel
                )
                Spacer(modifier = Modifier.height(2.dp))
                // Dot indicator
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> Color.White
                                isToday -> HJBlue
                                else -> Color.Transparent
                            }
                        )
                )
            }
        }
    }
}

@Composable
fun StatChip(label: String, count: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$count", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = HJLabel)
        Text(text = label, fontSize = 11.sp, color = HJSecondaryLabel)
    }
}

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit
) {
    val eventColor = runCatching { Color(android.graphics.Color.parseColor(event.color)) }
        .getOrDefault(HJBlue)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Color bar
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (event.isCompleted) Color(0xFFC7C7CC) else eventColor)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (event.isCompleted) HJSecondaryLabel else HJLabel,
                textDecoration = if (event.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (event.note.isNotBlank()) {
                Text(
                    text = event.note,
                    fontSize = 13.sp,
                    color = HJSecondaryLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Time
        val timeText = if (event.isAllDay) "全天" else {
            val fmt = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
            fmt.format(java.util.Date(event.startTimeMillis))
        }
        Text(text = timeText, fontSize = 12.sp, color = HJSecondaryLabel)

        // Complete toggle
        IconButton(
            onClick = onToggleComplete,
            modifier = Modifier.size(28.dp)
        ) {
            if (event.isCompleted) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "已完成",
                    tint = HJBlue,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Icon(
                    Icons.Outlined.Circle,
                    contentDescription = "未完成",
                    tint = Color(0xFFC7C7CC),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
