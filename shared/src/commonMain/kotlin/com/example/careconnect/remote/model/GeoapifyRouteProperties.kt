package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// Dauer und Strecke der Geoapify-Route
@Serializable
data class GeoapifyRouteProperties(
    val distance: Double? = null,
    val time: Double? = null
)
