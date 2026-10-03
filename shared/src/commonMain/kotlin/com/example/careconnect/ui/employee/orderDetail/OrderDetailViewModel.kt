package com.example.careconnect.ui.employee.orderDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.domain.repository.LocationRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.location.TrackingManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class OrderDetailViewModel(
    private val orderRepo: OrderRepository,
    private val authRepo: AuthRepository,
    private val trackingManager: TrackingManager,
    patientRepo: PatientRepository,
    employeeRepo: EmployeeRepository,
    locationRepo: LocationRepository,
    geoRepo: GeoRepository
) : ViewModel() {

    private val orderId = MutableStateFlow<String?>(null)

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    val order: StateFlow<Order?> = orderId
        .filterNotNull()
        .flatMapLatest { orderRepo.getOrder(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val patient: StateFlow<Patient?> = order
        .map { it?.patientId ?: "" }
        .distinctUntilChanged()
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else patientRepo.getPatient(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val assignedEmployee: StateFlow<Employee?> = order
        .map { it?.employeeId ?: "" }
        .distinctUntilChanged()
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else employeeRepo.getEmployee(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Patientenadresse -> Koordinate
    val patientCoordinate: StateFlow<Coordinate?> = patient
        .map { it?.fullAddress ?: "" }
        .distinctUntilChanged()
        .mapLatest { address -> geoRepo.geocode(address).getOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Live-Standort des Mitarbeiters, nur wenn der Auftrag aktuell ist
    val employeeCoordinate: StateFlow<Coordinate?> = order
        .map { current -> current?.takeIf { it.state == OrderState.AKTUELL }?.employeeId ?: "" }
        .distinctUntilChanged()
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else locationRepo.observeEmployeeLocation(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ETA und Entfernung (statt MKDirections)
    val route: StateFlow<RouteInfo?> = combine(employeeCoordinate, patientCoordinate) { employee, patient ->
        employee to patient
    }
        .mapLatest { (employee, patient) ->
            if (employee == null || patient == null) null
            else geoRepo.getRoute(employee, patient).getOrNull()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun load(id: String) {
        orderId.update { id }
    }

    fun accept() {
        val id = orderId.value ?: return
        val employeeId = authRepo.currentUserId ?: return
        runAction("Fehler beim Annehmen") {
            orderRepo.acceptOrder(id, employeeId).also { result ->
                if (result.isSuccess) trackingManager.start(employeeId)
            }
        }
    }

    fun complete() {
        val id = orderId.value ?: return
        runAction("Fehler beim Abschließen") {
            orderRepo.completeOrder(id).also { result ->
                if (result.isSuccess) trackingManager.stop()
            }
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val id = orderId.value ?: return
        runAction("Fehler beim Löschen") {
            orderRepo.deleteOrder(id).also { result ->
                if (result.isSuccess) onDeleted()
            }
        }
    }

    fun dismissError() {
        _errorMessage.update { null }
    }

    private fun runAction(errorText: String, action: suspend () -> Result<Unit>) {
        if (_isProcessing.value) return
        viewModelScope.launch {
            _isProcessing.update { true }
            action().onFailure { e ->
                _errorMessage.update { "$errorText: ${e.message}" }
            }
            _isProcessing.update { false }
        }
    }
}
