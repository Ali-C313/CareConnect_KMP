package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.remote.Secrets
import com.example.careconnect.remote.model.GeoapifyResponse
import com.example.careconnect.remote.model.GeoapifyRouteResponse
import com.example.careconnect.remote.model.toAddressSuggestion
import com.example.careconnect.remote.model.toCoordinate
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

// Adresssuche, Geocoding und Route über Geoapify (funktioniert auf Android und iOS gleich)
class GeoRepositoryImpl(
    private val client: HttpClient
) : GeoRepository {

    private val baseUrl = "https://api.geoapify.com/v1/"
    private val apiKey = Secrets.GEOAPIFY_API_KEY

    override suspend fun searchAddress(query: String): Result<List<AddressSuggestion>> = runCatching {
        if (query.trim().length < 3) return@runCatching emptyList()
        val response = client.get("${baseUrl}geocode/autocomplete") {
            parameter("text", query)
            parameter("limit", 5)
            parameter("lang", "de")
            parameter("filter", "countrycode:de")
            parameter("format", "json")
            parameter("apiKey", apiKey)
        }
        response.body<GeoapifyResponse>().results.map { it.toAddressSuggestion() }
    }

    override suspend fun geocode(address: String): Result<Coordinate?> = runCatching {
        if (address.isBlank()) return@runCatching null
        val response = client.get("${baseUrl}geocode/search") {
            parameter("text", address)
            parameter("limit", 1)
            parameter("lang", "de")
            parameter("format", "json")
            parameter("apiKey", apiKey)
        }
        response.body<GeoapifyResponse>().results.firstOrNull()?.toCoordinate()
    }

    override suspend fun getRoute(from: Coordinate, to: Coordinate): Result<RouteInfo?> = runCatching {
        val response = client.get("${baseUrl}routing") {
            parameter("waypoints", "${from.latitude},${from.longitude}|${to.latitude},${to.longitude}")
            parameter("mode", "drive")
            parameter("apiKey", apiKey)
        }
        val properties = response.body<GeoapifyRouteResponse>().features.firstOrNull()?.properties
        val distance = properties?.distance
        val time = properties?.time
        if (distance != null && time != null) RouteInfo(distance, time) else null
    }
}
