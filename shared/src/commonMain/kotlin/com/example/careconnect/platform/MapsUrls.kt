package com.example.careconnect.platform

// Android öffnet Google Maps, iOS öffnet Apple Maps
expect fun mapsSearchUrl(query: String): String

expect fun mapsDirectionsUrl(latitude: Double, longitude: Double): String
