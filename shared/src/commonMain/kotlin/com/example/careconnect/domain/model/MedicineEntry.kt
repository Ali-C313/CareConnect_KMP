package com.example.careconnect.domain.model

// Medikament im Plan des Patienten
data class MedicineEntry(
    val id: String = "",
    val name: String,
    val dosage: String,
    val timeOfDay: TimeOfDay
)
