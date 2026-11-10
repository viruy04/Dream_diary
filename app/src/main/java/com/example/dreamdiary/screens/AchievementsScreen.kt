
package com.example.dreamdiary.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dreamdiary.R
import com.example.dreamdiary.components.ScreenHeader

private data class Achievement(
    val titleRes: Int,
    val descriptionRes: Int,
    val conditionRes: Int,
    val iconRes: Int,
    val isUnlocked: Boolean,
    val dateRes: Int? = null
)

private val achievements = listOf(
    Achievement(
        R.string.achievement_first_sparkle,
        R.string.achievement_first_sparkle_description,
        R.string.achievement_first_sparkle_condition,
        R.drawable.achievement_first_sparkle,
        true,
        R.string.achievement_date_demo_1
    ),
    Achievement(
        R.string.achievement_moon_archive,
        R.string.achievement_moon_archive_description,
        R.string.achievement_moon_archive_condition,
        R.drawable.achievement_moon_archive,
        true,
        R.string.achievement_date_demo_2
    ),
    Achievement(
        R.string.achievement_galaxy,
        R.string.achievement_galaxy_description,
        R.string.achievement_galaxy_condition,
        R.drawable.achievement_galaxy,
        true,
        R.string.achievement_date_demo_3
    ),
    Achievement(
        R.string.achievement_constellation,
        R.string.achievement_constellation_description,
        R.string.achievement_constellation_condition,
        R.drawable.achievement_constellation,
        false
    ),
    Achievement(
        R.string.achievement_shooting_star,
        R.string.achievement_shooting_star_description,
        R.string.achievement_shooting_star_condition,
        R.drawable.achievement_shooting_star,
        false
    ),
    Achievement(
        R.string.achievement_world_explorer,
        R.string.achievement_world_explorer_description,
        R.string.achievement_world_explorer_condition,
        R.drawable.achievement_world_explorer,
        false
    ),
    Achievement(
        R.string.achievement_nebula,
        R.string.achievement_nebula_description,
        R.string.achievement_nebula_condition,
        R.drawable.achievement_nebula,
        false
    ),
    Achievement(
        R.string.achievement_universe_guardian,
        R.string.achievement_universe_guardian_description,
        R.string.achievement_universe_guardian_condition,
        R.drawable.achievement_universe_guardian,
        false
    )
)

@Composable
fun AchievementsScreen(onBack: () -> Unit = {}) {
    var selectedAchievement by remember {
        mutableStateOf<Achievement?>(null)
    }

    val unlockedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            //
            // background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 8.dp)
            ) {
                ScreenHeader(
                    title = stringResource(R.string.achievements_title),
                    onBack = onBack
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(
                        R.string.achievements_progress,
                        unlockedCount,
                        totalCount
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { unlockedCount.toFloat() / totalCount },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        items(achievements) { achievement ->
            AchievementCard(
                achievement = achievement,
                onClick = { selectedAchievement = achievement }
            )
        }
    }

    selectedAchievement?.let { achievement ->
        AlertDialog(
            onDismissRequest = { selectedAchievement = null },
            title = {
                Text(
                    text = if (achievement.isUnlocked) {
                        stringResource(achievement.titleRes)
                    } else {
                        stringResource(R.string.achievement_locked_title)
                    }
                )
            },
            text = {
                Column {
                    if (achievement.isUnlocked) {
                        Text(stringResource(achievement.descriptionRes))

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(
                                R.string.achievement_condition,
                                stringResource(achievement.conditionRes)
                            )
                        )

                        achievement.dateRes?.let { dateRes ->
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(
                                    R.string.achievement_received_date,
                                    stringResource(dateRes)
                                )
                            )
                        }
                    } else {
                        Text(
                            stringResource(
                                R.string.achievement_locked_description
                            )
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { selectedAchievement = null }
                ) {
                    Text(stringResource(R.string.button_close))
                }
            }
        )
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    onClick: () -> Unit
) {
    val unlocked = achievement.isUnlocked

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        if (unlocked) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        if (unlocked) {
                            achievement.iconRes
                        } else {
                            R.drawable.achievement_locked
                        }
                    ),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(22.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (unlocked) {
                    stringResource(achievement.titleRes)
                } else {
                    stringResource(R.string.achievement_locked_title)
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(
                    if (unlocked) {
                        R.string.achievement_unlocked_status
                    } else {
                        R.string.achievement_locked_status
                    }
                ),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.65f
                )
            )
        }
    }
}
