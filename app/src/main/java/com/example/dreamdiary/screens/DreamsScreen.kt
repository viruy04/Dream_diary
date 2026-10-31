package com.example.dreamdiary.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DreamsScreen() {
    Text(
        text = "Мои сновидения",
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    )
}
