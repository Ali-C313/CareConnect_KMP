package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Organization
import com.example.careconnect.domain.model.OrganizationType
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

// Organisation in Firestore
@Serializable
data class OrganizationDto(
    val name: String? = null,
    val type: String? = null,
    val address: String? = null,
    val ownerId: String? = null,
    val createdAt: Timestamp? = null
)

// Firestore -> App-Model
fun OrganizationDto.toOrganization(id: String): Organization = Organization(
    id = id,
    name = name ?: "",
    type = OrganizationType.fromValue(type),
    address = address ?: "",
    ownerId = ownerId ?: ""
)
