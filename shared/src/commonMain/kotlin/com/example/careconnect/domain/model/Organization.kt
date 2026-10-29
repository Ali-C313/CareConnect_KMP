package com.example.careconnect.domain.model

// Pflegedienst, Praxis oder Klinik
data class Organization(
    val id: String,
    val name: String,
    val type: OrganizationType,
    val address: String = "",
    val ownerId: String = ""
)
