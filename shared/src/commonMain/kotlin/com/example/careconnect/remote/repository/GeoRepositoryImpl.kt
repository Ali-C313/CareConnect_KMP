package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.domain.repository.GeoRepository
import com.example.careconnect.platform.PlatformGeocoder
import com.example.careconnect.remote.Secrets
import com.example.careconnect.remote.model.GeoapifyResponse
import com.example.careconnect.remote.model.GeoapifyRouteResponse
import com.example.careconnect.remote.model.OsrmResponse
import com.example.careconnect.remote.model.toAddressSuggestion
import com.example.careconnect.remote.model.toCoordinate
import com.example.careconnect.remote.model.toRouteInfo
import com.example.careconnect.util.distanceTo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.withTimeoutOrNull

// Adresssuche und Geocoding (Handy-Geocoder, sonst Geoapify), Route über OSRM
class GeoRepositoryImpl(
    private val client: HttpClient,
    private val platformGeocoder: PlatformGeocoder
) : GeoRepository {

    private val baseUrl = "https://api.geoapify.com/v1/"
    private val osrmUrl = "https://router.project-osrm.org/route/v1/driving/"
    private val apiKey = Secrets.GEOAPIFY_API_KEY

    // Ohne echten Key gar nicht erst bei Geoapify anfragen
    private val hasApiKey = apiKey.isNotBlank() && !apiKey.startsWith("DEIN_")

    override suspend fun searchAddress(query: String): Result<List<AddressSuggestion>> = runCatching {
        if (!hasApiKey || query.trim().length < 3) return@runCatching emptyList()
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

        // Erst der Geocoder vom Handy (braucht keinen Key), dann Geoapify
        val local = withTimeoutOrNull(10_000) { runCatching { platformGeocoder.geocode(address) }.getOrNull() }
        if (local != null) return@runCatching local
        if (!hasApiKey) return@runCatching null

        val response = client.get("${baseUrl}geocode/search") {
            parameter("text", address)
            parameter("limit", 1)
            parameter("lang", "de")
            parameter("format", "json")
            parameter("apiKey", apiKey)
        }
        response.body<GeoapifyResponse>().results.firstOrNull()?.toCoordinate()
    }

    // Erst OSRM (Straßenverlauf, kein Key), dann Geoapify, zur Not grob geschätzt
    override suspend fun getRoute(from: Coordinate, to: Coordinate): Result<RouteInfo?> = runCatching {
        osrmRoute(from, to)
            ?: geoapifyRoute(from, to)
            ?: estimateRoute(from, to)
    }

    private suspend fun osrmRoute(from: Coordinate, to: Coordinate): RouteInfo? = withTimeoutOrNull(8_000) {
        runCatching {
            val points = "${from.longitude},${from.latitude};${to.longitude},${to.latitude}"
            val response = client.get("$osrmUrl$points") {
                parameter("overview", "full")
                parameter("geometries", "geojson")
            }.body<OsrmResponse>()
            response.routes.firstOrNull()?.takeIf { response.code == "Ok" }?.toRouteInfo()
        }.getOrNull()
    }

    private suspend fun geoapifyRoute(from: Coordinate, to: Coordinate): RouteInfo? {
        if (!hasApiKey) return null
        return runCatching {
            val response = client.get("${baseUrl}routing") {
                parameter("waypoints", "${from.latitude},${from.longitude}|${to.latitude},${to.longitude}")
                parameter("mode", "drive")
                parameter("apiKey", apiKey)
            }
            val properties = response.body<GeoapifyRouteResponse>().features.firstOrNull()?.properties
            val distance = properties?.distance
            val time = properties?.time
            if (distance != null && time != null) RouteInfo(distance, time, listOf(from, to)) else null
        }.getOrNull()
    }

    // Luftlinie * 1,3 und ca. 30 km/h in der Stadt
    private fun estimateRoute(from: Coordinate, to: Coordinate): RouteInfo {
        val meters = from.distanceTo(to) * 1.3
        return RouteInfo(
            distanceMeters = meters,
            durationSeconds = meters / (30_000.0 / 3600.0),
            path = listOf(from, to)
        )
    }
}
