package com.example.careconnect.platform

import android.net.Uri

actual fun mapsSearchUrl(query: String): String =
    "https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}"

actual fun mapsDirectionsUrl(latitude: Double, longitude: Double): String =
    "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude&travelmode=driving"
