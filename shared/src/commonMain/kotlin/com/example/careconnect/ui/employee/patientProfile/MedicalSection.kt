package com.example.careconnect.ui.employee.patientProfile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.StatusPurple
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.util.toAge
import com.example.careconnect.util.toDateString
import com.example.careconnect.util.toDateTimeString

@Composable
fun MedicalSection(
    patient: Patient,
    medicines: List<MedicineEntry>,
    appointments: List<Appointment>
) {
    if (medicines.isNotEmpty()) {
        SectionCard(title = tr("Medikamente"), icon = Icons.Default.Medication, iconTint = StatusPurple) {
            medicines.forEach { med ->
                Text("• ${med.name} – ${med.dosage} (${med.timeOfDay.displayName})")
            }
        }
    } else if (patient.medications.isNotEmpty()) {
        // Alte Einträge aus der iOS-App
        SectionCard(title = tr("Medikamente"), icon = Icons.Default.Medication, iconTint = StatusPurple) {
            patient.medications.forEach { med ->
                Text("• ${med.name}")
            }
        }
    }

    if (appointments.isNotEmpty()) {
        SectionCard(title = tr("Nächste Termine"), icon = Icons.Default.CalendarMonth) {
            appointments.take(3).forEach { appointment ->
                Text("• ${appointment.date.toDateTimeString()} – ${appointment.what}")
            }
        }
    }

    if (patient.allergies.isNotBlank()) {
        SectionCard(title = tr("Allergien"), icon = Icons.Default.Warning, iconTint = StatusOrange) {
            Text(patient.allergies)
        }
    }

    if (patient.medicalHistory.isNotBlank()) {
        SectionCard(title = tr("Medizinische Vorgeschichte"), icon = Icons.Default.Favorite, iconTint = StatusRed) {
            Text(patient.medicalHistory)
        }
    }

    if (patient.history.isNotEmpty()) {
        SectionCard(title = tr("Historie"), icon = Icons.Default.History) {
            patient.history.forEach { entry ->
                Text("• $entry")
            }
        }
    }

    patient.birthDate?.let { birthDate ->
        SectionCard(title = tr("Geburtsdatum"), icon = Icons.Default.Cake) {
            InfoRow(Icons.Default.Cake, tr("Geboren am"), birthDate.toDateString())
            InfoRow(Icons.Default.Cake, tr("Alter"), tr("{0} Jahre", birthDate.toAge()))
        }
    }

    if (patient.notes.isNotBlank()) {
        SectionCard(title = tr("Notizen"), icon = Icons.AutoMirrored.Filled.Notes) {
            Text(patient.notes)
        }
    }
}

@Preview
@Composable
fun MedicalSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        MedicalSection(
            patient = MockData.patient,
            medicines = MockData.medicines,
            appointments = MockData.appointments
        )
    }
}
