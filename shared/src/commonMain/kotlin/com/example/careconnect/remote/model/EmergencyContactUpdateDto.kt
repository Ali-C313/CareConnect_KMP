package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// Nur für Updates mit merge = true
@Serializable
data class EmergencyContactUpdateDto(
    val emergencyContact: EmergencyContactDto
)
