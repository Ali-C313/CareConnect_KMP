package com.example.careconnect.platform

import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

actual fun deviceLanguage(): String =
    (NSLocale.preferredLanguages.firstOrNull() as? String)?.substringBefore("-") ?: "de"
