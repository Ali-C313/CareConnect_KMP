package com.example.careconnect.domain.model

import com.example.careconnect.i18n.tr

// value wird so in Firestore gespeichert und bleibt deutsch
enum class TimeOfDay(val value: String) {
    MORNING("Morgens"),
    NOON("Mittags"),
    EVENING("Abends");

    // Text in der gewählten Sprache
    val displayName: String
        get() = tr(value)

    companion object {
        fun fromValue(value: String?): TimeOfDay =
            entries.firstOrNull { it.value == value } ?: MORNING
    }
}
