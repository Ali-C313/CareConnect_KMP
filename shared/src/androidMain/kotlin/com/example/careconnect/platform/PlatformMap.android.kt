package com.example.careconnect.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.example.careconnect.domain.model.MapMarker

@Composable
actual fun PlatformMap(
    markers: List<MapMarker>,
    modifier: Modifier
) {
    val cameraPositionState = rememberCameraPositionState()
    var isMapLoaded by remember { mutableStateOf(false) }

    // Kamera auf alle Marker ausrichten (erst wenn die Karte geladen ist)
    LaunchedEffect(markers, isMapLoaded) {
        if (!isMapLoaded || markers.isEmpty()) return@LaunchedEffect
        val update = if (markers.size == 1) {
            val point = markers.first().coordinate
            CameraUpdateFactory.newLatLngZoom(LatLng(point.latitude, point.longitude), 14f)
        } else {
            val bounds = LatLngBounds.builder().apply {
                markers.forEach { include(LatLng(it.coordinate.latitude, it.coordinate.longitude)) }
            }.build()
            CameraUpdateFactory.newLatLngBounds(bounds, 120)
        }
        runCatching { cameraPositionState.move(update) }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false),
        onMapLoaded = { isMapLoaded = true }
    ) {
        markers.forEach { marker ->
            key(marker.title) {
                Marker(
                    state = rememberUpdatedMarkerState(
                        position = LatLng(marker.coordinate.latitude, marker.coordinate.longitude)
                    ),
                    title = marker.title,
                    // Mitarbeiter blau, Patient/Ziel rot
                    icon = BitmapDescriptorFactory.defaultMarker(
                        if (marker.isEmployee) BitmapDescriptorFactory.HUE_AZURE else BitmapDescriptorFactory.HUE_RED
                    )
                )
            }
        }
    }
}
