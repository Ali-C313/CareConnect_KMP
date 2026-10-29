package com.example.careconnect.domain.model

import com.example.careconnect.i18n.tr

// Mitglied einer Organisation mit Rolle
data class Member(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val role: MemberRole
) {
    val name: String
        get() = "$firstName $lastName".trim().ifEmpty { tr("Unbekannt") }
}
