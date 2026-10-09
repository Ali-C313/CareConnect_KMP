package com.example.careconnect.ui.patient.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.MapMarker
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.CareMap
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.currentTimeMillis
import com.example.careconnect.util.toKmString
import com.example.careconnect.util.toMinutesString
import com.example.careconnect.util.toTimeString

// Karte mit Auto und Strecke, vorher fragt die App einmal nach
@Composable
fun LiveSection(
    showLiveLocation: Boolean?,
    employeeCoordinate: Coordinate?,
    homeCoordinate: Coordinate?,
    route: RouteInfo?,
    onAllow: () -> Unit,
    onDeny: () -> Unit
) {
    SectionCard(title = tr("Live-Standort"), icon = Icons.Default.MyLocation) {
        if (showLiveLocation != true) {
            Text(tr("Live-Standort anzeigen?"), style = MaterialTheme.typography.titleMedium)
            Text(
                if (showLiveLocation == false) tr("Du hast den Live-Standort ausgeschaltet.") else tr("Du siehst auf der Karte, wo dein Mitarbeiter gerade ist und wann er ungefähr ankommt. Dafür wird deine Adresse für die Route verwendet."),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAllow) { Text(tr("Anzeigen")) }
                if (showLiveLocation == null) {
                    OutlinedButton(onClick = onDeny) { Text(tr("Nein danke")) }
                }
            }
        } else {
            val markers = buildList {
                employeeCoordinate?.let { add(MapMarker(tr("Mitarbeiter"), it, isEmployee = true)) }
                homeCoordinate?.let { add(MapMarker(tr("Ankunft"), it)) }
            }
            CareMap(
                markers = markers,
                height = 300.dp,
                emptyText = tr("Karte wird geladen…"),
                route = route?.path ?: emptyList()
            )

            if (employeeCoordinate == null) {
                Text(
                    tr("Der Standort des Mitarbeiters erscheint, sobald er unterwegs ist und das Teilen erlaubt hat."),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            route?.let {
                val arrival = currentTimeMillis() + (it.durationSeconds * 1000).toLong()
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoRow(Icons.Default.Straighten, tr("Entfernung"), it.distanceMeters.toKmString(), Modifier.weight(1f))
                    InfoRow(
                        Icons.Default.DirectionsCar,
                        tr("Ankunft"),
                        tr("{0} Uhr ({1})", arrival.toTimeString(), it.durationSeconds.toMinutesString()),
                        Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LiveSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        LiveSection(
            showLiveLocation = null,
            employeeCoordinate = MockData.employeeLocation,
            homeCoordinate = MockData.patientLocation,
            route = null,
            onAllow = {},
            onDeny = {}
        )
    }
}
