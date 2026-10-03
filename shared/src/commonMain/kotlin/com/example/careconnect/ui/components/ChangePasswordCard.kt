package com.example.careconnect.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChangePasswordCard(
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onChangePassword: () -> Unit
) {
    SectionCard(title = "Passwort ändern", icon = Icons.Default.Lock) {
        CareTextField("Aktuelles Passwort", currentPassword, onCurrentPasswordChange, isPassword = true)
        CareTextField("Neues Passwort", newPassword, onNewPasswordChange, isPassword = true)
        CareTextField("Neues Passwort bestätigen", confirmPassword, onConfirmPasswordChange, isPassword = true)

        Button(
            onClick = onChangePassword,
            enabled = !isLoading && newPassword.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Icon(Icons.Default.Key, contentDescription = null)
                Text("  Passwort ändern")
            }
        }
    }
}
