package com.example.careconnect.domain.model

// Notfallkontakt eines Patienten
data class EmergencyContact(
    val name: String = "",
    val relation: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val age: Int? = null
)
