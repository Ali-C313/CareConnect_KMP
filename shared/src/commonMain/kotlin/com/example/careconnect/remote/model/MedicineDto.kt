package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.model.TimeOfDay
import kotlinx.serialization.Serializable

// patients/{id}/medicines
@Serializable
data class MedicineDto(
    val name: String? = null,
    val dosage: String? = null,
    val timeOfDay: String? = null
)

// Firestore -> App-Model
fun MedicineDto.toMedicineEntry(docId: String): MedicineEntry = MedicineEntry(
    id = docId,
    name = name ?: "",
    dosage = dosage ?: "",
    timeOfDay = TimeOfDay.fromValue(timeOfDay)
)

// App-Model -> Firestore
fun MedicineEntry.toDto(): MedicineDto = MedicineDto(
    name = name,
    dosage = dosage,
    timeOfDay = timeOfDay.value
)
