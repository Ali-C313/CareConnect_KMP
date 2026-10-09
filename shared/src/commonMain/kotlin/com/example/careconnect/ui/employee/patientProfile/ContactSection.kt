package com.example.careconnect.ui.employee.patientProfile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.openSafely

@Composable
fun ContactSection(patient: Patient) {
    val uriHandler = LocalUriHandler.current

    SectionCard(title = tr("Kontakt")) {
        if (patient.phone.isNotEmpty()) {
            InfoButtonRow(
                icon = Icons.Default.Phone,
                title = tr("Telefon"),
                value = patient.phone,
                actionIcon = Icons.Default.Call,
                actionDescription = tr("Anrufen"),
                onClick = { uriHandler.openSafely("tel:${StringHelper.formattedPhone(patient.phone)}") }
            )
        } else {
            InfoRow(Icons.Default.Phone, tr("Telefon"), tr("Keine Telefonnummer angegeben"))
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
        } else {
            InfoRow(Icons.Default.Email, tr("E-Mail"), tr("Keine E-Mail-Adresse angegeben"))
        }
    }
}

@Preview
@Composable
fun ContactSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        ContactSection(patient = MockData.patient)
    }
}
