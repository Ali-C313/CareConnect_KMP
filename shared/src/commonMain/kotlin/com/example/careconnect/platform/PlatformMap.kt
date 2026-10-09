package com.example.careconnect.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.MapMarker

// Android: OpenStreetMap, iOS: MapKit. route wird als Linie eingezeichnet
@Composable
expect fun PlatformMap(
    markers: List<MapMarker>,
    modifier: Modifier = Modifier,
    route: List<Coordinate> = emptyList()
)
