package com.example.careconnect.platform

import com.example.careconnect.domain.model.Coordinate

// Umsetzung in androidMain (FusedLocation) und iosMain (CLLocationManager)
interface LocationTracker {
    fun start(onLocation: (Coordinate) -> Unit)
    fun stop()
}
