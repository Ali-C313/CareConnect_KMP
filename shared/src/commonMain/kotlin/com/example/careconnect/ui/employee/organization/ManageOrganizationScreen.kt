package com.example.careconnect.ui.employee.organization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.Member
import com.example.careconnect.domain.model.MemberRole
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.session.OrgSession
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageOrganizationScreen(
    onBack: () -> Unit,
    viewModel: ManageOrganizationViewModel = koinViewModel()
) {
    val organization by viewModel.organization.collectAsState()
    val isListed by viewModel.isListed.collectAsState()
    val requests by viewModel.joinRequests.collectAsState()
    val invites by viewModel.invites.collectAsState()
    val members by viewModel.members.collectAsState()
    val message by viewModel.message.collectAsState()
    var memberToRemove by remember { mutableStateOf<Member?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = organization?.name ?: tr("Organisation"), onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = tr("Sichtbarkeit"), icon = Icons.Default.Visibility) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(tr("In der Suche anzeigen"))
                        Text(
                            tr("Patienten können euch finden und anfragen."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = isListed, onCheckedChange = viewModel::setListed)
                }
            }

            SectionCard(title = tr("Beitrittsanfragen ({0})", requests.size), icon = Icons.Default.PersonAdd) {
                if (requests.isEmpty()) {
                    Text(tr("Keine offenen Anfragen."), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                requests.forEach { request ->
                    RequestRow(
                        request = request,
                        onAccept = { viewModel.accept(request) },
                        onDecline = { viewModel.decline(request) }
                    )
                }
            }

            SectionCard(title = tr("Einladungscodes"), icon = Icons.Default.Key) {
                Text(
                    tr("Gib die Codes an dein Team oder deine Patienten weiter. Lange drücken zum Kopieren."),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                invites.sortedBy { it.role.ordinal }.forEach { invite ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            SelectionContainer {
                                Text(
                                    invite.code,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                tr("für {0}", invite.role.displayName),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.deleteInvite(invite.code) }) {
                            Icon(Icons.Default.Delete, contentDescription = tr("Code löschen"), tint = StatusRed)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.createInvite(MemberRole.STAFF) },
                        modifier = Modifier.weight(1f)
                    ) { Text(tr("+ Mitarbeiter")) }
                    OutlinedButton(
                        onClick = { viewModel.createInvite(MemberRole.PATIENT) },
                        modifier = Modifier.weight(1f)
                    ) { Text(tr("+ Patient")) }
                }
            }

            SectionCard(title = tr("Mitglieder ({0})", members.size), icon = Icons.Default.Groups) {
                members.forEach { member ->
                    MemberRow(
                        member = member,
                        isSelf = member.uid == viewModel.currentUserId,
                        onRoleChange = { role -> viewModel.changeRole(member, role) },
                        onRemove = { memberToRemove = member }
                    )
                }
            }
        }
    }

    memberToRemove?.let { member ->
        AlertDialog(
            onDismissRequest = { memberToRemove = null },
            title = { Text(tr("{0} entfernen?", member.name)) },
            text = { Text(tr("Die Person hat danach keinen Zugriff mehr auf eure Daten. Ihre bisherigen Einträge bleiben erhalten.")) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.remove(member)
                    memberToRemove = null
                }) { Text(tr("Entfernen"), color = StatusRed) }
            },
            dismissButton = {
                TextButton(onClick = { memberToRemove = null }) { Text(tr("Abbrechen")) }
            }
        )
    }

    message?.let { text ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            title = { Text(tr("Organisation")) },
            text = { Text(text) },
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text("OK") } }
        )
    }
}

@Preview
@Composable
fun ManageOrganizationScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        ManageOrganizationScreen(
            onBack = {},
            viewModel = ManageOrganizationViewModel(
                OrganizationRepositoryMock(),
                AuthRepositoryMock(),
                OrgSession().apply { set(MockData.organization.id) }
            )
        )
    }
}
