package com.example.dreamdiary.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
        modifier = modifier,
        // «Fade through»: старый экран быстро гаснет, затем проявляется новый.
        // Экраны прозрачные (фон лежит под Scaffold), поэтому они не должны
        // быть видны одновременно - иначе тексты накладываются и мерцают.
        enterTransition = { fadeIn(tween(durationMillis = 210, delayMillis = 90)) },
        exitTransition = { fadeOut(tween(durationMillis = 90)) },
        popEnterTransition = { fadeIn(tween(durationMillis = 210, delayMillis = 90)) },
        popExitTransition = { fadeOut(tween(durationMillis = 90)) }
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