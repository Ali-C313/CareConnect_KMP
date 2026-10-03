package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.LoadingView
import com.example.careconnect.ui.employee.orders.NewOrderSheet
import com.example.careconnect.ui.theme.StatusRed
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderDetailScreen(
    orderId: String,
    onBack: () -> Unit,
    viewModel: OrderDetailViewModel = koinViewModel()
) {
    val order by viewModel.order.collectAsState()
    val patient by viewModel.patient.collectAsState()
    val assignedEmployee by viewModel.assignedEmployee.collectAsState()
    val employeeCoordinate by viewModel.employeeCoordinate.collectAsState()
    val patientCoordinate by viewModel.patientCoordinate.collectAsState()
    val route by viewModel.route.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showCompleteDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }

    LaunchedEffect(orderId) {
        viewModel.load(orderId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.fillMaxSize()) {
            CareTopBar(title = "Auftragsdetails", onBack = onBack)

            val current = order
            if (current == null) {
                LoadingView("Lade Auftrag …")
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HeaderCard(order = current, patient = patient)
                    StatusCard(order = current, employeeName = assignedEmployee?.name)

                    if (current.state == OrderState.AKTUELL) {
                        LiveMapCard(
                            employeeCoordinate = employeeCoordinate,
                            patientCoordinate = patientCoordinate,
                            route = route
                        )
                    }

                    patient?.let { PatientCard(it) }

                    ActionCard(
                        state = current.state,
                        onEdit = { showEditSheet = true },
                        onAccept = viewModel::accept,
                        onComplete = { showCompleteDialog = true },
                        onDelete = { showDeleteDialog = true }
                    )
                }
            }
        }

        // Lade-Overlay während einer Aktion
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            title = { Text("Auftrag abschließen?") },
            text = { Text("Dieser Auftrag wird als erledigt markiert.") },
            confirmButton = {
                TextButton(onClick = {
                    showCompleteDialog = false
                    viewModel.complete()
                }) { Text("Bestätigen") }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) { Text("Abbrechen") }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Auftrag löschen?") },
            text = { Text("Der Auftrag wird dauerhaft gelöscht.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete(onDeleted = onBack)
                }) { Text("Löschen", color = StatusRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Abbrechen") }
            }
        )
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text("Fehler") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissError) { Text("OK") }
            }
        )
    }

    if (showEditSheet) {
        NewOrderSheet(
            existingOrder = order,
            onDismiss = { showEditSheet = false }
        )
    }
}
