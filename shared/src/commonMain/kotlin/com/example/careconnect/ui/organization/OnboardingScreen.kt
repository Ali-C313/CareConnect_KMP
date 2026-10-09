package com.example.careconnect.ui.organization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.i18n.tr
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.OrganizationRepositoryMock
import com.example.careconnect.mock.repository.UserRepositoryMock
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed
import org.koin.compose.viewmodel.koinViewModel

// Nach der Registrierung: noch keiner Organisation zugeordnet
@Composable
fun OnboardingScreen(
    onLogout: () -> Unit,
    viewModel: OrganizationViewModel = koinViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = tr("Willkommen"))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = tr("Du bist noch keiner Organisation zugeordnet. Tritt mit einem Code bei oder gründe deine eigene."),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            OrganizationSetupContent(viewModel)

            TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = StatusRed)
                Text(tr("  Abmelden"), color = StatusRed)
            }
        }
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        OnboardingScreen(
            onLogout = {},
            viewModel = OrganizationViewModel(
                AuthRepositoryMock(),
                UserRepositoryMock(),
                OrganizationRepositoryMock()
            )
        )
    }
}
