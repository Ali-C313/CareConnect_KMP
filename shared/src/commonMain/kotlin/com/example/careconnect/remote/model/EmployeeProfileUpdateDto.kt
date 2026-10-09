package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// Nur für Updates mit merge = true
@Serializable
data class EmployeeProfileUpdateDto(
    val firstName: String,
    val lastName: String,
    val role: String,
    val email: String
)
