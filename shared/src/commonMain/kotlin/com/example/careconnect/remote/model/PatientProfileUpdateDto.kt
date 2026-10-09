package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

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
