package com.example.dreamdiary.data

import androidx.annotation.StringRes
import com.example.dreamdiary.R

/**
 * Настроение сна. В БД хранится [code], а не id ресурса:
 * числовые id из R меняются между сборками.
 */
enum class Mood(val code: String, @StringRes val labelRes: Int, val emoji: String) {
    CALM("calm", R.string.mood_calm, "🌊"),
    SURPRISE("surprise", R.string.mood_surprise, "😮"),
    JOY("joy", R.string.mood_joy, "😊"),
    SCARY("scary", R.string.mood_scary, "😰"),
    SADNESS("sadness", R.string.mood_sadness, "😔"),
    ANGRY("angry", R.string.mood_angry, "😠"),
    LOVE("love", R.string.mood_love, "💜"),
    INSPIRATION("inspiration", R.string.mood_inspiration, "✨");

    companion object {
        fun fromCode(code: String?): Mood? = entries.firstOrNull { it.code == code }
    }
}
