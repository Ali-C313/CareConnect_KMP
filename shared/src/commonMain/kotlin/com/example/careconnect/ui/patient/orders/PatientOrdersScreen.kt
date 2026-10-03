package com.example.careconnect.ui.patient.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.MapMarker
import com.example.careconnect.domain.model.Order
import com.example.careconnect.ui.components.CareMap
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.StatusGreen
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.openSafely
import com.example.careconnect.util.toDateTimeString
import com.example.careconnect.util.toKmString
import com.example.careconnect.util.toMinutesString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PatientOrdersScreen(
    viewModel: PatientOrdersViewModel = koinViewModel()
) {
    val activeOrder by viewModel.activeOrder.collectAsState()
    val openOrders by viewModel.openOrders.collectAsState()
    val historyOrders by viewModel.historyOrders.collectAsState()
    val employee by viewModel.employee.collectAsState()
    val employeeCoordinate by viewModel.employeeCoordinate.collectAsState()
    val homeCoordinate by viewModel.homeCoordinate.collectAsState()
    val route by viewModel.route.collectAsState()
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = "Live Notruf")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Aktiver Auftrag mit Live-Karte
            SectionCard(title = "Aktiver Auftrag", icon = Icons.Default.MyLocation) {
                val active = activeOrder
                if (active == null) {
                    Text(
                        "Kein aktiver Auftrag verfügbar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val markers = buildList {
                        employeeCoordinate?.let { add(MapMarker("Helfer", it, isEmployee = true)) }
                        homeCoordinate?.let { add(MapMarker("Ziel", it)) }
                    }
                    CareMap(
                        markers = markers,
                        height = 260.dp,
                        emptyText = "Standort des Mitarbeiters nicht verfügbar"
                    )

                    route?.let {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            InfoRow(Icons.Default.Straighten, "Entfernung", it.distanceMeters.toKmString(), Modifier.weight(1f))
                            InfoRow(Icons.Default.DirectionsCar, "ETA", it.durationSeconds.toMinutesString(), Modifier.weight(1f))
                        }
                    }

                    Text(active.description, fontWeight = FontWeight.SemiBold)
                    InfoRow(Icons.Default.Person, "Mitarbeiter", employee?.name ?: "Unbekannt")
                    // In Firestore gibt es keine Mitarbeiter-Telefonnummer, deshalb E-Mail als Kontakt
                    employee?.email?.takeIf { it.isNotEmpty() }?.let { email ->
                        InfoButtonRow(
                            icon = Icons.Default.Phone,
                            title = "Kontakt",
                            value = email,
                            actionIcon = Icons.Default.Call,
                            actionDescription = "Kontaktieren",
                            onClick = { uriHandler.openSafely("mailto:$email") }
                        )
                    }
                }
            }

            OrderSection(
                title = "Offene Aufträge",
                emptyText = "Keine offenen Aufträge.",
                orders = openOrders,
                iconTint = StatusOrange,
                icon = Icons.Default.Schedule
            )

            OrderSection(
                title = "Erledigte Aufträge",
                emptyText = "Keine erledigten Aufträge.",
                orders = historyOrders,
                iconTint = StatusGreen,
                icon = Icons.Default.CheckCircle
            )
        }
    }
}

@Composable
private fun OrderSection(
    title: String,
    emptyText: String,
    orders: List<Order>,
    iconTint: Color,
    icon: ImageVector
) {
    SectionCard(title = title, icon = icon, iconTint = iconTint) {
        if (orders.isEmpty()) {
            Text(emptyText, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            // Horizontal scrollbar wie in iOS
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                orders.forEach { order ->
                    PatientOrderCard(order)
                }
            }
        }
    }
}

@Composable
private fun PatientOrderCard(order: Order) {
    Column(
        modifier = Modifier
            .width(260.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = order.description.ifEmpty { "Auftrag" },
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = order.state.displayName,
            color = order.state.color,
            style = MaterialTheme.typography.bodyMedium
        )
        (order.dueDate ?: order.createdAt)?.let {
            Text(
                text = "Datum: ${it.toDateTimeString()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun PatientOrdersScreenPreview() {
    PatientOrdersScreen()
}
