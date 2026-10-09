package com.example.careconnect.platform

// Android öffnet Google Maps, iOS öffnet Apple Maps
expect fun mapsSearchUrl(query: String): String

expect fun mapsDirectionsUrl(latitude: Double, longitude: Double): String

// Route über mehrere Adressen, Start ist der aktuelle Standort
fun tourUrl(addresses: List<String>): String? {
    if (addresses.isEmpty()) return null
    val destination = urlEncode(addresses.last())
    val waypoints = addresses.dropLast(1).joinToString("%7C") { urlEncode(it) }
    return "https://www.google.com/maps/dir/?api=1&travelmode=driving&destination=$destination" +
        if (waypoints.isNotEmpty()) "&waypoints=$waypoints" else ""
}

private fun urlEncode(value: String): String = buildString {
    value.encodeToByteArray().forEach { byte ->
        val code = byte.toInt() and 0xFF
        val char = code.toChar()
        if (char in 'a'..'z' || char in 'A'..'Z' || char in '0'..'9' || char in "-_.~") {
            append(char)
        } else {
            append('%')
            append(code.toString(16).uppercase().padStart(2, '0'))
        }
    }
}
