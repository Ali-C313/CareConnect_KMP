package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Coordinate
import kotlinx.serialization.Serializable

// Geocoding / Autocomplete mit format=json
@Serializable
data class GeoapifyResponse(
    val results: List<GeoapifyResult> = emptyList()
)

@Serializable
data class GeoapifyResult(
    val formatted: String = "",
    val lat: Double? = null,
    val lon: Double? = null
)

// Routing (GeoJSON)
@Serializable
data class GeoapifyRouteResponse(
    val features: List<GeoapifyRouteFeature> = emptyList()
)

@Serializable
data class GeoapifyRouteFeature(
    val properties: GeoapifyRouteProperties = GeoapifyRouteProperties()
)

@Serializable
data class GeoapifyRouteProperties(
    val distance: Double? = null,
    val time: Double? = null
)

fun GeoapifyResult.toAddressSuggestion(): AddressSuggestion = AddressSuggestion(
    formatted = formatted,
    coordinate = toCoordinate()
)

fun GeoapifyResult.toCoordinate(): Coordinate? {
    val latitude = lat ?: return null
    val longitude = lon ?: return null
    return Coordinate(latitude, longitude)
}
