package com.example.careconnect.util

import androidx.compose.ui.platform.UriHandler

// Ohne passende App (z.B. kein Mail-Programm) würde openUri abstürzen
fun UriHandler.openSafely(uri: String) {
    runCatching { openUri(uri) }
}
