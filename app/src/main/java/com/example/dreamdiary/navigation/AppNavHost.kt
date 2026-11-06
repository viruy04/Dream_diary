package com.example.dreamdiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dreamdiary.screens.HomeScreen
import com.example.dreamdiary.screens.DreamsScreen
import com.example.dreamdiary.screens.ProfileScreen
import com.example.dreamdiary.screens.CalendarScreen
import com.example.dreamdiary.screens.StatisticsScreen
import com.example.dreamdiary.screens.ConstellationScreen
import androidx.compose.material3.Text
import com.example.dreamdiary.screens.AchievementsScreen
import com.example.dreamdiary.ui.theme.ThemeMode

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onAddDreamClick: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
        modifier = modifier
    ) {
        composable(Destination.Home.route) {
            HomeScreen(navController = navController, onAddDreamClick = onAddDreamClick)
        }

        composable(Destination.Dreams.route) {
            DreamsScreen(onAddDreamClick = onAddDreamClick)
        }

        composable(Destination.Profile.route) {
            ProfileScreen(
                navController = navController,
                themeMode = themeMode,
                onThemeChange = onThemeChange
            )
        }

        composable(Destination.Calendar.route) {
            CalendarScreen()
        }

        composable(Destination.Statistics.route) {
            StatisticsScreen()
        }

        composable(Destination.Constellation.route) {
            ConstellationScreen()
        }
        composable(Destination.Achievements.route) {
            AchievementsScreen()
        }
    }
}