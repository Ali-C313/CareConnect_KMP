package com.example.careconnect.ui.patient.orders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.openSafely

// Zuständiger Mitarbeiter mit Kontakt
@Composable
fun EmployeeSection(employee: Employee?) {
    val uriHandler = LocalUriHandler.current
    SectionCard(title = tr("Mitarbeiter"), icon = Icons.Default.Person) {
        InfoRow(Icons.Default.Person, tr("Mitarbeiter"), employee?.name?.ifBlank { null } ?: tr("Unbekannt"))
        // In Firestore gibt es keine Telefonnummer, deshalb E-Mail als Kontakt
        employee?.email?.takeIf { it.isNotEmpty() }?.let { email ->
            InfoButtonRow(
                icon = Icons.Default.Phone,
                title = tr("Kontakt"),
                value = email,
                actionIcon = Icons.Default.Call,
                actionDescription = tr("Kontaktieren"),
                onClick = { uriHandler.openSafely("mailto:$email") }
            )
        }
    }
}

@Preview
@Composable
fun EmployeeSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        EmployeeSection(employee = MockData.employee)
    }
}
