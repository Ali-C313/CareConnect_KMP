package com.example.careconnect.domain.model

import com.example.careconnect.i18n.tr

// Status einer Verordnung
enum class PrescriptionStatus(val value: String, private val label: String) {
    OFFEN("offen", "Offen"),
    ANGENOMMEN("angenommen", "Übernommen"),
    ABGELEHNT("abgelehnt", "Abgelehnt"),
    ERLEDIGT("erledigt", "Erledigt");

    // Text in der gewählten Sprache
    val displayName: String
        get() = tr(label)

    companion object {
        fun fromValue(value: String?): PrescriptionStatus =
            entries.firstOrNull { it.value == value } ?: OFFEN
    }
}
