package com.example.careconnect.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.i18n.AppLanguage
import com.example.careconnect.i18n.tr
import com.example.careconnect.ui.theme.CareConnectTheme

// Dialog zur Auswahl der Sprache
@Composable
fun LanguageDialog(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("Sprache wählen")) },
        text = {
            Column {
                AppLanguage.entries.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = option == selected, onClick = { onSelect(option) })
                        Text(option.nativeName)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(tr("Abbrechen")) }
        }
    )
}

@Preview
@Composable
fun LanguageDialogPreview() {
    CareConnectTheme(darkTheme = false) {
        LanguageDialog(selected = AppLanguage.GERMAN, onSelect = {}, onDismiss = {})
    }
}
