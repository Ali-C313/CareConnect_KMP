package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class LinkedOrgDto(
    val orgId: String? = null,
    val orgName: String? = null
)
