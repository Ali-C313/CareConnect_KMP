package com.example.careconnect.ui.settings

import com.example.careconnect.i18n.tr

// Name, E-Mail und Bild für den Kopf der Einstellungen
data class ProfileInfo(
    val name: String = tr("Unbekannt"),
    val email: String = "—",
    val imageUrl: String = ""
)
