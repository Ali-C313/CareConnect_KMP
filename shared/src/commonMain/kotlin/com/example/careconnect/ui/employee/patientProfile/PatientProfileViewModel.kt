package com.example.careconnect.ui.employee.patientProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.EmergencyContact
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.domain.repository.PatientRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PatientProfileViewModel(
    private val patientRepo: PatientRepository,
    private val geoRepo: GeoRepository
) : ViewModel() {

    private val patientId = MutableStateFlow<String?>(null)

    val patient: StateFlow<Patient?> = patientId
        .filterNotNull()
        .flatMapLatest { patientRepo.getPatient(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Adresse -> Koordinate für die Karte
    val coordinate: StateFlow<Coordinate?> = patient
        .map { it?.fullAddress ?: "" }
        .distinctUntilChanged()
        .mapLatest { address -> geoRepo.geocode(address).getOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Notfallkontakt-Editor
    private val _editContact = MutableStateFlow(EmergencyContact())
    val editContact = _editContact.asStateFlow()

    private val _addressSuggestions = MutableStateFlow<List<AddressSuggestion>>(emptyList())
    val addressSuggestions = _addressSuggestions.asStateFlow()

    private var searchJob: Job? = null

    fun load(id: String) {
        patientId.update { id }
    }

    fun startEditingContact() {
        _editContact.update { patient.value?.emergencyContact ?: EmergencyContact() }
        _addressSuggestions.update { emptyList() }
    }

    fun onContactChange(contact: EmergencyContact) {
        _editContact.update { contact }
    }

    fun onContactAddressChange(address: String) {
        _editContact.update { it.copy(address = address) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _addressSuggestions.update { geoRepo.searchAddress(address).getOrDefault(emptyList()) }
        }
    }

    fun onContactSuggestionClick(suggestion: AddressSuggestion) {
        searchJob?.cancel()
        _editContact.update { it.copy(address = suggestion.formatted) }
        _addressSuggestions.update { emptyList() }
    }

    fun saveEmergencyContact(onDone: () -> Unit) {
        val id = patientId.value ?: return
        viewModelScope.launch {
            patientRepo.saveEmergencyContact(id, _editContact.value)
            onDone()
        }
    }
}
