package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// Routing (GeoJSON)
@Serializable
data class GeoapifyRouteResponse(
    val features: List<GeoapifyRouteFeature> = emptyList()
)
