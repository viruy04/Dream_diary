package com.example.dreamdiary.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.dreamdiary.R

/** Числа, по которым проверяются условия достижений. */
data class AchievementProgress(
    val dreams: Int,
    val dreamsWithMood: Int,
    val dreamsWithTags: Int,
    val maxTagUses: Int,
    val longestStreak: Int
)

/**
 * Достижения. Условия вычисляются из данных, в БД хранится только факт открытия.
 */
enum class AchievementType(
    val code: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val conditionRes: Int,
    @DrawableRes val iconRes: Int,
    val isEarned: (AchievementProgress) -> Boolean
) {
    FIRST_SPARKLE(
        "first_sparkle",
        R.string.achievement_first_sparkle,
        R.string.achievement_first_sparkle_description,
        R.string.achievement_first_sparkle_condition,
        R.drawable.achievement_first_sparkle,
        { it.dreams >= 1 }
    ),
    MOON_ARCHIVE(
        "moon_archive",
        R.string.achievement_moon_archive,
        R.string.achievement_moon_archive_description,
        R.string.achievement_moon_archive_condition,
        R.drawable.achievement_moon_archive,
        { it.dreams >= 5 }
    ),
    GALAXY(
        "galaxy",
        R.string.achievement_galaxy,
        R.string.achievement_galaxy_description,
        R.string.achievement_galaxy_condition,
        R.drawable.achievement_galaxy,
        { it.dreams >= 10 }
    ),
    CONSTELLATION(
        "constellation",
        R.string.achievement_constellation,
        R.string.achievement_constellation_description,
        R.string.achievement_constellation_condition,
        R.drawable.achievement_constellation,
        { it.maxTagUses >= 3 } // один и тот же тег у 3 снов
    ),
    SHOOTING_STAR(
        "shooting_star",
        R.string.achievement_shooting_star,
        R.string.achievement_shooting_star_description,
        R.string.achievement_shooting_star_condition,
        R.drawable.achievement_shooting_star,
        { it.longestStreak >= 3 }
    ),
    WORLD_EXPLORER(
        "world_explorer",
        R.string.achievement_world_explorer,
        R.string.achievement_world_explorer_description,
        R.string.achievement_world_explorer_condition,
        R.drawable.achievement_world_explorer,
        { it.dreamsWithTags >= 5 }
    ),
    NEBULA(
        "nebula",
        R.string.achievement_nebula,
        R.string.achievement_nebula_description,
        R.string.achievement_nebula_condition,
        R.drawable.achievement_nebula,
        { it.dreamsWithMood >= 10 }
    ),
    UNIVERSE_GUARDIAN(
        "universe_guardian",
        R.string.achievement_universe_guardian,
        R.string.achievement_universe_guardian_description,
        R.string.achievement_universe_guardian_condition,
        R.drawable.achievement_universe_guardian,
        { it.dreams >= 25 }
    )
}
