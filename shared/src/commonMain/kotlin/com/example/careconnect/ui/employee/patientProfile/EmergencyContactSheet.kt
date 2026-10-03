package com.example.careconnect.ui.employee.patientProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.EmergencyContact
import com.example.careconnect.platform.mapsSearchUrl
import com.example.careconnect.ui.components.AddressSearchField
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.openSafely

/**
 * Inhalt des Sheets "Notfallkontakt". Ohne Kontakt gibt es einen Button zum Hinzufügen.
 */
@Composable
fun EmergencyContactDetails(
    contact: EmergencyContact?,
    onAddClick: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Notfallkontakt",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (contact == null || contact.name.isBlank()) {
            Text("Kein Notfallkontakt vorhanden.")
            Button(onClick = onAddClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
                Text("  Notfallkontakt hinzufügen")
            }
        } else {
            SectionCard {
                InfoRow(Icons.Default.Person, "Name", contact.name)
                if (contact.relation.isNotEmpty()) {
                    InfoRow(Icons.Default.Favorite, "Beziehung", contact.relation)
                }
                contact.age?.let { InfoRow(Icons.Default.Cake, "Alter", "$it") }

                if (contact.phone.isNotEmpty()) {
                    InfoButtonRow(
                        icon = Icons.Default.Phone,
                        title = "Telefon",
                        value = contact.phone,
                        actionIcon = Icons.Default.Call,
                        actionDescription = "Anrufen",
                        onClick = { uriHandler.openSafely("tel:${StringHelper.formattedPhone(contact.phone)}") }
                    )
                } else {
                    InfoRow(Icons.Default.Phone, "Telefon", "Keine Telefonnummer angegeben")
                }

                if (contact.email.isNotEmpty()) {
                    InfoButtonRow(
                        icon = Icons.Default.Email,
                        title = "E-Mail",
                        value = contact.email,
                        actionIcon = Icons.AutoMirrored.Filled.Send,
                        actionDescription = "E-Mail senden",
                        onClick = { uriHandler.openSafely("mailto:${contact.email}") }
                    )
                } else {
                    InfoRow(Icons.Default.Email, "E-Mail", "Keine E-Mail-Adresse angegeben")
                }

                if (contact.address.isNotEmpty()) {
                    InfoButtonRow(
                        icon = Icons.Default.LocationOn,
                        title = "Adresse",
                        value = contact.address,
                        actionIcon = Icons.Default.Map,
                        actionDescription = "In Karten öffnen",
                        onClick = { uriHandler.openSafely(mapsSearchUrl(contact.address)) }
                    )
                } else {
                    InfoRow(Icons.Default.LocationOn, "Adresse", "Keine Adresse angegeben")
                }
            }
            OutlinedButton(onClick = onAddClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Text("  Bearbeiten")
            }
        }
    }
}

/**
 * Formular für den Notfallkontakt (entspricht EmergencyContactEditorView).
 */
@Composable
fun EmergencyContactEditor(
    viewModel: PatientProfileViewModel,
    onDismiss: () -> Unit
) {
    val contact by viewModel.editContact.collectAsState()
    val suggestions by viewModel.addressSuggestions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Notfallkontakt hinzufügen",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        ContactTextField("Name", contact.name) {
            viewModel.onContactChange(contact.copy(name = it))
        }
        ContactTextField("Beziehung (z. B. Ehepartner)", contact.relation) {
            viewModel.onContactChange(contact.copy(relation = it))
        }
        ContactTextField("Alter", contact.age?.toString() ?: "", KeyboardType.Number) {
            viewModel.onContactChange(contact.copy(age = it.filter { c -> c.isDigit() }.toIntOrNull()))
        }
        ContactTextField("Telefonnummer", contact.phone, KeyboardType.Phone) {
            viewModel.onContactChange(contact.copy(phone = it))
        }
        ContactTextField("E-Mail-Adresse", contact.email, KeyboardType.Email) {
            viewModel.onContactChange(contact.copy(email = it))
        }
        AddressSearchField(
            value = contact.address,
            onValueChange = viewModel::onContactAddressChange,
            suggestions = suggestions,
            onSuggestionClick = viewModel::onContactSuggestionClick
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Abbrechen")
            }
            Button(
                onClick = { viewModel.saveEmergencyContact(onDone = onDismiss) },
                enabled = contact.name.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Speichern")
            }
        }
    }
}

@Composable
private fun ContactTextField(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
