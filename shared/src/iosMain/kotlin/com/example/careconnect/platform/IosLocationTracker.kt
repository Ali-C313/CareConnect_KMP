package com.example.careconnect.platform

import com.example.careconnect.domain.model.Coordinate
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLLocationAccuracyHundredMeters
import platform.darwin.NSObject

// Standort auf iOS über CLLocationManager
class IosLocationTracker : LocationTracker {

    private val manager = CLLocationManager()
    private var delegate: LocationDelegate? = null

    override fun start(onLocation: (Coordinate) -> Unit) {
        val newDelegate = LocationDelegate(onLocation)
        delegate = newDelegate
        manager.delegate = newDelegate
        manager.desiredAccuracy = kCLLocationAccuracyHundredMeters
        manager.distanceFilter = 50.0
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()
    }

    override fun stop() {
        manager.stopUpdatingLocation()
        manager.delegate = null
        delegate = null
    }
}

// Empfängt die Standorte vom CLLocationManager
private class LocationDelegate(
    private val onLocation: (Coordinate) -> Unit
) : NSObject(), CLLocationManagerDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
        location.coordinate.useContents {
            onLocation(Coordinate(latitude, longitude))
        }
    }

    // Nach der Erlaubnis Tracking starten
    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val status = manager.authorizationStatus
        if (status == kCLAuthorizationStatusAuthorizedWhenInUse || status == kCLAuthorizationStatusAuthorizedAlways) {
            manager.startUpdatingLocation()
        }
    }
}
