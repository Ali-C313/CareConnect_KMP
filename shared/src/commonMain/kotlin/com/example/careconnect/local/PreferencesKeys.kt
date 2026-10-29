package com.example.careconnect.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

// Schlüssel für den DataStore
object PreferencesKeys {
    val DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val SHARE_LOCATION = booleanPreferencesKey("share_location")
    val SHOW_LIVE_LOCATION = booleanPreferencesKey("show_live_location")
    val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
    val LANGUAGE = stringPreferencesKey("app_language")
}
