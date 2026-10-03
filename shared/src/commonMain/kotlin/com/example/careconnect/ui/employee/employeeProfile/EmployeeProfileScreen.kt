package com.example.careconnect.ui.employee.employeeProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.EmployeeDocument
import com.example.careconnect.ui.components.AvatarView
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.LoadingView
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.openSafely
import com.example.careconnect.util.toDateString
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EmployeeProfileScreen(
    employeeId: String,
    onBack: () -> Unit,
    viewModel: EmployeeProfileViewModel = koinViewModel()
) {
    val employee by viewModel.employee.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var documentToDelete by remember { mutableStateOf<EmployeeDocument?>(null) }

    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    // PDF auswählen (fileImporter in iOS)
    val pdfLauncher = rememberFilePickerLauncher(type = FileKitType.File("pdf")) { file ->
        if (file != null) {
            scope.launch {
                viewModel.uploadPdf(file.name, file.readBytes())
            }
        }
    }

    LaunchedEffect(employeeId) {
        viewModel.load(employeeId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(
            title = "Mitarbeiter",
            onBack = onBack,
            actions = {
                IconButton(onClick = { pdfLauncher.launch() }) {
                    Icon(Icons.Default.UploadFile, contentDescription = "Dokument hinzufügen")
                }
            }
        )

        val current = employee
        if (current == null) {
            LoadingView("Lade …")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileHeader(current)
                WorkScheduleCard(current)

                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Dokumente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        if (isUploading) {
                            CircularProgressIndicator(Modifier.size(24.dp))
                        } else {
                            Button(onClick = { pdfLauncher.launch() }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text("Hinzufügen")
                            }
                        }
                    }

                    if (documents.isEmpty()) {
                        Text(
                            "Noch keine Dokumente vorhanden.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        documents.forEach { document ->
                            DocumentRow(
                                document = document,
                                onOpen = { uriHandler.openSafely(document.downloadUrl) },
                                onDelete = { documentToDelete = document }
                            )
                        }
                    }
                }
            }
        }
    }

    documentToDelete?.let { document ->
        AlertDialog(
            onDismissRequest = { documentToDelete = null },
            title = { Text("Dokument löschen?") },
            text = { Text(document.fileName) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteDocument(document)
                    documentToDelete = null
                }) { Text("Löschen", color = StatusRed) }
            },
            dismissButton = {
                TextButton(onClick = { documentToDelete = null }) { Text("Abbrechen") }
            }
        )
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text("Fehler") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = viewModel::dismissError) { Text("OK") } }
        )
    }
}

@Composable
private fun ProfileHeader(employee: Employee) {
    SectionCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AvatarView(
                initials = StringHelper.initials(employee.name),
                imageUrl = employee.profileImageUrl,
                size = 72.dp
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = employee.name.ifEmpty { "—" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(employee.role.ifEmpty { "—" }, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    employee.email.ifEmpty { "—" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(employee.status.color)
                    )
                    Text(
                        employee.status.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = employee.status.color
                    )
                }
                employee.birthDate?.let {
                    Text(
                        "Geboren: ${it.toDateString()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkScheduleCard(employee: Employee) {
    val schedule = employee.workSchedule
    SectionCard(title = "Arbeitszeiten", icon = Icons.Default.Schedule) {
        InfoRow(Icons.Default.Schedule, "Wochenstunden", "${schedule.weeklyHours} Std/Woche")
        InfoRow(Icons.Default.Schedule, "Schicht", "${schedule.shiftStart} – ${schedule.shiftEnd}")
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            schedule.workDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun DocumentRow(
    document: EmployeeDocument,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onOpen)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Default.PictureAsPdf,
            contentDescription = null,
            tint = StatusRed,
            modifier = Modifier.size(36.dp)
        )
        Column(Modifier.weight(1f)) {
            Text(document.fileName, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "Tippen zum Anzeigen",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = StatusRed)
        }
    }
}
