package com.example.careconnect.ui.settings

import com.example.careconnect.i18n.tr

data class ProfileInfo(
    val name: String = tr("Unbekannt"),
    val email: String = "—",
    val imageUrl: String = ""
)
