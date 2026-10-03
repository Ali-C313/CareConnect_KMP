package com.example.careconnect.domain.model

enum class TimeOfDay(val displayName: String) {
    MORNING("Morgens"),
    NOON("Mittags"),
    EVENING("Abends");

    companion object {
        fun fromValue(value: String?): TimeOfDay =
            entries.firstOrNull { it.displayName == value } ?: MORNING
    }
}

data class MedicineEntry(
    val id: String = "",
    val name: String,
    val dosage: String,
    val timeOfDay: TimeOfDay
)
