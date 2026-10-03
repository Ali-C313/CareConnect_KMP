package com.example.careconnect.domain.model

data class Coordinate(
    val latitude: Double,
    val longitude: Double
)

data class AddressSuggestion(
    val formatted: String,
    val coordinate: Coordinate?
)

data class RouteInfo(
    val distanceMeters: Double,
    val durationSeconds: Double
)

data class MapMarker(
    val title: String,
    val coordinate: Coordinate,
    val isEmployee: Boolean = false
)
