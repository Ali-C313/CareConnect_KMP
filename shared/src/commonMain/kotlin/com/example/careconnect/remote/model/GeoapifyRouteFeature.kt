package com.example.careconnect.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoapifyRouteFeature(
    val properties: GeoapifyRouteProperties = GeoapifyRouteProperties()
)
