package com.example.careconnect.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.MapMarker
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import platform.CoreLocation.CLLocationCoordinate2D
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKMarkerAnnotationView
import platform.MapKit.MKOverlayProtocol
import platform.MapKit.MKOverlayRenderer
import platform.MapKit.MKPointAnnotation
import platform.MapKit.MKPolyline
import platform.MapKit.MKPolylineRenderer
import platform.MapKit.addOverlay
import platform.MapKit.removeOverlay
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIView
import platform.UIKit.systemBlueColor
import platform.darwin.NSObject

// Hält Marker und Linie fest und liefert Auto-Icon und Linienfarbe
@OptIn(ExperimentalForeignApi::class)
private class MapDelegate : NSObject(), MKMapViewDelegateProtocol {
    val annotations = mutableMapOf<String, MKPointAnnotation>()
    var employeeTitles: Set<String> = emptySet()
    var line: MKPolyline? = null
    var lastFit: List<String> = emptyList()

    override fun mapView(mapView: MKMapView, rendererForOverlay: MKOverlayProtocol): MKOverlayRenderer {
        val polyline = rendererForOverlay as? MKPolyline ?: return MKOverlayRenderer(overlay = rendererForOverlay)
        return MKPolylineRenderer(polyline = polyline).apply {
            strokeColor = UIColor.systemBlueColor
            lineWidth = 6.0
        }
    }

    override fun mapView(mapView: MKMapView, viewForAnnotation: MKAnnotationProtocol): MKAnnotationView? {
        val point = viewForAnnotation as? MKPointAnnotation ?: return null
        val title = point.title ?: return null
        if (title !in employeeTitles) return null

        val view = mapView.dequeueReusableAnnotationViewWithIdentifier("car") as? MKMarkerAnnotationView
            ?: MKMarkerAnnotationView(annotation = point, reuseIdentifier = "car")
        view.annotation = point
        view.glyphImage = UIImage.systemImageNamed("car.fill")
        view.markerTintColor = UIColor.systemBlueColor
        return view
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformMap(
    markers: List<MapMarker>,
    modifier: Modifier,
    route: List<Coordinate>
) {
    // State lesen, damit update bei neuen Werten erneut aufgerufen wird
    val currentMarkers by rememberUpdatedState(markers)
    val currentRoute by rememberUpdatedState(route)
    val mapDelegate = remember { MapDelegate() }

    UIKitView(
        factory = { MKMapView().apply { delegate = mapDelegate } },
        modifier = modifier,
        update = { mapView ->
            mapDelegate.employeeTitles = currentMarkers.filter { it.isEmployee }.map { it.title }.toSet()

            // Linie neu zeichnen
            mapDelegate.line?.let { mapView.removeOverlay(it) }
            mapDelegate.line = null
            if (currentRoute.size >= 2) {
                val polyline = memScoped {
                    val coords = allocArray<CLLocationCoordinate2D>(currentRoute.size)
                    currentRoute.forEachIndexed { index, point ->
                        coords[index].latitude = point.latitude
                        coords[index].longitude = point.longitude
                    }
                    MKPolyline.polylineWithCoordinates(coords, currentRoute.size.toULong())
                }
                mapView.addOverlay(polyline)
                mapDelegate.line = polyline
            }

            // Weggefallene Marker entfernen
            val titles = currentMarkers.map { it.title }.toSet()
            (mapDelegate.annotations.keys - titles).forEach { title ->
                mapDelegate.annotations.remove(title)?.let { mapView.removeAnnotation(it) }
            }

            currentMarkers.forEach { marker ->
                val coordinate = CLLocationCoordinate2DMake(marker.coordinate.latitude, marker.coordinate.longitude)
                val existing = mapDelegate.annotations[marker.title]
                if (existing == null) {
                    val annotation = MKPointAnnotation()
                    annotation.setCoordinate(coordinate)
                    annotation.setTitle(marker.title)
                    mapView.addAnnotation(annotation)
                    mapDelegate.annotations[marker.title] = annotation
                } else {
                    // Auto gleitet zur neuen Position
                    UIView.animateWithDuration(1.5) { existing.setCoordinate(coordinate) }
                }
            }

            val fitKey = currentMarkers.map { it.title } + if (currentRoute.size >= 2) listOf("route") else emptyList()
            if (fitKey != mapDelegate.lastFit && currentMarkers.isNotEmpty()) {
                mapDelegate.lastFit = fitKey
                mapView.showAnnotations(mapView.annotations, animated = true)
            }
        }
    )
}
