package com.example.careconnect.ui.employee.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.unit.dp
import com.example.careconnect.ui.components.CareTextField
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.ChangePasswordCard
import com.example.careconnect.ui.components.ProfileImagePicker
import com.example.careconnect.ui.components.SectionCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditEmployeeProfileScreen(
    onBack: () -> Unit,
    viewModel: EditEmployeeProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val imageBytes by viewModel.imageBytes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(
            title = "Profil bearbeiten",
            onBack = onBack,
            actions = {
                if (uiState.isSaving) {
                    CircularProgressIndicator(Modifier.padding(end = 12.dp).size(24.dp))
                } else {
                    TextButton(onClick = { viewModel.save(onSuccess = onBack) }) {
                        Text("Speichern")
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard {
                ProfileImagePicker(
                    imageBytes = imageBytes,
                    imageUrl = uiState.profileImageUrl,
                    onImagePicked = viewModel::onImagePicked
                )
            }

            SectionCard(title = "Allgemein", icon = Icons.Default.Badge) {
                CareTextField("Vorname", uiState.firstName, viewModel::onFirstNameChange, icon = Icons.Default.Person)
                CareTextField("Nachname", uiState.lastName, viewModel::onLastNameChange, icon = Icons.Default.Person)
                CareTextField("Rolle", uiState.role, viewModel::onRoleChange, icon = Icons.Default.Work)
                CareTextField(
                    "E-Mail",
                    uiState.email,
                    viewModel::onEmailChange,
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
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

    uiState.message?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            title = { Text("Hinweis") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text("OK") } }
        )
    }
}
