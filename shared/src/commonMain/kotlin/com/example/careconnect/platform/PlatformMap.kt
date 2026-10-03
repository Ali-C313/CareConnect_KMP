package com.example.careconnect.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.careconnect.domain.model.MapMarker

// Android: Google Maps, iOS: MapKit
@Composable
expect fun PlatformMap(
    markers: List<MapMarker>,
    modifier: Modifier = Modifier
)
