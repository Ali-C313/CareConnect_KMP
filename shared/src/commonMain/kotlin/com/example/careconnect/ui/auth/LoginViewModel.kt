package com.example.careconnect.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.i18n.tr
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Logik für den Login
class LoginViewModel(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    // Nach erfolgreichem Login wechselt der AuthViewModel automatisch den Screen
    fun login() {
        val state = _uiState.value
        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = tr("Bitte E-Mail und Passwort eingeben.")) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            authRepo.login(state.email, state.password)
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = tr("Fehler beim Login: {0}", error.message)) }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
