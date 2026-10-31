
package com.example.dreamdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.dreamdiary.navigation.NavigationBarExample
import com.example.dreamdiary.ui.theme.DreamDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DreamDiaryTheme {
                val navController = rememberNavController()
                NavigationBarExample(navController)
            }
        }
    }
}
