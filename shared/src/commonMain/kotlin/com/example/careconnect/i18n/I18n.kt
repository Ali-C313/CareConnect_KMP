package com.example.careconnect.i18n

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.careconnect.platform.deviceLanguage

// Aktuelle Sprache. Ist ein Compose-State, damit sich alle Texte beim Wechsel neu zeichnen
object I18n {
    var language by mutableStateOf(AppLanguage.fromCode(deviceLanguage()))
}

/**
 * Übersetzt einen deutschen Text in die aktuelle Sprache.
 * Platzhalter: {0}, {1}, ... werden durch args ersetzt.
 * Leerzeichen am Anfang/Ende bleiben erhalten.
 */
fun tr(text: String, vararg args: Any?): String {
    val key = text.trim()
    val table = when (I18n.language) {
        AppLanguage.GERMAN -> null
        AppLanguage.ENGLISH -> englishTable
        AppLanguage.TURKISH -> turkishTable
        AppLanguage.ARABIC -> arabicTable
        AppLanguage.SPANISH -> spanishTable
    }
    var result = table?.get(key) ?: key
    args.forEachIndexed { index, arg -> result = result.replace("{$index}", arg?.toString() ?: "") }
    val start = text.length - text.trimStart().length
    val end = text.length - text.trimEnd().length
    return text.take(start) + result + text.takeLast(end)
}
