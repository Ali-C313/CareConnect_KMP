package com.example.careconnect.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.i18n.tr
import com.example.careconnect.ui.theme.CareConnectTheme

// Mitarbeiter bestätigt, dass sein Standort während des Einsatzes geteilt wird
@Composable
fun LocationConsentDialog(
    onAllow: () -> Unit,
    onDeny: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
        title = { Text(tr("Standort teilen?")) },
        text = { Text(tr("Du hast einen Auftrag angenommen. Der Patient kann dann auf der Karte sehen, wo du bist und wann du ungefähr ankommst. Geteilt wird nur, solange der Auftrag läuft. Du kannst das jederzeit in den Einstellungen ändern.")) },
        confirmButton = {
            TextButton(onClick = onAllow) { Text(tr("Teilen")) }
        },
        dismissButton = {
            TextButton(onClick = onDeny) { Text(tr("Nicht teilen")) }
        }
    )
}

@Preview
@Composable
fun LocationConsentDialogPreview() {
    CareConnectTheme(darkTheme = false) {
        LocationConsentDialog(onAllow = {}, onDeny = {})
    }
}
