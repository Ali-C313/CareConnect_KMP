package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Organization
import com.example.careconnect.domain.model.OrganizationType
import kotlinx.serialization.Serializable

// Öffentlicher Eintrag, damit Patienten die Organisation finden
@Serializable
data class DirectoryDto(
    val name: String? = null,
    val nameLower: String? = null,
    val type: String? = null,
    val address: String? = null
)

// Firestore -> App-Model
fun DirectoryDto.toOrganization(id: String): Organization = Organization(
    id = id,
    name = name ?: "",
    type = OrganizationType.fromValue(type),
    address = address ?: ""
)
