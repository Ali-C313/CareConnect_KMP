package com.example.careconnect.ui.employee.prescriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.mock.repository.OrderRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.mock.repository.PrescriptionRepositoryMock
import com.example.careconnect.session.OrgSession
import com.example.careconnect.ui.components.CareTextField
import com.example.careconnect.ui.components.DateTimePickerField
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.StatusRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPrescriptionSheet(
    viewModel: PrescriptionsViewModel,
    onDismiss: () -> Unit
) {
    val form by viewModel.form.collectAsState()
    val patients by viewModel.patients.collectAsState()
    val selectedPatient = patients.firstOrNull { it.id == form.patientId }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(tr("Neue Verordnung"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Picker(
                label = tr("Patient"),
                icon = Icons.Default.Person,
                selected = selectedPatient?.name,
                options = patients.map { it.id to it.name },
                onSelect = viewModel::onPatientSelect
            )

            if (selectedPatient != null) {
                if (selectedPatient.linkedOrgs.isEmpty()) {
                    Text(
                        tr("{0} hat noch keinen Pflegedienst freigegeben. ", selectedPatient.name) +
                            tr("Das geht in der App des Patienten unter Einstellungen → Freigaben."),
                        color = StatusOrange,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Picker(
                        label = tr("Pflegedienst"),
                        icon = Icons.Default.Business,
                        selected = selectedPatient.linkedOrgs.firstOrNull { it.orgId == form.toOrgId }?.orgName,
                        options = selectedPatient.linkedOrgs.map { it.orgId to it.orgName },
                        onSelect = viewModel::onTargetSelect
                    )
                }
            }

            CareTextField(
                label = tr("Was? (z.B. Verbandswechsel)"),
                value = form.title,
                onValueChange = viewModel::onTitleChange
            )
            CareTextField(
                label = tr("Wie oft? (z.B. 3x pro Woche)"),
                value = form.frequency,
                onValueChange = viewModel::onFrequencyChange,
                icon = Icons.Default.Repeat
            )
            DateTimePickerField(
                label = tr("Ab wann"),
                millis = form.startDate,
                onValueChange = viewModel::onStartDateChange,
                withTime = false
            )
            OutlinedTextField(
                value = form.details,
                onValueChange = viewModel::onDetailsChange,
                label = { Text(tr("Hinweise für die Pflege")) },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            form.errorMessage?.let { Text(it, color = StatusRed) }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(tr("Abbrechen")) }
                Button(
                    onClick = { viewModel.create(onSuccess = onDismiss) },
                    enabled = form.canSave,
                    modifier = Modifier.weight(1f)
                ) {
                    if (form.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text(tr("Senden"))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NewPrescriptionSheetPreview() {
    CareConnectTheme(darkTheme = false) {
        NewPrescriptionSheet(
            viewModel = PrescriptionsViewModel(
                PrescriptionRepositoryMock(),
                OrderRepositoryMock(),
                AuthRepositoryMock(),
                PatientRepositoryMock(),
                EmployeeRepositoryMock(),
                OrganizationRepositoryMock(),
                OrgSession().apply { set(MockData.organization.id) }
            ),
            onDismiss = {}
        )
    }
}
