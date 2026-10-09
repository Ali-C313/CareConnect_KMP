package com.example.careconnect.ui.patient.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.GeoRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.mock.repository.StorageRepositoryMock
import com.example.careconnect.ui.components.AddressSearchField
import com.example.careconnect.ui.components.CareTextField
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.ChangePasswordCard
import com.example.careconnect.ui.components.LoadingView
import com.example.careconnect.ui.components.ProfileImagePicker
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditPatientProfileScreen(
    onBack: () -> Unit,
    viewModel: EditPatientProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val imageBytes by viewModel.imageBytes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(
            title = tr("Profil bearbeiten"),
            onBack = onBack,
            actions = {
                if (uiState.isSaving) {
                    CircularProgressIndicator(Modifier.padding(end = 12.dp).size(24.dp))
                } else {
                    TextButton(onClick = { viewModel.save(onSuccess = onBack) }) {
                        Text(tr("Speichern"))
                    }
                }
            }
        )

        val patient = uiState.patient
        if (patient == null) {
            LoadingView(tr("Lade Profil …"))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SectionCard(title = tr("Profilbild")) {
                    ProfileImagePicker(
                        imageBytes = imageBytes,
                        imageUrl = patient.profileImageUrl,
                        onImagePicked = viewModel::onImagePicked
                    )
                }

                SectionCard(title = tr("Allgemein"), icon = Icons.Default.Badge) {
                    CareTextField(tr("Vorname"), patient.firstName, viewModel::onFirstNameChange, icon = Icons.Default.Person)
                    CareTextField(tr("Nachname"), patient.lastName, viewModel::onLastNameChange, icon = Icons.Default.Person)
                    AddressSearchField(
                        value = patient.address,
                        onValueChange = viewModel::onAddressChange,
                        suggestions = uiState.addressSuggestions,
                        onSuggestionClick = viewModel::onSuggestionClick
                    )
                    CareTextField(tr("Stadt"), patient.city, viewModel::onCityChange, icon = Icons.Default.Apartment)
                    CareTextField(
                        tr("E-Mail"),
                        patient.email,
                        viewModel::onEmailChange,
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )
                    CareTextField(
                        tr("Telefonnummer"),
                        patient.phone,
                        viewModel::onPhoneChange,
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )
                }

                SectionCard(title = tr("Medizinisch"), icon = Icons.Default.LocalHospital) {
                    CareTextField(
                        tr("Allergien"),
                        patient.allergies,
                        viewModel::onAllergiesChange,
                        icon = Icons.Default.Medication,
                        singleLine = false
                    )
                    CareTextField(
                        tr("Krankengeschichte"),
                        patient.medicalHistory,
                        viewModel::onMedicalHistoryChange,
                        icon = Icons.Default.Favorite,
                        singleLine = false
                    )
                }

                ChangePasswordCard(
                    currentPassword = uiState.currentPassword,
                    newPassword = uiState.newPassword,
                    confirmPassword = uiState.confirmPassword,
                    onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
                    onNewPasswordChange = viewModel::onNewPasswordChange,
                    onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                    isLoading = uiState.isChangingPassword,
                    onChangePassword = viewModel::changePassword
                )
            }
        }
    }

    uiState.message?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            title = { Text(tr("Hinweis")) },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text("OK") } }
        )
    }
}

@Preview
@Composable
fun EditPatientProfileScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        EditPatientProfileScreen(
            onBack = {},
            viewModel = EditPatientProfileViewModel(
                PatientRepositoryMock(),
                StorageRepositoryMock(),
                AuthRepositoryMock(MockData.patient.id),
                GeoRepositoryMock()
            )
        )
    }
}
