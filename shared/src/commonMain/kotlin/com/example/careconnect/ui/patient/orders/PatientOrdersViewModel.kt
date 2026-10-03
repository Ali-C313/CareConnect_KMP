package com.example.careconnect.ui.patient.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.domain.repository.LocationRepository
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.domain.repository.PatientRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

// "Live Notruf" in iOS: aktiver Auftrag mit Karte, offene und erledigte Aufträge
@OptIn(ExperimentalCoroutinesApi::class)
class PatientOrdersViewModel(
    orderRepo: OrderRepository,
    employeeRepo: EmployeeRepository,
    locationRepo: LocationRepository,
    patientRepo: PatientRepository,
    geoRepo: GeoRepository,
    authRepo: AuthRepository
) : ViewModel() {

    private val uid = authRepo.currentUserId ?: ""

    private val orders = orderRepo.getOrdersForPatient(uid)

    val activeOrder: StateFlow<Order?> = orders
        .map { list -> list.firstOrNull { it.state == OrderState.AKTUELL } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val openOrders: StateFlow<List<Order>> = orders
        .map { list -> list.filter { it.state == OrderState.OFFEN } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historyOrders: StateFlow<List<Order>> = orders
        .map { list -> list.filter { it.state == OrderState.ERLEDIGT } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val employeeId = activeOrder
        .map { it?.employeeId ?: "" }
        .distinctUntilChanged()

    val employee: StateFlow<Employee?> = employeeId
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else employeeRepo.getEmployee(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val employeeCoordinate: StateFlow<Coordinate?> = employeeId
        .flatMapLatest { id -> if (id.isEmpty()) flowOf(null) else locationRepo.observeEmployeeLocation(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Ziel = eigene Adresse aus dem Patientenprofil
    val homeCoordinate: StateFlow<Coordinate?> = (if (uid.isNotEmpty()) patientRepo.getPatient(uid) else flowOf(null))
        .map { it?.fullAddress ?: "" }
        .distinctUntilChanged()
        .mapLatest { address -> geoRepo.geocode(address).getOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val route: StateFlow<RouteInfo?> = combine(employeeCoordinate, homeCoordinate) { from, to -> from to to }
        .mapLatest { (from, to) ->
            if (from == null || to == null) null
            else geoRepo.getRoute(from, to).getOrNull()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
