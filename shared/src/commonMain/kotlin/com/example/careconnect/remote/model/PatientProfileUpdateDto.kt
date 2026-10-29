package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// Felder, die der Patient selbst ändern darf
@Serializable
data class PatientProfileUpdateDto(
    val firstName: String,
    val lastName: String,
    val address: String,
    val city: String,
    val email: String,
    val phone: String,
    val allergies: String,
    val medicalHistory: String
)
