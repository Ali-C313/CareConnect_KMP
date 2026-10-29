package com.example.careconnect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.careconnect.i18n.I18n
import com.example.careconnect.ui.settings.SettingsViewModel
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.compose.viewmodel.koinViewModel

// Startpunkt der gemeinsamen UI für Android und iOS
@Composable
fun App() {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    // Gleiche Quelle wie tr(), wird vom SettingsViewModel gesetzt
    val language = I18n.language

    // Arabisch wird von rechts nach links angezeigt
    CompositionLocalProvider(
        LocalLayoutDirection provides if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        CareConnectTheme(darkTheme = isDarkMode) {
            AuthWrapper()
        }
    }
}
