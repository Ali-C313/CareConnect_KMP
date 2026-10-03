@file:OptIn(ExperimentalTime::class)

package com.example.careconnect.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

private fun Long.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault())

private fun Int.pad(): String = toString().padStart(2, '0')

/** @return z.B. "11.10.2025" */
fun Long.toDateString(): String {
    val dateTime = toLocalDateTime()
    return "${dateTime.day.pad()}.${dateTime.month.number.pad()}.${dateTime.year}"
}

/** @return z.B. "14:30" */
fun Long.toTimeString(): String {
    val dateTime = toLocalDateTime()
    return "${dateTime.hour.pad()}:${dateTime.minute.pad()}"
}

/** @return z.B. "11.10.2025, 14:30" */
fun Long.toDateTimeString(): String = "${toDateString()}, ${toTimeString()}"

/** Alter in vollen Jahren ab Geburtsdatum */
fun Long.toAge(): Int {
    val birth = toLocalDateTime().date
    val today = currentTimeMillis().toLocalDateTime().date
    var age = today.year - birth.year
    val hadBirthday = today.month.number > birth.month.number ||
            (today.month.number == birth.month.number && today.day >= birth.day)
    if (!hadBirthday) age--
    return age
}

fun Long.hour(): Int = toLocalDateTime().hour

fun Long.minute(): Int = toLocalDateTime().minute

/**
 * Der DatePicker arbeitet mit UTC-Mitternacht.
 * @return lokales Datum als UTC-Millisekunden für den DatePicker
 */
fun Long.toPickerDateMillis(): Long =
    toLocalDateTime().date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

/**
 * Setzt Datum aus dem DatePicker und Uhrzeit zusammen.
 * @param pickerDateMillis Wert aus dem DatePicker (UTC)
 * @return Zeitpunkt in lokaler Zeitzone als Millisekunden
 */
fun combineDateAndTime(pickerDateMillis: Long, hour: Int, minute: Int): Long {
    val date = Instant.fromEpochMilliseconds(pickerDateMillis).toLocalDateTime(TimeZone.UTC).date
    return LocalDateTime(date, LocalTime(hour, minute))
        .toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}
