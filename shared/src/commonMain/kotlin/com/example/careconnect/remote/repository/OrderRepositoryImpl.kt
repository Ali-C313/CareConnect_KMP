package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.repository.OrderRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.model.OrderDto
import com.example.careconnect.remote.model.toDto
import com.example.careconnect.remote.model.toOrder
import com.example.careconnect.remote.model.toTimestamp
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class OrderRepositoryImpl(
    private val db: FirebaseFirestore
) : OrderRepository {

    private val ordersRef = db.collection(CollectionNames.ORDERS)

    override fun getOrders(): Flow<List<Order>> = ordersRef.snapshots
        .map { it.toOrders() }
        .catch { emit(emptyList()) }

    override fun getOrder(id: String): Flow<Order?> = ordersRef.document(id).snapshots
        .map { doc ->
            if (!doc.exists) null
            else runCatching { doc.data<OrderDto>().toOrder(doc.id) }.getOrNull()
        }
        .catch { emit(null) }

    override fun getOrdersForPatient(patientId: String): Flow<List<Order>> = ordersRef
        .where { "patientId" equalTo patientId }
        .snapshots
        .map { it.toOrders() }
        .catch { emit(emptyList()) }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createOrder(order: Order): Result<Unit> = runCatching {
        val id = order.id.ifEmpty { Uuid.random().toString() }
        ordersRef.document(id).set(OrderDto.serializer(), order.copy(id = id).toDto(), merge = false)
    }

    override suspend fun updateOrder(
        id: String,
        patientId: String,
        patientName: String,
        description: String,
        dueDate: Long
    ): Result<Unit> = runCatching {
        ordersRef.document(id).updateFields {
            "patientId" to patientId
            "patientName" to patientName
            "description" to description
            "dueDate" to dueDate.toTimestamp()
        }
    }

    override suspend fun acceptOrder(id: String, employeeId: String): Result<Unit> = runCatching {
        ordersRef.document(id).updateFields {
            "state" to OrderState.AKTUELL.value
            "employeeId" to employeeId
        }
    }

    override suspend fun completeOrder(id: String): Result<Unit> = runCatching {
        ordersRef.document(id).updateFields {
            "state" to OrderState.ERLEDIGT.value
        }
    }

    override suspend fun deleteOrder(id: String): Result<Unit> = runCatching {
        ordersRef.document(id).delete()
    }

    // Neueste zuerst, kaputte Dokumente überspringen
    private fun QuerySnapshot.toOrders(): List<Order> = documents
        .mapNotNull { doc ->
            runCatching { doc.data<OrderDto>().toOrder(doc.id) }.getOrNull()
        }
        .sortedByDescending { it.createdAt ?: 0L }
}
