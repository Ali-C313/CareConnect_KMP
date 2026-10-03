package com.example.careconnect.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkMode: Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
}
