package com.example.careconnect.ui.employee.prescriptions

import com.example.careconnect.i18n.tr

// Eingang oder Ausgang bei den Verordnungen
enum class PrescriptionDirection(private val label: String) {
    INCOMING("Eingang"),
    OUTGOING("Ausgang");

    val displayName: String
        get() = tr(label)
}
