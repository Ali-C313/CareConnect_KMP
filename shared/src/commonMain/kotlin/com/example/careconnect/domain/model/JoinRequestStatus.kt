package com.example.careconnect.domain.model

import com.example.careconnect.i18n.tr

// Status einer Beitrittsanfrage
enum class JoinRequestStatus(val value: String, private val label: String) {
    PENDING("pending", "Ausstehend"),
    ACCEPTED("accepted", "Angenommen"),
    DECLINED("declined", "Abgelehnt");

    // Text in der gewählten Sprache
    val displayName: String
        get() = tr(label)

    companion object {
        fun fromValue(value: String?): JoinRequestStatus =
            entries.firstOrNull { it.value == value } ?: PENDING
    }
}
