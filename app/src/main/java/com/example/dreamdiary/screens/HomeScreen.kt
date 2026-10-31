package com.example.dreamdiary.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Text(
        text = "Главная",
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    )
}
