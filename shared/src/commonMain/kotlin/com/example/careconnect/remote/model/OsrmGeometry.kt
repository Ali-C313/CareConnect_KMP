package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// GeoJSON: [lon, lat]
@Serializable
data class OsrmGeometry(
    val coordinates: List<List<Double>> = emptyList()
)
