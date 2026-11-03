package com.example.dreamdiary.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val route: String
) {
    data object Home : NavigationItem(
        title = "Главная",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        route = Destination.Home.route
    )

    data object Dreams : NavigationItem(
        title = "Сновидения",
        icon = Icons.Outlined.AutoAwesome,
        selectedIcon = Icons.Filled.AutoAwesome,
        route = Destination.Dreams.route
    )

    data object Profile : NavigationItem(
        title = "Профиль",
        icon = Icons.Outlined.PersonOutline,
        selectedIcon = Icons.Filled.Person,
        route = Destination.Profile.route
    )
}