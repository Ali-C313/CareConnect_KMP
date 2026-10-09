package com.example.careconnect.ui.employee.employeeProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.EmployeeDocument
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.LoadingView
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.openSafely
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
            title = tr("Mitarbeiter"),
            onBack = onBack,
            actions = {
                IconButton(onClick = { pdfLauncher.launch() }) {
                    Icon(Icons.Default.UploadFile, contentDescription = tr("Dokument hinzufügen"))
                }
            }
        )

        val current = employee
        if (current == null) {
            LoadingView(tr("Lade …"))
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
                            text = tr("Dokumente"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        if (isUploading) {
                            CircularProgressIndicator(Modifier.size(24.dp))
                        } else {
                            Button(onClick = { pdfLauncher.launch() }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text(tr("Hinzufügen"))
                            }
                        }
                    }

                    if (documents.isEmpty()) {
                        Text(
                            tr("Noch keine Dokumente vorhanden."),
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
            title = { Text(tr("Dokument löschen?")) },
            text = { Text(document.fileName) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteDocument(document)
                    documentToDelete = null
                }) { Text(tr("Löschen"), color = StatusRed) }
            },
            dismissButton = {
                TextButton(onClick = { documentToDelete = null }) { Text(tr("Abbrechen")) }
            }
        )
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text(tr("Fehler")) },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = viewModel::dismissError) { Text("OK") } }
        )
    }
}

@Preview
@Composable
fun EmployeeProfileScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        EmployeeProfileScreen(
            employeeId = MockData.employee.id,
            onBack = {},
            viewModel = EmployeeProfileViewModel(EmployeeRepositoryMock()).apply { load(MockData.employee.id) }
        )
    }
}
