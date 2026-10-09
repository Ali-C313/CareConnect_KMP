package com.example.careconnect.ui.patient.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.mock.repository.PrescriptionRepositoryMock
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.compose.viewmodel.koinViewModel

/**
 * Freigaben: Welche Praxis darf an welchen Pflegedienst Verordnungen schicken.
 */
@Composable
fun SharingSection(
    viewModel: SharingViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val message by viewModel.message.collectAsState()

    SectionCard(title = tr("Freigaben"), icon = Icons.Default.Share) {
        when {
            state.careServices.isEmpty() || state.entries.isEmpty() -> Text(
                tr("Bist du bei einer Praxis und bei einem Pflegedienst, kannst du hier erlauben, ") +
                    tr("dass die Praxis Verordnungen direkt an deinen Pflegedienst schickt."),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            else -> {
                Text(
                    tr("Nur Verordnungen, keine anderen Daten. Du kannst das jederzeit wieder ausschalten."),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                state.entries.forEach { entry ->
                    Column {
                        Text(tr("{0} darf schicken an:", entry.source.orgName), fontWeight = FontWeight.SemiBold)
                        state.careServices.forEach { careService ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = careService.orgId in entry.linkedOrgIds,
                                    onCheckedChange = { viewModel.toggle(entry, careService) }
                                )
                                Text(careService.orgName)
                            }
                        }
                    }
                }
            }
        }
    }

    message?.let { text ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            title = { Text(tr("Freigaben")) },
            text = { Text(text) },
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text("OK") } }
        )
    }
}

@Preview
@Composable
fun SharingSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        SharingSection(
            SharingViewModel(
                PrescriptionRepositoryMock(),
                AuthRepositoryMock(MockData.patient.id),
                OrganizationRepositoryMock()
            )
        )
    }
}
