package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// Freigegebene Organisation in Firestore
@Serializable
data class LinkedOrgDto(
    val orgId: String? = null,
    val orgName: String? = null
)
