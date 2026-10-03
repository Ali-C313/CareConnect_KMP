package com.example.careconnect.ui.patient.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.careconnect.ui.settings.SettingsContent
import com.example.careconnect.ui.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PatientSettingsScreen(
    onEditProfileClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    SettingsContent(
        profile = profile,
        isDarkMode = isDarkMode,
        onDarkModeChange = viewModel::setDarkMode,
        onEditProfileClick = onEditProfileClick,
        onLogout = viewModel::logout
    )
}
