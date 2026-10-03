package com.example.careconnect.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.domain.repository.SettingsRepository
import com.example.careconnect.location.TrackingManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileInfo(
    val name: String = "Unbekannt",
    val email: String = "—",
    val imageUrl: String = ""
)

// Wird von beiden Rollen genutzt (Dunkelmodus, Profil-Kopf, Abmelden)
class SettingsViewModel(
    private val settingsRepo: SettingsRepository,
    private val authRepo: AuthRepository,
    private val trackingManager: TrackingManager,
    employeeRepo: EmployeeRepository,
    patientRepo: PatientRepository
) : ViewModel() {

    private val uid = authRepo.currentUserId

    val isDarkMode = settingsRepo.isDarkMode
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    val profile = combine(
        if (uid != null) employeeRepo.getEmployee(uid) else flowOf(null),
        if (uid != null) patientRepo.getPatient(uid) else flowOf(null)
    ) { employee, patient ->
        val email = authRepo.currentUserEmail ?: "—"
        when {
            employee != null -> ProfileInfo(employee.name.ifEmpty { "Unbekannt" }, email, employee.profileImageUrl)
            patient != null -> ProfileInfo(patient.name, email, patient.profileImageUrl)
            else -> ProfileInfo(email = email)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ProfileInfo()
    )

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setDarkMode(enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            trackingManager.stop()
            authRepo.logout()
        }
    }
}
