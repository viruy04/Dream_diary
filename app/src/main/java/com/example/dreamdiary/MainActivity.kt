package com.example.dreamdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.dreamdiary.navigation.NavigationBarExample
import com.example.dreamdiary.ui.theme.DreamDiaryTheme
import com.example.dreamdiary.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var themeModeName by rememberSaveable {
                mutableStateOf(ThemeMode.SYSTEM.name)
            }

            val themeMode = ThemeMode.valueOf(themeModeName)

            DreamDiaryTheme(themeMode = themeMode) {
                val navController = rememberNavController()

                NavigationBarExample(
                    navController = navController,
                    themeMode = themeMode,
                    onThemeChange = { newTheme ->
                        themeModeName = newTheme.name
                    }
                )
            }
        }
    }
}
