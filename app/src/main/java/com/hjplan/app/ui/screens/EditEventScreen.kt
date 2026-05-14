package com.hjplan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.hjplan.app.ui.theme.*
import com.hjplan.app.viewmodel.EventViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val EVENT_COLORS = listOf(
    "#1A73E8", "#34D399", "#F59E0B",
    "#EF4444", "#A78BFA", "#EC4899", "#1C1C1E"
)

val REMINDER_OPTIONS = listOf(
    0 to "不提醒",
    5 to "提前5分钟",
    10 to "提前10分钟",
    15 to "提前15分钟",
    30 to "提前30分钟",
    60 to "提前1小时",
    1440 to "提前1天"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventScreen(
    eventId: Long?,
    onBack: () -> Unit,
    viewModel: EventViewModel = hiltViewModel()
) {
    var existingEvent by remember { mutableStateOf<Event?>(null) }

    LaunchedEffect(eventId) {
        if (eventId != null && eventId > 0) {
            existingEvent = viewModel.viewModelScope.run { null } // placeholder
        }
    }

    val today = LocalDate.now()
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(today) }
    var startHour by remember { mutableStateOf(9) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(10) }
    var endMinute by remember { mutableStateOf(0) }
    var isAllDay by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(EVENT_COLORS[0]) }
    var reminderMinutes by remember { mutableStateOf(15) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReminderMenu by remember { mutableStateOf(false) }

    val isEditing = eventId != null && eventId > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "编辑日程" else "新建日程",
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("取消", color = HJSecondaryLabel)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (title.isBlank()) return@TextButton
                            val startDt = LocalDateTime.of(selectedDate, LocalTime.of(startHour, startMinute))
                            val endDt = LocalDateTime.of(selectedDate, LocalTime.of(endHour, endMinute))
                            val event = Event(
                                id = eventId ?: 0,
                                title = title.trim(),
                                note = note.trim(),
                                location = location.trim(),
                                startTimeMillis = startDt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                                endTimeMillis = endDt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                                isAllDay = isAllDay,
                                color = selectedColor,
                                reminderMinutes = reminderMinutes
                            )
                            if (isEditing) viewModel.updateEvent(event)
                            else viewModel.insertEvent(event)
                            onBack()
                        }
                    ) {
                        Text(
                            "保存",
                            color = if (title.isNotBlank()) HJBlue else HJSecondaryLabel,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HJBackground
                )
            )
        },
        containerColor = HJBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Title & Note
            FormSection {
                FormTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "日程标题",
                    fontSize = 16
                )
                Divider(color = HJBackground, thickness = 0.5.dp)
                FormTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = "备注",
                    label = "备注"
                )
            }

            // Date & Time
            FormSection {
                FormRow(label = "日期") {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy年M月d日")),
                        fontSize = 14.sp,
                        color = HJLabel
                    )
                }
                Divider(color = HJBackground, thickness = 0.5.dp)
                FormRow(label = "全天") {
                    Switch(
                        checked = isAllDay,
                        onCheckedChange = { isAllDay = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = HJGreen)
                    )
                }
                if (!isAllDay) {
                    Divider(color = HJBackground, thickness = 0.5.dp)
                    FormRow(label = "开始") {
                        Text(
                            text = String.format("%02d:%02d", startHour, startMinute),
                            fontSize = 14.sp,
                            color = HJLabel
                        )
                    }
                    Divider(color = HJBackground, thickness = 0.5.dp)
                    FormRow(label = "结束") {
                        Text(
                            text = String.format("%02d:%02d", endHour, endMinute),
                            fontSize = 14.sp,
                            color = HJLabel
                        )
                    }
                }
            }

            // Location & Reminder
            FormSection {
                FormTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "添加地点",
                    label = "地点"
                )
                Divider(color = HJBackground, thickness = 0.5.dp)
                Box {
                    FormRow(
                        label = "提醒",
                        modifier = Modifier.clickable { showReminderMenu = true }
                    ) {
                        Text(
                            text = REMINDER_OPTIONS.find { it.first == reminderMinutes }?.second ?: "不提醒",
                            fontSize = 14.sp,
                            color = HJLabel
                        )
                    }
                    DropdownMenu(
                        expanded = showReminderMenu,
                        onDismissRequest = { showReminderMenu = false }
                    ) {
                        REMINDER_OPTIONS.forEach { (min, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    reminderMinutes = min
                                    showReminderMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Color picker
            FormSection {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("颜色", fontSize = 14.sp, color = HJSecondaryLabel)
                    Spacer(modifier = Modifier.width(8.dp))
                    EVENT_COLORS.forEach { hex ->
                        val color = runCatching {
                            Color(android.graphics.Color.parseColor(hex))
                        }.getOrDefault(HJBlue)
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor = hex }
                        ) {
                            if (selectedColor == hex) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }

            if (isEditing) {
                TextButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = HJRed)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("删除日程", color = HJRed)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除日程") },
            text = { Text("确定要删除这个日程吗？此操作无法撤销。") },
            confirmButton = {
                TextButton(onClick = {
                    existingEvent?.let { viewModel.deleteEvent(it) }
                    showDeleteDialog = false
                    onBack()
                }) {
                    Text("删除", color = HJRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun FormSection(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        content = content
    )
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String? = null,
    fontSize: Int = 14
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (label != null) {
            Text(label, fontSize = 14.sp, color = HJSecondaryLabel, modifier = Modifier.width(36.dp))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            fontSize = fontSize
        )
    }
}

@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    fontSize: Int = 14
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = fontSize.sp,
            color = HJLabel,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default
        ),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(placeholder, fontSize = fontSize.sp, color = Color(0xFFC7C7CC))
            }
            innerTextField()
        },
        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
    )
}

@Composable
fun FormRow(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = HJSecondaryLabel)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            content()
        }
    }
}
