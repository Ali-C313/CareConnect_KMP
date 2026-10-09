package com.example.careconnect.domain.model

import com.example.careconnect.i18n.tr

enum class OrderState(val value: String, private val label: String) {
    OFFEN("offen", "Offen"),
    AKTUELL("aktuell", "Aktuell"),
    ERLEDIGT("erledigt", "Erledigt");

    // Text in der gewählten Sprache
    val displayName: String
        get() = tr(label)

    companion object {
        fun fromValue(value: String?): OrderState =
            entries.firstOrNull { it.value == value?.lowercase() } ?: OFFEN
    }
}
