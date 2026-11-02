package com.example.dreamdiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dreamdiary.screens.HomeScreen
import com.example.dreamdiary.screens.DreamsScreen
import com.example.dreamdiary.screens.ProfileScreen
import androidx.compose.material3.Text

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
        modifier = modifier
    ) {
        composable(Destination.Home.route) {
            HomeScreen(navController)
        }

        composable(Destination.Dreams.route) {
            DreamsScreen()
        }

        composable(Destination.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Destination.Calendar.route) {
            Text("Календарь сновидений")
        }

        composable(Destination.Statistics.route) {
            Text("Статистика настроений")
        }

        composable(Destination.Constellation.route) {
            Text("Созвездие снов")
        }

        composable(Destination.Achievements.route) {
            Text("Мои достижения")
        }
    }
}