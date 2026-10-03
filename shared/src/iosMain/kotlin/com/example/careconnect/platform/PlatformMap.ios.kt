package com.example.careconnect.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.example.careconnect.domain.model.MapMarker
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformMap(
    markers: List<MapMarker>,
    modifier: Modifier
) {
    // State lesen, damit update bei neuen Markern erneut aufgerufen wird
    val currentMarkers by rememberUpdatedState(markers)

    UIKitView(
        factory = { MKMapView() },
        modifier = modifier,
        update = { mapView ->
            mapView.removeAnnotations(mapView.annotations)
            currentMarkers.forEach { marker ->
                val annotation = MKPointAnnotation()
                annotation.setCoordinate(
                    CLLocationCoordinate2DMake(marker.coordinate.latitude, marker.coordinate.longitude)
                )
                annotation.setTitle(marker.title)
                mapView.addAnnotation(annotation)
            }
            if (currentMarkers.isNotEmpty()) {
                mapView.showAnnotations(mapView.annotations, animated = true)
            }
        }
    )
}
