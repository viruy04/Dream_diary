package com.example.dreamdiary.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dreamdiary.R
import com.example.dreamdiary.components.AddDreamDialog
import com.example.dreamdiary.ui.theme.ThemeMode

@Composable
fun NavigationBarExample(
    navController: NavHostController,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    var showAddDreamDialog by remember { mutableStateOf(false) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Dreams,
        NavigationItem.Profile
    )

    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Фоновое изображение в зависимости от темы
        Image(
            painter = painterResource(
                id = if (isDarkTheme) {
                    R.drawable.dream_background_dark
                } else {
                    R.drawable.dream_background_light
                }
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    containerColor = Color.Transparent
                ) {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == item.route) {
                                        item.selectedIcon
                                    } else {
                                        item.icon
                                    },
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(item.title)
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                onAddDreamClick = { showAddDreamDialog = true }
            )
        }
        if (showAddDreamDialog) {
            AddDreamDialog(
                onDismiss = { showAddDreamDialog = false }
            )
        }
    }
}
