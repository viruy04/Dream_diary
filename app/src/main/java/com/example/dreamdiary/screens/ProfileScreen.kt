package com.example.dreamdiary.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.dreamdiary.navigation.Destination
import com.example.dreamdiary.ui.theme.ThemeMode

@Composable
fun ProfileScreen(
    navController: NavHostController,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    var showThemeDialog by remember {
        mutableStateOf(false)
    }

    BackHandler {
        navController.navigate(Destination.Dreams.route) {
            popUpTo(Destination.Home.route) {
                inclusive = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            //.background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Профиль",
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "А",
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = "Имя пользователя",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Возраст: не указан",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.7f
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Моя активность",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ProfileStatistic("0", "Сновидений")
                ProfileStatistic("0", "Достижений")
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Настройки",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        ProfileOption(
            title = "Тема оформления",
            description = when (themeMode) {
                ThemeMode.SYSTEM -> "Как на устройстве"
                ThemeMode.LIGHT -> "Светлая"
                ThemeMode.DARK -> "Тёмная"
            },
            onClick = {
                showThemeDialog = true
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProfileOption(
            title = "О приложении",
            description = "Дневник сновидений",
            onClick = {}
        )
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = {
                showThemeDialog = false
            },
            title = {
                Text("Тема оформления")
            },
            text = {
                Column {
                    ThemeModeOption(
                        title = "Как на устройстве",
                        selected = themeMode == ThemeMode.SYSTEM,
                        onClick = {
                            onThemeChange(ThemeMode.SYSTEM)
                            showThemeDialog = false
                        }
                    )

                    ThemeModeOption(
                        title = "Светлая",
                        selected = themeMode == ThemeMode.LIGHT,
                        onClick = {
                            onThemeChange(ThemeMode.LIGHT)
                            showThemeDialog = false
                        }
                    )

                    ThemeModeOption(
                        title = "Тёмная",
                        selected = themeMode == ThemeMode.DARK,
                        onClick = {
                            onThemeChange(ThemeMode.DARK)
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showThemeDialog = false
                    }
                ) {
                    Text("Закрыть")
                }
            }
        )
    }
}

@Composable
private fun ThemeModeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(
            text = title,
            modifier = Modifier.padding(start = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ProfileStatistic(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ProfileOption(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.7f
                )
            )
        }
    }
}
