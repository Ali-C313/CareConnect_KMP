package com.example.careconnect.platform

import com.example.careconnect.domain.model.Coordinate

// Adresse -> Koordinate ohne API-Key: Android Geocoder, iOS CLGeocoder
interface PlatformGeocoder {
    suspend fun geocode(address: String): Coordinate?
}
