package com.example.careconnect.ui.employee.patientProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.MapMarker
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AppointmentRepositoryMock
import com.example.careconnect.mock.repository.GeoRepositoryMock
import com.example.careconnect.mock.repository.MedicineRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.platform.mapsSearchUrl
import com.example.careconnect.ui.components.CareMap
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.LoadingView
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.openSafely
import org.koin.compose.viewmodel.koinViewModel

// Profil eines Patienten für Mitarbeiter
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    patientId: String,
    onBack: () -> Unit,
    viewModel: PatientProfileViewModel = koinViewModel()
) {
    val patient by viewModel.patient.collectAsState()
    val location by viewModel.location.collectAsState()
    val uriHandler = LocalUriHandler.current
    val medicines by viewModel.medicines.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
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
        CareTopBar(title = tr("Profil"), onBack = onBack)

        val current = patient
        if (current == null) {
            LoadingView(tr("Lade Patientendaten …"))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PatientProfileHeader(
                    patient = current,
                    onEmergencyClick = { showContactSheet = true }
                )
                ContactSection(current)
                MedicalSection(current, medicines, appointments)

                val state = location
                val markers = if (state is AddressLocation.Found) listOf(MapMarker(current.name, state.coordinate)) else emptyList()
                SectionCard(title = tr("Standort"), icon = Icons.Default.Map) {
                    CareMap(
                        markers = markers,
                        isLoading = state is AddressLocation.Loading,
                        emptyText = when {
                            current.fullAddress.isEmpty() -> tr("Keine Adresse angegeben")
                            state is AddressLocation.Loading -> tr("Adresse wird gesucht…")
                            else -> tr("Adresse nicht auf der Karte gefunden")
                        }
                    )
                    // Klappt auch, wenn die Karte die Adresse nicht findet
                    if (current.fullAddress.isNotEmpty()) {
                        TextButton(
                            onClick = { uriHandler.openSafely(mapsSearchUrl(current.fullAddress)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null)
                            Text(tr("  In Karten öffnen"))
                        }
                    }
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

@Preview
@Composable
fun PatientProfileScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        PatientProfileScreen(
            patientId = MockData.patient.id,
            onBack = {},
            viewModel = PatientProfileViewModel(
                PatientRepositoryMock(),
                GeoRepositoryMock(),
                MedicineRepositoryMock(),
                AppointmentRepositoryMock()
            ).apply { load(MockData.patient.id) }
        )
    }
}
