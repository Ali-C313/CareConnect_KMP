package com.example.careconnect.domain.model

import com.example.careconnect.i18n.tr

enum class OrganizationType(val value: String, private val label: String) {
    PFLEGEDIENST("pflegedienst", "Pflegedienst"),
    ARZTPRAXIS("arztpraxis", "Arztpraxis"),
    KLINIK("klinik", "Klinik");

    // Text in der gewählten Sprache
    val displayName: String
        get() = tr(label)

    companion object {
        fun fromValue(value: String?): OrganizationType =
            entries.firstOrNull { it.value == value } ?: PFLEGEDIENST
    }
}
