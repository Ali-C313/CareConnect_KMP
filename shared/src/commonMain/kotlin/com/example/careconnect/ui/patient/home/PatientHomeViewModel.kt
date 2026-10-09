package com.example.careconnect.ui.patient.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.i18n.tr
import com.example.careconnect.util.currentTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PatientHomeViewModel(
    private val orderRepo: OrderRepository,
    authRepo: AuthRepository,
    patientRepo: PatientRepository
) : ViewModel() {

    private val uid = authRepo.currentUserId

    val patient: StateFlow<Patient?> = (if (uid != null) patientRepo.getPatient(uid) else flowOf(null))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    // Dringlicher Auftrag (createEmergencyOrder in iOS)
    fun createEmergencyOrder() {
        val id = uid ?: return
        val current = patient.value

        viewModelScope.launch {
            val order = Order(
                id = "",
                state = OrderState.OFFEN,
                notes = listOf(tr("Automatisch generierter Notfallauftrag")),
                description = tr("Notfallauftrag"),
                patientId = id,
                patientName = current?.name ?: "",
                createdBy = id,
                dueDate = currentTimeMillis(),
                createdAt = currentTimeMillis()
            )
            orderRepo.createOrder(order)
                .onSuccess { _message.update { tr("Der Notfallauftrag wurde erstellt. Ein Mitarbeiter kümmert sich darum.") } }
                .onFailure { e -> _message.update { tr("Fehler beim Erstellen des Notfallauftrags: {0}", e.message) } }
        }
    }

    fun dismissMessage() {
        _message.update { null }
    }
}
