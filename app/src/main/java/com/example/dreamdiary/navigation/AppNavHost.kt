package com.example.dreamdiary.navigation

import androidx.activity.compose.BackHandler
import androidx.lifecycle.Lifecycle
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
    // Общий «назад» для кнопки в приложении и системной кнопки/жеста.
    // Проверка состояния защищает от двойного нажатия во время анимации.
    val goBack: () -> Unit = {
        val entry = navController.currentBackStackEntry
        if (entry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.popBackStack()
        }
    }

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
            BackHandler(onBack = goBack)
            CalendarScreen(onBack = goBack)
        }

        composable(Destination.Statistics.route) {
            BackHandler(onBack = goBack)
            StatisticsScreen(onBack = goBack)
        }

        composable(Destination.Constellation.route) {
            BackHandler(onBack = goBack)
            ConstellationScreen(onBack = goBack)
        }

        composable(Destination.Achievements.route) {
            BackHandler(onBack = goBack)
            AchievementsScreen(onBack = goBack)
        }
    }
}