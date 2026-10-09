package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoapifyRouteProperties(
    val distance: Double? = null,
    val time: Double? = null
)
