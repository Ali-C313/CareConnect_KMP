package com.example.careconnect.domain.model

import com.example.careconnect.i18n.tr

// value = gleicher String wie in der iOS-App (Firestore-Feld "status"), bleibt deutsch
enum class StaffStatus(val value: String) {
    AVAILABLE("Verfügbar"),
    BUSY("Beschäftigt"),
    PAUSE("Pause"),
    OFF("Frei"),
    VACATION("Urlaub");

    // Text in der gewählten Sprache
    val displayName: String
        get() = tr(value)

    companion object {
        fun fromValue(value: String?): StaffStatus =
            entries.firstOrNull { it.value == value } ?: AVAILABLE
    }
}
