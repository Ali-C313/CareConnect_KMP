package com.example.careconnect.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.UserProfile
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.domain.repository.StorageRepository
import com.example.careconnect.domain.repository.UserRepository
import com.example.careconnect.i18n.tr
import com.example.careconnect.util.StringHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Legt nur das Konto an, die Organisation kommt danach im Onboarding
class RegisterViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val storageRepo: StorageRepository,
    private val geoRepo: GeoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    // Bild separat, weil ByteArray in data classes kein sinnvolles equals hat
    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes = _imageBytes.asStateFlow()

    private var searchJob: Job? = null

    fun onFirstNameChange(value: String) = _uiState.update { it.copy(firstName = value) }
    fun onLastNameChange(value: String) = _uiState.update { it.copy(lastName = value) }
    fun onBirthDateChange(value: Long) = _uiState.update { it.copy(birthDate = value) }
    fun onCityChange(value: String) = _uiState.update { it.copy(city = value) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }

    fun onImagePicked(bytes: ByteArray) {
        _imageBytes.update { bytes }
    }

    fun onAddressChange(value: String) {
        _uiState.update { it.copy(address = value) }
        // Kurz warten, damit nicht bei jedem Buchstaben gesucht wird
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            val suggestions = geoRepo.searchAddress(value).getOrDefault(emptyList())
            _uiState.update { it.copy(addressSuggestions = suggestions) }
        }
    }

    fun onSuggestionClick(suggestion: AddressSuggestion) {
        searchJob?.cancel()
        _uiState.update { it.copy(address = suggestion.formatted, addressSuggestions = emptyList()) }
    }

    fun register() {
        val state = _uiState.value
        val error = validate(state)
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val uid = authRepo.register(state.email, state.password).getOrElse { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = tr("Fehler bei der Registrierung: {0}", e.message)) }
                return@launch
            }

            // Profilbild hochladen (Fehler hier ist nicht schlimm)
            val imageUrl = _imageBytes.value?.let { bytes ->
                storageRepo.uploadFile("profileImages/$uid.jpg", bytes, "image/jpeg").getOrNull()
            } ?: ""

            // Profil als letztes speichern, danach geht es ins Onboarding
            val profile = UserProfile(
                uid = uid,
                firstName = state.firstName.trim(),
                lastName = state.lastName.trim(),
                email = state.email.trim(),
                birthDate = state.birthDate,
                address = state.address.trim(),
                city = state.city.trim(),
                phone = state.phone.trim(),
                profileImageUrl = imageUrl
            )
            userRepo.createProfile(profile).onFailure { e ->
                _uiState.update { it.copy(errorMessage = tr("Fehler beim Speichern der Profildaten: {0}", e.message)) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /** @return Fehlermeldung oder null, wenn alles passt */
    private fun validate(state: RegisterUiState): String? = when {
        state.firstName.isBlank() || state.lastName.isBlank() -> tr("Bitte Vor- und Nachname eingeben.")
        !StringHelper.isValidEmail(state.email) -> tr("Bitte eine gültige E-Mail-Adresse eingeben.")
        state.password.length < 6 -> tr("Das Passwort muss mindestens 6 Zeichen haben.")
        state.password != state.confirmPassword -> tr("Passwörter stimmen nicht überein.")
        else -> null
    }
}
