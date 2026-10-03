package com.example.careconnect.ui.employee.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditEmployeeUiState(
    val firstName: String = "",
    val lastName: String = "",
    val role: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isSaving: Boolean = false,
    val isChangingPassword: Boolean = false,
    val message: String? = null
)

class EditEmployeeProfileViewModel(
    private val employeeRepo: EmployeeRepository,
    private val storageRepo: StorageRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val uid = authRepo.currentUserId

    private val _uiState = MutableStateFlow(EditEmployeeUiState())
    val uiState = _uiState.asStateFlow()

    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes = _imageBytes.asStateFlow()

    init {
        // Formular einmal mit den aktuellen Daten füllen
        val id = uid
        if (id != null) {
            viewModelScope.launch {
                val employee = employeeRepo.getEmployee(id).first() ?: return@launch
                _uiState.update {
                    it.copy(
                        firstName = employee.firstName,
                        lastName = employee.lastName,
                        role = employee.role,
                        email = employee.email,
                        profileImageUrl = employee.profileImageUrl
                    )
                }
            }
        }
    }

    fun onFirstNameChange(value: String) = _uiState.update { it.copy(firstName = value) }
    fun onLastNameChange(value: String) = _uiState.update { it.copy(lastName = value) }
    fun onRoleChange(value: String) = _uiState.update { it.copy(role = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onCurrentPasswordChange(value: String) = _uiState.update { it.copy(currentPassword = value) }
    fun onNewPasswordChange(value: String) = _uiState.update { it.copy(newPassword = value) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value) }

    fun onImagePicked(bytes: ByteArray) {
        _imageBytes.update { bytes }
    }

    fun save(onSuccess: () -> Unit) {
        val id = uid ?: return
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            _imageBytes.value?.let { bytes ->
                storageRepo.uploadFile("profile_images/$id.jpg", bytes, "image/jpeg")
                    .onSuccess { url -> employeeRepo.updateProfileImage(id, url) }
            }

            employeeRepo.updateProfile(id, state.firstName.trim(), state.lastName.trim(), state.role.trim(), state.email.trim())
                .onSuccess { onSuccess() }
                .onFailure { e -> _uiState.update { it.copy(message = "Fehler beim Speichern: ${e.message}") } }

            _uiState.update { it.copy(isSaving = false) }
        }
    }

    fun changePassword() {
        val state = _uiState.value
        val error = when {
            state.currentPassword.isEmpty() -> "Bitte aktuelles Passwort eingeben."
            state.newPassword.isEmpty() -> "Bitte neues Passwort eingeben."
            state.newPassword != state.confirmPassword -> "Die neuen Passwörter stimmen nicht überein."
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(message = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isChangingPassword = true) }
            authRepo.changePassword(state.currentPassword, state.newPassword)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            message = "Passwort erfolgreich geändert.",
                            currentPassword = "",
                            newPassword = "",
                            confirmPassword = ""
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(message = "Fehler: ${e.message}") } }
            _uiState.update { it.copy(isChangingPassword = false) }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
