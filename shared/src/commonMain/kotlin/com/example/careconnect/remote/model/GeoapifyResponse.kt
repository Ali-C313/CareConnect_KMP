package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// Geocoding / Autocomplete mit format=json
@Serializable
data class GeoapifyResponse(
    val results: List<GeoapifyResult> = emptyList()
)
