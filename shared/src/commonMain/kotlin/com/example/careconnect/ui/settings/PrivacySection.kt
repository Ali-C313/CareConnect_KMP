package com.example.careconnect.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.platform.LocationTrackerMock
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.LocationRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.mock.repository.SettingsRepositoryMock
import com.example.careconnect.mock.repository.UserRepositoryMock
import com.example.careconnect.service.TrackingManager
import com.example.careconnect.session.OrgSession
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme

// Benachrichtigungen und Standort-Freigabe
@Composable
fun PrivacySection(
    viewModel: SettingsViewModel,
    isEmployee: Boolean
) {
    val notifications by viewModel.notificationsEnabled.collectAsState()
    val shareLocation by viewModel.shareLocation.collectAsState()
    val showLiveLocation by viewModel.showLiveLocation.collectAsState()

    SectionCard(title = tr("Benachrichtigungen & Standort")) {
        SwitchRow(
            icon = Icons.Default.Notifications,
            title = tr("Benachrichtigungen"),
            subtitle = if (isEmployee) tr("Neue Aufträge und Verordnungen, nur wenn du Verfügbar oder Beschäftigt bist") else tr("Wenn sich an deinen Aufträgen etwas ändert"),
            checked = notifications,
            onCheckedChange = viewModel::setNotificationsEnabled
        )
        if (isEmployee) {
            SwitchRow(
                icon = Icons.Default.LocationOn,
                title = tr("Standort bei Einsätzen teilen"),
                subtitle = tr("Der Patient sieht, wann du ankommst"),
                checked = shareLocation,
                onCheckedChange = viewModel::setShareLocation
            )
        } else {
            SwitchRow(
                icon = Icons.Default.LocationOn,
                title = tr("Live-Standort anzeigen"),
                subtitle = tr("Mitarbeiter auf der Karte sehen, wenn er unterwegs ist"),
                checked = showLiveLocation,
                onCheckedChange = viewModel::setShowLiveLocation
            )
        }
    }
}

@Preview
@Composable
fun PrivacySectionPreview() {
    CareConnectTheme(darkTheme = false) {
        PrivacySection(
            viewModel = SettingsViewModel(
                SettingsRepositoryMock(),
                AuthRepositoryMock(),
                TrackingManager(LocationTrackerMock(), LocationRepositoryMock()),
                UserRepositoryMock(),
                OrganizationRepositoryMock(),
                OrgSession(),
                EmployeeRepositoryMock(),
                PatientRepositoryMock()
            ),
            isEmployee = true
        )
    }
}
