package com.example.careconnect.ui.patient.orders

import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.i18n.tr

// Statustext aus Sicht des Patienten ("Unterwegs" statt "Aktuell")
val OrderState.patientLabel: String
    get() = when (this) {
        OrderState.OFFEN -> tr("Offen")
        OrderState.AKTUELL -> tr("Unterwegs")
        OrderState.ERLEDIGT -> tr("Erledigt")
    }
