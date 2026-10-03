package com.example.careconnect.ui.employee.patientProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.MapMarker
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.platform.mapsSearchUrl
import com.example.careconnect.ui.components.AvatarView
import com.example.careconnect.ui.components.CareMap
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.LoadingView
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.StatusPurple
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.openSafely
import com.example.careconnect.util.toAge
import com.example.careconnect.util.toDateString
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    patientId: String,
    onBack: () -> Unit,
    viewModel: PatientProfileViewModel = koinViewModel()
) {
    val patient by viewModel.patient.collectAsState()
    val coordinate by viewModel.coordinate.collectAsState()
    var showContactSheet by remember { mutableStateOf(false) }
    var showContactEditor by remember { mutableStateOf(false) }

    LaunchedEffect(patientId) {
        viewModel.load(patientId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = "Profil", onBack = onBack)

        val current = patient
        if (current == null) {
            LoadingView("Lade Patientendaten …")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileHeader(
                    patient = current,
                    onEmergencyClick = { showContactSheet = true }
                )
                ContactSection(current)
                MedicalSection(current)

                val markers = coordinate?.let { listOf(MapMarker(current.name, it)) } ?: emptyList()
                SectionCard(title = "Standort", icon = Icons.Default.Map) {
                    CareMap(
                        markers = markers,
                        emptyText = if (current.fullAddress.isEmpty()) "Keine Adresse angegeben" else "Adresse wird gesucht…"
                    )
                }
            }
        }
    }

    // Notfallkontakt anzeigen
    if (showContactSheet) {
        ModalBottomSheet(onDismissRequest = { showContactSheet = false }) {
            EmergencyContactDetails(
                contact = patient?.emergencyContact,
                onAddClick = {
                    viewModel.startEditingContact()
                    showContactSheet = false
                    showContactEditor = true
                }
            )
        }
    }

    // Notfallkontakt bearbeiten / hinzufügen
    if (showContactEditor) {
        ModalBottomSheet(onDismissRequest = { showContactEditor = false }) {
            EmergencyContactEditor(
                viewModel = viewModel,
                onDismiss = { showContactEditor = false }
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    patient: Patient,
    onEmergencyClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    SectionCard {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AvatarView(
                initials = StringHelper.initials(patient.name),
                imageUrl = patient.profileImageUrl,
                size = 64.dp
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = patient.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                // Adresse antippen -> Karten-App
                if (patient.fullAddress.isNotEmpty()) {
                    Row(
                        modifier = Modifier.clickable {
                            uriHandler.openSafely(mapsSearchUrl(patient.fullAddress))
                        },
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(patient.fullAddress, color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    Text(
                        "Keine Adresse angegeben",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            TextButton(onClick = onEmergencyClick) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ContactPhone, contentDescription = "Notfallkontakt anzeigen")
                    Text("Notfall", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun ContactSection(patient: Patient) {
    val uriHandler = LocalUriHandler.current

    SectionCard(title = "Kontakt") {
        if (patient.phone.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Phone,
                title = "Telefon",
                value = patient.phone,
                actionIcon = Icons.Default.Call,
                actionDescription = "Anrufen",
                onClick = { uriHandler.openSafely("tel:${StringHelper.formattedPhone(patient.phone)}") }
            )
        } else {
            InfoRow(Icons.Default.Phone, "Telefon", "Keine Telefonnummer angegeben")
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
        } else {
            InfoRow(Icons.Default.Email, "E-Mail", "Keine E-Mail-Adresse angegeben")
        }
    }
}

@Composable
private fun MedicalSection(patient: Patient) {
    if (patient.medications.isNotEmpty()) {
        SectionCard(title = "Medikamente", icon = Icons.Default.Medication, iconTint = StatusPurple) {
            patient.medications.forEach { med ->
                Text("• ${med.name}")
            }
        }
    }

    if (patient.allergies.isNotBlank()) {
        SectionCard(title = "Allergien", icon = Icons.Default.Warning, iconTint = StatusOrange) {
            Text(patient.allergies)
        }
    }

    if (patient.medicalHistory.isNotBlank()) {
        SectionCard(title = "Medizinische Vorgeschichte", icon = Icons.Default.Favorite, iconTint = StatusRed) {
            Text(patient.medicalHistory)
        }
    }

    if (patient.history.isNotEmpty()) {
        SectionCard(title = "Historie", icon = Icons.Default.History) {
            patient.history.forEach { entry ->
                Text("• $entry")
            }
        }
    }

    patient.birthDate?.let { birthDate ->
        SectionCard(title = "Geburtsdatum", icon = Icons.Default.Cake) {
            InfoRow(Icons.Default.Cake, "Geboren am", birthDate.toDateString())
            InfoRow(Icons.Default.Cake, "Alter", "${birthDate.toAge()} Jahre")
        }
    }

    if (patient.notes.isNotBlank()) {
        SectionCard(title = "Notizen", icon = Icons.AutoMirrored.Filled.Notes) {
            Text(patient.notes)
        }
    }
}
