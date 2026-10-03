package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.model.TimeOfDay
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

// patients/{id}/medicines
@Serializable
data class MedicineDto(
    val name: String? = null,
    val dosage: String? = null,
    val timeOfDay: String? = null
)

// patients/{id}/appointments
@Serializable
data class AppointmentDto(
    val what: String? = null,
    val address: String? = null,
    val date: Timestamp? = null,
    val bringItems: List<String>? = null,
    val notes: String? = null
)

// employeeLocations/{employeeId}
@Serializable
data class EmployeeLocationDto(
    val lat: Double? = null,
    val lon: Double? = null,
    val updatedAt: Timestamp? = null
)

fun MedicineDto.toMedicineEntry(docId: String): MedicineEntry = MedicineEntry(
    id = docId,
    name = name ?: "",
    dosage = dosage ?: "",
    timeOfDay = TimeOfDay.fromValue(timeOfDay)
)

fun MedicineEntry.toDto(): MedicineDto = MedicineDto(
    name = name,
    dosage = dosage,
    timeOfDay = timeOfDay.displayName
)

fun AppointmentDto.toAppointment(docId: String): Appointment = Appointment(
    id = docId,
    what = what ?: "",
    address = address ?: "",
    date = date?.toMillis() ?: 0L,
    bringItems = bringItems.orEmpty(),
    notes = notes ?: ""
)

fun Appointment.toDto(): AppointmentDto = AppointmentDto(
    what = what,
    address = address,
    date = date.toTimestamp(),
    bringItems = bringItems,
    notes = notes.ifBlank { null }
)

fun EmployeeLocationDto.toCoordinate(): Coordinate? {
    val latitude = lat ?: return null
    val longitude = lon ?: return null
    return Coordinate(latitude, longitude)
}
