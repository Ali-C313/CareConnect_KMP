package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

// Antwort vom OSRM-Routing (geometries=geojson)
@Serializable
data class OsrmResponse(
    val code: String = "",
    val routes: List<OsrmRoute> = emptyList()
)
