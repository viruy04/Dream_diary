package com.example.dreamdiary.data

data class Profile(
    val name: String,
    val birthDate: String // yyyy-MM-dd
) {
    val zodiac: ZodiacSign get() = ZodiacSign.fromIso(birthDate)
}

data class Dream(
    val id: Long,
    val title: String,
    val description: String,
    val dreamDate: String, // yyyy-MM-dd
    val createdAt: Long,   // epoch millis, нужно для времени записи
    val mood: Mood?,
    val tags: List<String>
) {
    /** Поиск по названию, описанию и тегам. Регистр не важен (в том числе для кириллицы). */
    fun matches(query: String): Boolean {
        val q = query.trim().removePrefix("#")
        if (q.isEmpty()) return true
        return title.contains(q, ignoreCase = true) ||
                description.contains(q, ignoreCase = true) ||
                tags.any { it.contains(q, ignoreCase = true) }
    }
}

data class MoodCount(val mood: Mood, val count: Int)

data class TagCount(val tag: String, val count: Int)

data class AchievementState(
    val type: AchievementType,
    val unlockedAt: String? // yyyy-MM-dd, null - ещё не открыто
)

data class DreamStatistics(
    val total: Int,
    val lastWeek: Int,
    val byMood: List<MoodCount>,
    val topTags: List<TagCount>,
    val currentStreak: Int,
    val longestStreak: Int
) {
    val topMood: Mood? get() = byMood.firstOrNull()?.mood
}
