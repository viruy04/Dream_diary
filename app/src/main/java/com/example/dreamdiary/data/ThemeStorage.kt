package com.example.dreamdiary.data

import android.content.Context
import com.example.dreamdiary.ui.theme.ThemeMode

/**
 * Выбранная тема хранится в SharedPreferences: это одна настройка приложения,
 * для неё отдельная таблица в БД не нужна.
 */
object ThemeStorage {
    private const val PREFS_NAME = "dream_diary_settings"
    private const val KEY_THEME_MODE = "theme_mode"

    fun load(context: Context): ThemeMode {
        val saved = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME_MODE, null)
        return ThemeMode.entries.firstOrNull { it.name == saved } ?: ThemeMode.SYSTEM
    }

    fun save(context: Context, mode: ThemeMode) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME_MODE, mode.name)
            .apply()
    }
}
