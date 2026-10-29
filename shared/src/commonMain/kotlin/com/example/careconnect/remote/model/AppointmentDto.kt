package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Appointment
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

// patients/{id}/appointments
@Serializable
data class AppointmentDto(
    val what: String? = null,
    val address: String? = null,
    val date: Timestamp? = null,
    val bringItems: List<String>? = null,
    val notes: String? = null
)

// Firestore -> App-Model
fun AppointmentDto.toAppointment(docId: String): Appointment = Appointment(
    id = docId,
    what = what ?: "",
    address = address ?: "",
    date = date?.toMillis() ?: 0L,
    bringItems = bringItems.orEmpty(),
    notes = notes ?: ""
)

// App-Model -> Firestore
fun Appointment.toDto(): AppointmentDto = AppointmentDto(
    what = what,
    address = address,
    date = date.toTimestamp(),
    bringItems = bringItems,
    notes = notes.ifBlank { null }
)
