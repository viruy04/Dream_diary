package com.example.dreamdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.dreamdiary.data.DreamRepository
import com.example.dreamdiary.data.ThemeStorage
import com.example.dreamdiary.navigation.Destination
import com.example.dreamdiary.navigation.NavigationBarExample
import com.example.dreamdiary.ui.theme.DreamDiaryTheme
import com.example.dreamdiary.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Тема читается из SharedPreferences, поэтому переживает перезапуск приложения
            var themeModeName by rememberSaveable {
                mutableStateOf(ThemeStorage.load(applicationContext).name)
            }

            val themeMode = ThemeMode.valueOf(themeModeName)

            DreamDiaryTheme(themeMode = themeMode) {
                val navController = rememberNavController()

                // Профиль создаётся при первом запуске: пока его нет в БД - стартовый экран,
                // потом сразу главная
                val repository = remember { DreamRepository.get(applicationContext) }
                val startDestination = remember {
                    if (repository.hasProfile()) Destination.Home.route else Destination.Welcome.route
                }

                NavigationBarExample(
                    navController = navController,
                    startDestination = startDestination,
                    themeMode = themeMode,
                    onThemeChange = { newTheme ->
                        themeModeName = newTheme.name
                        ThemeStorage.save(applicationContext, newTheme)
                    }
                )
            }
        }
    }
}