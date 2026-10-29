package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// Route aus der Geoapify-Antwort
@Serializable
data class GeoapifyRouteFeature(
    val properties: GeoapifyRouteProperties = GeoapifyRouteProperties()
)
