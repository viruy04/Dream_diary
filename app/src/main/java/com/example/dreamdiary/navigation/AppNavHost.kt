package com.example.dreamdiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dreamdiary.screens.AchievementsScreen
import com.example.dreamdiary.screens.CalendarScreen
import com.example.dreamdiary.screens.ConstellationScreen
import com.example.dreamdiary.screens.DreamsScreen
import com.example.dreamdiary.screens.HomeScreen
import com.example.dreamdiary.screens.ProfileScreen
import com.example.dreamdiary.screens.StatisticsScreen
import com.example.dreamdiary.ui.theme.ThemeMode

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onAddDreamClick: () -> Unit = {}
) {
    // «Назад» для кнопки в интерфейсе. Системную кнопку и жест обрабатывает сам NavHost
    // (BackHandler здесь не нужен - он бы отключил предиктивную анимацию жеста).
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
        enterTransition = { navEnter() },
        exitTransition = { navExit() },
        popEnterTransition = { navPopEnter() },
        popExitTransition = { navPopExit() }
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
            CalendarScreen(onBack = goBack)
        }

        composable(Destination.Statistics.route) {
            StatisticsScreen(onBack = goBack)
        }

        composable(Destination.Constellation.route) {
            ConstellationScreen(onBack = goBack)
        }

        composable(Destination.Achievements.route) {
            AchievementsScreen(onBack = goBack)
        }
    }
}