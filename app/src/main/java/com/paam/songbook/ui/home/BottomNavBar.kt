package com.paam.songbook.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
    val items = listOf(
        NavItem("home", Icons.Default.Home, "Home"),
        NavItem("search", Icons.Default.Search, "Search"),
        NavItem("favorites", Icons.Default.Favorite, "Favorites"),
        NavItem("settings", Icons.Default.Settings, "Settings")
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color.Transparent
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) Color.White else Color.LightGray,
                        modifier = Modifier
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF70E1F5), Color(0xFFFFD194))
                                ),
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(8.dp)
                    )
                },
                label = {
                    Text(item.label, color = Color.White)
                }
            )
        }
    }
}

data class NavItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)
