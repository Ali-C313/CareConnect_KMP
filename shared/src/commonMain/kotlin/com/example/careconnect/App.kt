package com.example.careconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.careconnect.domain.model.UserRole
import com.example.careconnect.ui.auth.AuthNavigation
import com.example.careconnect.ui.auth.AuthState
import com.example.careconnect.ui.auth.AuthViewModel
import com.example.careconnect.ui.employee.EmployeeApp
import com.example.careconnect.ui.patient.PatientApp
import com.example.careconnect.ui.settings.SettingsViewModel
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    CareConnectTheme(darkTheme = isDarkMode) {
        AuthWrapper()
    }
}

// Entspricht RootView aus iOS: Login, Mitarbeiter-Tabs oder Patienten-Tabs
@Composable
fun AuthWrapper(
    authViewModel: AuthViewModel = koinViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val state = authState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state is AuthState.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            state is AuthState.LoggedIn && state.role == UserRole.EMPLOYEE -> {
                EmployeeApp()
            }

            state is AuthState.LoggedIn && state.role == UserRole.PATIENT -> {
                PatientApp()
            }

            else -> {
                AuthNavigation(
                    isLoggedInWithoutProfile = state is AuthState.LoggedIn,
                    onLogout = authViewModel::logout
                )
            }
        }
    }
}
