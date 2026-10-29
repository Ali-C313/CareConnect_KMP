package com.example.careconnect.domain.model

import com.example.careconnect.i18n.tr

// Auftrag bzw. Hausbesuch
data class Order(
    val id: String,
    val state: OrderState = OrderState.OFFEN,
    val notes: List<String> = emptyList(),
    val description: String = "",
    val patientId: String = "",
    val patientName: String = "",
    val createdBy: String = "",
    val employeeId: String = "",
    val dueDate: Long? = null,
    val createdAt: Long? = null,
    val report: CompletionReport? = null
) {
    val displayTitle: String
        get() = tr("Auftrag #{0}", id.take(6))
}
