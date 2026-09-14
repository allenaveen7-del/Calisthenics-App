package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppSettings(
    val restTimeSeconds: Int = 60,
    val currentWeek: Int = 1,
    val remindersEnabled: Boolean = false,
    val reminderTime: String = "18:00",
    val appTheme: String = "athletic" // "athletic", "dark", "light", "system"
)

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("calisthenics_coach_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<AppSettings> = _settingsFlow.asStateFlow()

    fun loadSettings(): AppSettings {
        return AppSettings(
            restTimeSeconds = prefs.getInt(KEY_REST_TIME, 60),
            currentWeek = prefs.getInt(KEY_CURRENT_WEEK, 1),
            remindersEnabled = prefs.getBoolean(KEY_REMINDERS_ENABLED, false),
            reminderTime = prefs.getString(KEY_REMINDER_TIME, "18:00") ?: "18:00",
            appTheme = prefs.getString(KEY_APP_THEME, "athletic") ?: "athletic"
        )
    }

    fun setRestTime(seconds: Int) {
        prefs.edit().putInt(KEY_REST_TIME, seconds).apply()
        _settingsFlow.value = _settingsFlow.value.copy(restTimeSeconds = seconds)
    }

    fun setCurrentWeek(week: Int) {
        val safeWeek = week.coerceAtLeast(1)
        prefs.edit().putInt(KEY_CURRENT_WEEK, safeWeek).apply()
        _settingsFlow.value = _settingsFlow.value.copy(currentWeek = safeWeek)
    }

    fun setRemindersEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDERS_ENABLED, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(remindersEnabled = enabled)
    }

    fun setReminderTime(time: String) {
        prefs.edit().putString(KEY_REMINDER_TIME, time).apply()
        _settingsFlow.value = _settingsFlow.value.copy(reminderTime = time)
    }

    fun setAppTheme(theme: String) {
        prefs.edit().putString(KEY_APP_THEME, theme).apply()
        _settingsFlow.value = _settingsFlow.value.copy(appTheme = theme)
    }

    fun resetSettings() {
        prefs.edit().clear().apply()
        _settingsFlow.value = AppSettings()
    }

    companion object {
        private const val KEY_REST_TIME = "rest_time_seconds"
        private const val KEY_CURRENT_WEEK = "current_program_week"
        private const val KEY_REMINDERS_ENABLED = "workout_reminders_enabled"
        private const val KEY_REMINDER_TIME = "workout_reminder_time"
        private const val KEY_APP_THEME = "app_theme"
    }
}
