package com.example.careconnect.ui.employee.prescriptions

import com.example.careconnect.i18n.tr

enum class PrescriptionDirection(private val label: String) {
    INCOMING("Eingang"),
    OUTGOING("Ausgang");

    val displayName: String
        get() = tr(label)
}
