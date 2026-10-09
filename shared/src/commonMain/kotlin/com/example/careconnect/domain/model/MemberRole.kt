package com.example.careconnect.domain.model

import com.example.careconnect.i18n.tr

enum class MemberRole(val value: String, private val label: String) {
    ADMIN("admin", "Leitung"),
    STAFF("staff", "Mitarbeiter"),
    PATIENT("patient", "Patient");

    // Text in der gewählten Sprache
    val displayName: String
        get() = tr(label)

    // Leitung und Mitarbeiter sehen die Mitarbeiter-App
    val userRole: UserRole
        get() = if (this == PATIENT) UserRole.PATIENT else UserRole.EMPLOYEE

    companion object {
        fun fromValue(value: String?): MemberRole =
            entries.firstOrNull { it.value == value } ?: PATIENT
    }
}
