package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class MedicationDto(
    val id: String? = null,
    val name: String? = null
)
