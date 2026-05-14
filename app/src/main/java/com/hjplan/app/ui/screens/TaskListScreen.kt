package com.hjplan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hjplan.app.ui.theme.HJBackground
import com.hjplan.app.ui.theme.HJBlue
import com.hjplan.app.ui.theme.HJLabel
import com.hjplan.app.ui.theme.HJSecondaryLabel
import com.hjplan.app.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onEventClick: (Long) -> Unit,
    viewModel: EventViewModel = hiltViewModel()
) {
    val allEvents by viewModel.allEvents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val displayEvents = if (searchQuery.isBlank()) allEvents else searchResults

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HJBackground)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("搜索日程", color = HJSecondaryLabel) },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = HJSecondaryLabel)
            },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = HJBlue,
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true
        )

        Text(
            text = "全部日程",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            fontSize = 13.sp,
            color = HJSecondaryLabel,
            fontWeight = FontWeight.Medium
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (displayEvents.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            if (searchQuery.isBlank()) "暂无日程" else "没有找到相关日程",
                            color = HJSecondaryLabel,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                items(displayEvents, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        onClick = { onEventClick(event.id) },
                        onToggleComplete = { viewModel.toggleCompleted(event) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
