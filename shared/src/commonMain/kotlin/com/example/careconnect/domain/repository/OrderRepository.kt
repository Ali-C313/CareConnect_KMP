package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
    fun getOrder(id: String): Flow<Order?>
    fun getOrdersForPatient(patientId: String): Flow<List<Order>>
    suspend fun createOrder(order: Order): Result<Unit>
    suspend fun updateOrder(
        id: String,
        patientId: String,
        patientName: String,
        description: String,
        dueDate: Long
    ): Result<Unit>
    suspend fun acceptOrder(id: String, employeeId: String): Result<Unit>
    suspend fun completeOrder(id: String): Result<Unit>
    suspend fun deleteOrder(id: String): Result<Unit>
}
