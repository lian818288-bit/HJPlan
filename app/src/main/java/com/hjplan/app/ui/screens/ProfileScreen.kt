package com.hjplan.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hjplan.app.ui.theme.*
import com.hjplan.app.viewmodel.EventViewModel

@Composable
fun ProfileScreen(
    viewModel: EventViewModel = hiltViewModel()
) {
    val allEvents by viewModel.allEvents.collectAsState()
    val completedCount = allEvents.count { it.isCompleted }
    val totalCount = allEvents.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HJBackground)
    ) {
        // Header avatar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(HJBlue),
                contentAlignment = Alignment.Center
            ) {
                Text("H", fontSize = 32.sp, fontWeight = FontWeight.Medium, color = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("HJ", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = HJLabel)
            Text("个人日程管理", fontSize = 13.sp, color = HJSecondaryLabel)
        }

        // Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = "$totalCount", label = "全部日程")
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(0.5.dp),
                color = Color(0xFFC6C6C8)
            )
            StatItem(value = "$completedCount", label = "已完成")
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(0.5.dp),
                color = Color(0xFFC6C6C8)
            )
            StatItem(
                value = "${if (totalCount > 0) (completedCount * 100 / totalCount) else 0}%",
                label = "完成率"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Settings
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            SettingsRow(
                icon = Icons.Filled.CalendarToday,
                iconColor = HJBlue,
                title = "默认提醒时间",
                subtitle = "提前15分钟"
            )
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFFF2F2F7),
                thickness = 0.5.dp
            )
            SettingsRow(
                icon = Icons.Filled.CheckCircleOutline,
                iconColor = HJGreen,
                title = "显示已完成",
                subtitle = "显示"
            )
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFFF2F2F7),
                thickness = 0.5.dp
            )
            SettingsRow(
                icon = Icons.Filled.Info,
                iconColor = Color(0xFF8E8E93),
                title = "关于 HJ Plan",
                subtitle = "v1.0.0"
            )
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = HJLabel)
        Text(label, fontSize = 12.sp, color = HJSecondaryLabel)
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Text(title, fontSize = 15.sp, color = HJLabel, modifier = Modifier.weight(1f))
        Text(subtitle, fontSize = 13.sp, color = HJSecondaryLabel)
    }
}
