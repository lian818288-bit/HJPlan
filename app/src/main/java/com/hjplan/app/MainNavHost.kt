package com.hjplan.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.hjplan.app.ui.screens.*
import com.hjplan.app.ui.theme.HJBackground
import com.hjplan.app.ui.theme.HJBlue
import com.hjplan.app.ui.theme.HJLabel
import com.hjplan.app.ui.theme.HJSecondaryLabel

data class TabItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val TABS = listOf(
    TabItem("home", "日程", Icons.Filled.CalendarToday, Icons.Outlined.CalendarToday),
    TabItem("calendar", "月历", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    TabItem("tasks", "清单", Icons.Filled.List, Icons.Outlined.List),
    TabItem("profile", "我的", Icons.Filled.Person, Icons.Outlined.Person),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabRoutes = TABS.map { it.route }
    val showBottomBar = tabRoutes.any { currentRoute?.startsWith(it) == true }

    Scaffold(
        containerColor = HJBackground,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFF9F9F9),
                    tonalElevation = 0.dp
                ) {
                    TABS.forEach { tab ->
                        val selected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    if (selected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HJBlue,
                                selectedTextColor = HJBlue,
                                unselectedIconColor = HJSecondaryLabel,
                                unselectedTextColor = HJSecondaryLabel,
                                indicatorColor = HJBackground
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                FloatingActionButton(
                    onClick = { navController.navigate("edit/-1") },
                    containerColor = HJBlue,
                    contentColor = androidx.compose.ui.graphics.Color.White
                ) {
                    Icon(
                        androidx.compose.material.icons.Icons.Filled.Add,
                        contentDescription = "新建日程"
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(onAddEvent = { id ->
                    navController.navigate("edit/${id ?: -1}")
                })
            }
            composable("calendar") {
                CalendarScreen(onEventClick = { id ->
                    navController.navigate("edit/$id")
                })
            }
            composable("tasks") {
                TaskListScreen(onEventClick = { id ->
                    navController.navigate("edit/$id")
                })
            }
            composable("profile") {
                ProfileScreen()
            }
            composable(
                "edit/{eventId}",
                arguments = listOf(navArgument("eventId") { type = NavType.LongType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getLong("eventId")
                    ?.takeIf { it > 0 }
                EditEventScreen(
                    eventId = eventId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
