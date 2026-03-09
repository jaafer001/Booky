package com.example.booky.data.pref

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

enum class LayoutType {
    ROW, GRID
}

data class AppSettings(
    val fontSize: Float = 16f,
    val fontFamily: String = "Default",
    val themeColor: Int = 0xFF6200EE.toInt(),
    val isDarkMode: Boolean = false,
    val layoutType: LayoutType = LayoutType.ROW
)

class UserPreferences(private val context: Context) {
    private object Keys {
        val FONT_SIZE = floatPreferencesKey("font_size")
        val FONT_FAMILY = stringPreferencesKey("font_family")
        val THEME_COLOR = intPreferencesKey("theme_color")
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val LAYOUT_TYPE = stringPreferencesKey("layout_type")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            fontSize = preferences[Keys.FONT_SIZE] ?: 16f,
            fontFamily = preferences[Keys.FONT_FAMILY] ?: "Default",
            themeColor = preferences[Keys.THEME_COLOR] ?: 0xFF6200EE.toInt(),
            isDarkMode = preferences[Keys.IS_DARK_MODE] ?: false,
            layoutType = LayoutType.valueOf(preferences[Keys.LAYOUT_TYPE] ?: LayoutType.ROW.name)
        )
    }

    suspend fun updateFontSize(size: Float) {
        context.dataStore.edit { it[Keys.FONT_SIZE] = size }
    }

    suspend fun updateFontFamily(family: String) {
        context.dataStore.edit { it[Keys.FONT_FAMILY] = family }
    }

    suspend fun updateThemeColor(color: Int) {
        context.dataStore.edit { it[Keys.THEME_COLOR] = color }
    }

    suspend fun updateDarkMode(isDark: Boolean) {
        context.dataStore.edit { it[Keys.IS_DARK_MODE] = isDark }
    }

    suspend fun updateLayoutType(layoutType: LayoutType) {
        context.dataStore.edit { it[Keys.LAYOUT_TYPE] = layoutType.name }
    }
}
