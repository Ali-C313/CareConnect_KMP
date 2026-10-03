package com.example.careconnect.ui.patient.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.platform.mapsSearchUrl
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.EmptyStateView
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.StatusBlue
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.util.openSafely
import com.example.careconnect.util.toDateTimeString
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    viewModel: AppointmentsViewModel = koinViewModel()
) {
    val appointments by viewModel.appointments.collectAsState()
    var showEditSheet by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(
            title = "Termine",
            actions = {
                IconButton(onClick = {
                    viewModel.startEditing(null)
                    showEditSheet = true
                }) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "Termin hinzufügen",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        if (appointments.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.EventBusy,
                title = "Keine Termine",
                message = "Füge mit dem + Button oben rechts einen Termin hinzu."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(appointments, key = { it.id }) { appointment ->
                    AppointmentRow(
                        appointment = appointment,
                        onClick = { selectedAppointment = appointment },
                        onEdit = {
                            viewModel.startEditing(appointment)
                            showEditSheet = true
                        },
                        onDelete = { viewModel.delete(appointment) }
                    )
                }
            }
        }
    }

    if (showEditSheet) {
        AddEditAppointmentSheet(
            viewModel = viewModel,
            onDismiss = { showEditSheet = false }
        )
    }

    selectedAppointment?.let { appointment ->
        ModalBottomSheet(onDismissRequest = { selectedAppointment = null }) {
            AppointmentDetails(appointment)
        }
    }
}

@Composable
private fun AppointmentRow(
    appointment: Appointment,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    SectionCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = StatusBlue)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(appointment.what, style = MaterialTheme.typography.titleMedium)
                Text(
                    appointment.date.toDateTimeString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Bearbeiten", tint = StatusBlue)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = StatusRed)
            }
        }
    }
}

@Composable
private fun AppointmentDetails(appointment: Appointment) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = appointment.what,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        InfoRow(Icons.Default.CalendarMonth, "Datum", appointment.date.toDateTimeString())

        if (appointment.address.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.LocationOn,
                title = "Adresse",
                value = appointment.address,
                actionIcon = Icons.Default.Map,
                actionDescription = "In Karten öffnen",
                onClick = { uriHandler.openSafely(mapsSearchUrl(appointment.address)) }
            )
        }

        if (appointment.bringItems.isNotEmpty()) {
            InfoRow(Icons.Default.ShoppingBag, "Mitbringen", appointment.bringItems.joinToString(", "))
        }

        if (appointment.notes.isNotEmpty()) {
            InfoRow(Icons.AutoMirrored.Filled.Notes, "Notizen", appointment.notes)
        } else {
            Text("Keine weiteren Informationen.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview
@Composable
fun AppointmentsScreenPreview() {
    AppointmentsScreen()
}
