package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.MapMarker
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.model.RouteInfo
import com.example.careconnect.platform.mapsDirectionsUrl
import com.example.careconnect.platform.mapsSearchUrl
import com.example.careconnect.ui.components.CareMap
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.StatusBlue
import com.example.careconnect.ui.theme.StatusGreen
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.StatusPurple
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.ui.theme.color
import com.example.careconnect.ui.theme.icon
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.openSafely
import com.example.careconnect.util.toAge
import com.example.careconnect.util.toDateString
import com.example.careconnect.util.toDateTimeString
import com.example.careconnect.util.toKmString
import com.example.careconnect.util.toMinutesString
import com.example.careconnect.util.toTimeString
import com.example.careconnect.util.currentTimeMillis

// Karten für den Auftragsdetail-Screen (wie OrderDetailComponents in iOS)

@Composable
fun HeaderCard(
    order: Order,
    patient: Patient?
) {
    SectionCard {
        Text(
            text = "Patientenauftrag",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = order.patientName.ifEmpty { "Unbekannter Patient" },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (order.description.isNotEmpty()) {
            Text(
                text = order.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        patient?.birthDate?.let { birthDate ->
            Text(
                text = "Geboren: ${birthDate.toDateString()} • ${birthDate.toAge()} Jahre",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (order.notes.isNotEmpty()) {
            InfoRow(Icons.Default.EditNote, "Bemerkungen", order.notes.joinToString(", "))
        }
    }
}

@Composable
fun StatusCard(
    order: Order,
    employeeName: String?
) {
    SectionCard(title = "Status", icon = Icons.Default.Schedule) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(order.state.icon, contentDescription = null, tint = order.state.color)
            Text(
                text = order.state.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = order.state.color
            )
        }
        order.createdAt?.let {
            InfoRow(Icons.Default.Schedule, "Erstellt am", it.toDateTimeString())
        }
        order.dueDate?.let {
            InfoRow(Icons.Default.CalendarMonth, "Fällig am", it.toDateTimeString())
        }
        if (employeeName != null) {
            InfoRow(Icons.Default.Person, "Mitarbeiter", employeeName)
        }
    }
}

/**
 * Live-Karte mit Mitarbeiter (blau) und Patient (rot), dazu ETA.
 */
@Composable
fun LiveMapCard(
    employeeCoordinate: Coordinate?,
    patientCoordinate: Coordinate?,
    route: RouteInfo?
) {
    val uriHandler = LocalUriHandler.current
    val markers = buildList {
        patientCoordinate?.let { add(MapMarker("Patient", it)) }
        employeeCoordinate?.let { add(MapMarker("Mitarbeiter", it, isEmployee = true)) }
    }

    SectionCard(title = "Live Karte", icon = Icons.Default.Map) {
        CareMap(markers = markers)

        route?.let {
            val arrival = currentTimeMillis() + (it.durationSeconds * 1000).toLong()
            InfoRow(
                Icons.Default.DirectionsCar,
                "Voraussichtliche Ankunft",
                "${arrival.toTimeString()} Uhr (${it.durationSeconds.toMinutesString()}, ${it.distanceMeters.toKmString()})"
            )
        }

        patientCoordinate?.let { target ->
            Button(
                onClick = { uriHandler.openSafely(mapsDirectionsUrl(target.latitude, target.longitude)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null)
                Text("  Navigation öffnen")
            }
        }
    }
}

@Composable
fun PatientCard(patient: Patient) {
    val uriHandler = LocalUriHandler.current

    SectionCard(title = "Patientendaten", icon = Icons.Default.Person) {
        Text(
            text = patient.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        if (patient.phone.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Phone,
                title = "Telefon",
                value = patient.phone,
                actionIcon = Icons.Default.Call,
                actionDescription = "Anrufen",
                onClick = { uriHandler.openSafely("tel:${StringHelper.formattedPhone(patient.phone)}") }
            )
        }
        if (patient.email.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Email,
                title = "E-Mail",
                value = patient.email,
                actionIcon = Icons.AutoMirrored.Filled.Send,
                actionDescription = "E-Mail senden",
                onClick = { uriHandler.openSafely("mailto:${patient.email}") }
            )
        }
        if (patient.fullAddress.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Map,
                title = "Adresse",
                value = patient.fullAddress,
                actionIcon = Icons.Default.Navigation,
                actionDescription = "In Karten öffnen",
                onClick = { uriHandler.openSafely(mapsSearchUrl(patient.fullAddress)) }
            )
        }
        if (patient.medications.isNotEmpty()) {
            SmallSection(Icons.Default.Medication, StatusPurple, "Medikamente", patient.medications.joinToString("\n") { "• ${it.name}" })
        }
        if (patient.allergies.isNotBlank()) {
            SmallSection(Icons.Default.Warning, StatusOrange, "Allergien", patient.allergies)
        }
        if (patient.medicalHistory.isNotBlank()) {
            SmallSection(Icons.Default.Warning, StatusRed, "Medizinische Vorgeschichte", patient.medicalHistory)
        }
        if (patient.history.isNotEmpty()) {
            SmallSection(Icons.Default.Schedule, MaterialTheme.colorScheme.onSurfaceVariant, "Historie", patient.history.joinToString("\n") { "• $it" })
        }
    }
}

@Composable
private fun SmallSection(
    icon: ImageVector,
    tint: Color,
    title: String,
    text: String
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, contentDescription = null, tint = tint)
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * Aktionen passend zum Status: annehmen, abschließen, bearbeiten, löschen.
 */
@Composable
fun ActionCard(
    state: OrderState,
    onEdit: () -> Unit,
    onAccept: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    SectionCard(title = "Aktionen", icon = Icons.Default.Edit) {
        ActionButton("Bearbeiten", Icons.Default.Edit, MaterialTheme.colorScheme.primary, onEdit)

        when (state) {
            OrderState.OFFEN -> ActionButton("Annehmen", Icons.Default.CheckCircle, StatusBlue, onAccept)
            OrderState.AKTUELL -> ActionButton("Abschließen", Icons.Default.Verified, StatusGreen, onComplete)
            OrderState.ERLEDIGT -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen)
                Text(
                    "  Auftrag abgeschlossen",
                    color = StatusGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        ActionButton("Löschen", Icons.Default.Delete, StatusRed, onDelete)
    }
}

@Composable
private fun ActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null)
        Text("  $title")
    }
}
