package com.example.careconnect.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.GeoRepositoryMock
import com.example.careconnect.mock.repository.StorageRepositoryMock
import com.example.careconnect.mock.repository.UserRepositoryMock
import com.example.careconnect.ui.components.AddressSearchField
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.DateTimePickerField
import com.example.careconnect.ui.components.ProfileImagePicker
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val imageBytes by viewModel.imageBytes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = tr("Registrieren"), onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = tr("Erstelle dein CareConnect Konto"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            SectionCard {
                ProfileImagePicker(
                    imageBytes = imageBytes,
                    imageUrl = "",
                    onImagePicked = viewModel::onImagePicked
                )
            }

            // Persönliche Daten
            SectionCard(title = tr("Persönliche Daten"), icon = Icons.Default.Person) {
                RegisterTextField(tr("Vorname"), uiState.firstName, viewModel::onFirstNameChange, Icons.Default.Person)
                RegisterTextField(tr("Nachname"), uiState.lastName, viewModel::onLastNameChange, Icons.Default.Person)

                DateTimePickerField(
                    label = tr("Geburtsdatum"),
                    millis = uiState.birthDate,
                    onValueChange = viewModel::onBirthDateChange,
                    withTime = false
                )
            }

            // Adresse brauchen vor allem Patienten (Hausbesuche, Karte)
            SectionCard(title = tr("Adresse & Kontakt"), icon = Icons.Default.Apartment) {
                AddressSearchField(
                    value = uiState.address,
                    onValueChange = viewModel::onAddressChange,
                    suggestions = uiState.addressSuggestions,
                    onSuggestionClick = viewModel::onSuggestionClick
                )
                RegisterTextField(tr("Stadt"), uiState.city, viewModel::onCityChange, Icons.Default.Apartment)
                RegisterTextField(
                    tr("Telefonnummer"),
                    uiState.phone,
                    viewModel::onPhoneChange,
                    Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone
                )
            }

            // Zugangsdaten
            SectionCard(title = tr("Zugangsdaten"), icon = Icons.Default.Lock) {
                RegisterTextField(
                    tr("E-Mail-Adresse"),
                    uiState.email,
                    viewModel::onEmailChange,
                    Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                RegisterTextField(
                    tr("Passwort"),
                    uiState.password,
                    viewModel::onPasswordChange,
                    Icons.Default.Lock,
                    isPassword = true
                )
                RegisterTextField(
                    tr("Passwort bestätigen"),
                    uiState.confirmPassword,
                    viewModel::onConfirmPasswordChange,
                    Icons.Default.Lock,
                    isPassword = true
                )
            }

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = viewModel::register,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(tr("Konto erstellen"), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        RegisterScreen(
            onBack = {},
            viewModel = RegisterViewModel(
                AuthRepositoryMock(),
                UserRepositoryMock(),
                StorageRepositoryMock(),
                GeoRepositoryMock()
            )
        )
    }
}
