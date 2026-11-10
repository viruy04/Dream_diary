package com.example.dreamdiary.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dreamdiary.R
import com.example.dreamdiary.components.AddDreamDialog
import com.example.dreamdiary.ui.theme.ThemeMode

@Composable
fun NavigationBarExample(
    navController: NavHostController,
    startDestination: String,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    var showAddDreamDialog by remember { mutableStateOf(false) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    // В первый кадр currentRoute ещё null - берём стартовый маршрут, чтобы фон и панель не мигали
    val activeRoute = currentRoute ?: startDestination

    // На экранах из NavRoutes.bottomBarHidden нижняя панель скрыта (там кнопка «Назад»)
    val showBottomBar = activeRoute !in NavRoutes.bottomBarHidden

    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Dreams,
        NavigationItem.Profile
    )

    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // Фон: на стартовом экране своя картинка, дальше - по теме
    val backgroundRes = when {
        activeRoute == Destination.Welcome.route -> R.drawable.welcome_bg
        isDarkTheme -> R.drawable.dream_background_dark
        else -> R.drawable.dream_background_light
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Плавная смена фона без «щелчка»
        Crossfade(
            targetState = backgroundRes,
            animationSpec = tween(300),
            label = "background"
        ) { res ->
            Image(
                painter = painterResource(id = res),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = bottomBarEnter(),
                    exit = bottomBarExit()
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent
                    ) {
                        items.forEach { item ->
                            NavigationBarItem(
                                selected = currentRoute == item.route,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(Destination.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (currentRoute == item.route) {
                                            item.selectedIcon
                                        } else {
                                            item.icon
                                        },
                                        contentDescription = item.title
                                    )
                                },
                                label = {
                                    Text(item.title)
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding),
                themeMode = themeMode,
                onThemeChange = onThemeChange,
                onAddDreamClick = { showAddDreamDialog = true }
            )
        }
        if (showAddDreamDialog) {
            AddDreamDialog(
                onDismiss = { showAddDreamDialog = false }
            )
        }
    }
}