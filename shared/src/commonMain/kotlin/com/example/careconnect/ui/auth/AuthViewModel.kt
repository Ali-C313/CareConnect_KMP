package com.example.careconnect.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.UserRole
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.location.TrackingManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading : AuthState()
    object LoggedOut : AuthState()

    // role == null -> eingeloggt, aber (noch) kein Profil in employees/patients
    data class LoggedIn(val uid: String, val role: UserRole?) : AuthState()
}

class AuthViewModel(
    private val authRepo: AuthRepository,
    private val trackingManager: TrackingManager
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val authState = authRepo.authState
        .flatMapLatest { uid ->
            if (uid == null) flowOf(AuthState.LoggedOut)
            else authRepo.observeRole(uid).map { role -> AuthState.LoggedIn(uid, role) }
        }
        .distinctUntilChanged()
        .onEach { state ->
            // Mitarbeiter teilen ihren Standort, sobald sie eingeloggt sind (wie in iOS)
            if (state is AuthState.LoggedIn && state.role == UserRole.EMPLOYEE) {
                trackingManager.start(state.uid)
            } else {
                trackingManager.stop()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AuthState.Loading
        )

    fun logout() {
        viewModelScope.launch {
            trackingManager.stop()
            authRepo.logout()
        }
    }
}
