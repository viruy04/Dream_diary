package com.example.dreamdiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dreamdiary.screens.HomeScreen
import com.example.dreamdiary.screens.DreamsScreen
import com.example.dreamdiary.screens.ProfileScreen

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
        composable(route = Destination.Home.route) {
            HomeScreen(navController)
        }

        composable(route = Destination.Dreams.route) {
            DreamsScreen()
        }

        composable(route = Destination.Profile.route) {
            ProfileScreen(navController)
        }
    }
}
