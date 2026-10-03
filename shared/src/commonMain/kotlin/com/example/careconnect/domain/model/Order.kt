package com.example.careconnect.domain.model

enum class OrderState(val value: String, val displayName: String) {
    OFFEN("offen", "Offen"),
    AKTUELL("aktuell", "Aktuell"),
    ERLEDIGT("erledigt", "Erledigt");

    companion object {
        fun fromValue(value: String?): OrderState =
            entries.firstOrNull { it.value == value?.lowercase() } ?: OFFEN
    }
}

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
    val createdAt: Long? = null
) {
    val displayTitle: String
        get() = "Auftrag #${id.take(6)}"
}
