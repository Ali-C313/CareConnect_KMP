package com.example.careconnect.domain.model

// Werte sind die gleichen Strings wie in der iOS-App (Firestore-Feld "status")
enum class StaffStatus(val displayName: String) {
    AVAILABLE("Verfügbar"),
    BUSY("Beschäftigt"),
    PAUSE("Pause"),
    OFF("Frei"),
    VACATION("Urlaub");

    companion object {
        fun fromValue(value: String?): StaffStatus =
            entries.firstOrNull { it.displayName == value } ?: AVAILABLE
    }
}
