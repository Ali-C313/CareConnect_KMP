package com.example.careconnect.platform

import com.example.careconnect.domain.model.Coordinate
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLPlacemark
import kotlin.coroutines.resume

// Adresse -> Koordinate mit CLGeocoder
class IosGeocoder : PlatformGeocoder {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun geocode(address: String): Coordinate? {
        if (address.isBlank()) return null
        val geocoder = CLGeocoder()

        return suspendCancellableCoroutine { cont ->
            cont.invokeOnCancellation { geocoder.cancelGeocode() }
            geocoder.geocodeAddressString(address) { placemarks, _ ->
                val placemark = placemarks?.firstOrNull() as? CLPlacemark
                val coordinate = placemark?.location?.coordinate?.useContents {
                    Coordinate(latitude, longitude)
                }
                if (cont.isActive) cont.resume(coordinate)
            }
        }
    }
}
