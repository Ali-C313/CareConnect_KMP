package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// Nur für Updates mit merge = true
@Serializable
data class LinkedOrgsUpdateDto(
    val linkedOrgIds: List<String>,
    val linkedOrgs: List<LinkedOrgDto>
)
