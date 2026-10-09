package com.example.careconnect.domain.model

import com.example.careconnect.i18n.tr

enum class UserRole(private val label: String) {
    EMPLOYEE("Mitarbeiter"),
    PATIENT("Patient");

    val displayName: String
        get() = tr(label)
}
