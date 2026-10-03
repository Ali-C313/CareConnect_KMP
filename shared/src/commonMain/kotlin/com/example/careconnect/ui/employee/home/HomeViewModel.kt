package com.example.careconnect.ui.employee.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.StaffStatus
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.domain.repository.EmployeeRepository
import com.example.careconnect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val employeeRepo: EmployeeRepository,
    orderRepo: OrderRepository,
    authRepo: AuthRepository
) : ViewModel() {

    private val uid = authRepo.currentUserId

    val employees: StateFlow<List<Employee>> = employeeRepo.getEmployees()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = orderRepo.getOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentEmployee: StateFlow<Employee?> = (if (uid != null) employeeRepo.getEmployee(uid) else flowOf(null))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateStatus(status: StaffStatus) {
        val id = uid ?: return
        viewModelScope.launch {
            employeeRepo.updateStatus(id, status)
        }
    }
}
