package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.AppThemeSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "our_dream_preferences")

class UserPreferences(private val context: Context) {

  companion object {
    val KEY_THEME = stringPreferencesKey("app_theme")
    val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val KEY_REMIND_7_DAYS = booleanPreferencesKey("remind_7_days")
    val KEY_REMIND_3_DAYS = booleanPreferencesKey("remind_3_days")
    val KEY_REMIND_1_DAY = booleanPreferencesKey("remind_1_day")
    val KEY_REMIND_SAME_DAY = booleanPreferencesKey("remind_same_day")
    val KEY_ACTIVE_USER_ID = stringPreferencesKey("active_user_id")
    val KEY_SUPABASE_URL = stringPreferencesKey("supabase_url")
    val KEY_SUPABASE_ANON_KEY = stringPreferencesKey("supabase_anon_key")
  }

  val themeFlow: Flow<AppThemeSetting> = context.dataStore.data.map { prefs ->
    val themeName = prefs[KEY_THEME] ?: AppThemeSetting.LIGHT_ROMANTIC.name
    try {
      AppThemeSetting.valueOf(themeName)
    } catch (_: Exception) {
      AppThemeSetting.LIGHT_ROMANTIC
    }
  }

  val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[KEY_NOTIFICATIONS_ENABLED] ?: true
  }

  val remind7DaysFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[KEY_REMIND_7_DAYS] ?: true
  }

  val remind3DaysFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[KEY_REMIND_3_DAYS] ?: true
  }

  val remind1DayFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[KEY_REMIND_1_DAY] ?: true
  }

  val remindSameDayFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[KEY_REMIND_SAME_DAY] ?: true
  }

  val activeUserIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
    prefs[KEY_ACTIVE_USER_ID]
  }

  val supabaseUrlFlow: Flow<String> = context.dataStore.data.map { prefs ->
    prefs[KEY_SUPABASE_URL] ?: ""
  }

  val supabaseKeyFlow: Flow<String> = context.dataStore.data.map { prefs ->
    prefs[KEY_SUPABASE_ANON_KEY] ?: ""
  }

  suspend fun setTheme(theme: AppThemeSetting) {
    context.dataStore.edit { prefs ->
      prefs[KEY_THEME] = theme.name
    }
  }

  suspend fun setNotificationsEnabled(enabled: Boolean) {
    context.dataStore.edit { prefs ->
      prefs[KEY_NOTIFICATIONS_ENABLED] = enabled
    }
  }

  suspend fun setReminderIntervals(sevenDays: Boolean, threeDays: Boolean, oneDay: Boolean, sameDay: Boolean) {
    context.dataStore.edit { prefs ->
      prefs[KEY_REMIND_7_DAYS] = sevenDays
      prefs[KEY_REMIND_3_DAYS] = threeDays
      prefs[KEY_REMIND_1_DAY] = oneDay
      prefs[KEY_REMIND_SAME_DAY] = sameDay
    }
  }

  suspend fun setActiveUserId(userId: String?) {
    context.dataStore.edit { prefs ->
      if (userId != null) {
        prefs[KEY_ACTIVE_USER_ID] = userId
      } else {
        prefs.remove(KEY_ACTIVE_USER_ID)
      }
    }
  }

  suspend fun setSupabaseCredentials(url: String, anonKey: String) {
    context.dataStore.edit { prefs ->
      prefs[KEY_SUPABASE_URL] = url
      prefs[KEY_SUPABASE_ANON_KEY] = anonKey
    }
  }
}
