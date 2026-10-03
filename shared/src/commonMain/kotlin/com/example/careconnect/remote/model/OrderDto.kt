package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String? = null,
    val state: String? = null,
    val notes: List<String>? = null,
    val description: String? = null,
    val patientId: String? = null,
    val patientName: String? = null,
    val createdBy: String? = null,
    val assignedTo: String? = null,
    val employeeId: String? = null,
    val dueDate: Timestamp? = null,
    val createdAt: Timestamp? = null
)

fun OrderDto.toOrder(docId: String): Order = Order(
    id = docId,
    state = OrderState.fromValue(state),
    notes = notes.orEmpty(),
    description = description ?: "",
    patientId = patientId ?: "",
    patientName = patientName ?: "",
    createdBy = createdBy ?: "",
    employeeId = employeeId?.ifEmpty { null } ?: assignedTo ?: "",
    dueDate = dueDate?.toMillis(),
    createdAt = createdAt?.toMillis()
)

fun Order.toDto(): OrderDto = OrderDto(
    id = id,
    state = state.value,
    notes = notes,
    description = description,
    patientId = patientId,
    patientName = patientName,
    createdBy = createdBy,
    assignedTo = "",
    employeeId = employeeId,
    dueDate = dueDate?.toTimestamp(),
    createdAt = createdAt?.toTimestamp()
)
