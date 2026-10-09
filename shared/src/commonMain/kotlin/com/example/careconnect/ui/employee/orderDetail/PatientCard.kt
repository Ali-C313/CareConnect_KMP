package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.platform.mapsSearchUrl
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.StatusPurple
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.ui.theme.icon
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.openSafely

@Composable
fun PatientCard(patient: Patient) {
    val uriHandler = LocalUriHandler.current

    SectionCard(title = tr("Patientendaten"), icon = Icons.Default.Person) {
        Text(
            text = patient.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        if (patient.phone.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Phone,
                title = tr("Telefon"),
                value = patient.phone,
                actionIcon = Icons.Default.Call,
                actionDescription = tr("Anrufen"),
                onClick = { uriHandler.openSafely("tel:${StringHelper.formattedPhone(patient.phone)}") }
            )
        }
        if (patient.email.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Email,
                title = tr("E-Mail"),
                value = patient.email,
                actionIcon = Icons.AutoMirrored.Filled.Send,
                actionDescription = tr("E-Mail senden"),
                onClick = { uriHandler.openSafely("mailto:${patient.email}") }
            )
        }
        if (patient.fullAddress.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Map,
                title = tr("Adresse"),
                value = patient.fullAddress,
                actionIcon = Icons.Default.Navigation,
                actionDescription = tr("In Karten öffnen"),
                onClick = { uriHandler.openSafely(mapsSearchUrl(patient.fullAddress)) }
            )
        }
        if (patient.medications.isNotEmpty()) {
            SmallSection(Icons.Default.Medication, StatusPurple, tr("Medikamente"), patient.medications.joinToString("\n") { "• ${it.name}" })
        }
        if (patient.allergies.isNotBlank()) {
            SmallSection(Icons.Default.Warning, StatusOrange, tr("Allergien"), patient.allergies)
        }
        if (patient.medicalHistory.isNotBlank()) {
            SmallSection(Icons.Default.Warning, StatusRed, tr("Medizinische Vorgeschichte"), patient.medicalHistory)
        }
        if (patient.history.isNotEmpty()) {
            SmallSection(Icons.Default.Schedule, MaterialTheme.colorScheme.onSurfaceVariant, tr("Historie"), patient.history.joinToString("\n") { "• $it" })
        }
    }
}

@Preview
@Composable
fun PatientCardPreview() {
    CareConnectTheme(darkTheme = false) {
        PatientCard(patient = MockData.patient)
    }
}
