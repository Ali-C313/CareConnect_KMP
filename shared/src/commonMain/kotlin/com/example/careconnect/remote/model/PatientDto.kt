package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.EmergencyContact
import com.example.careconnect.domain.model.Medication
import com.example.careconnect.domain.model.Patient
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

// Feldnamen wie in der iOS-App, damit die bestehende Datenbank passt
@Serializable
data class PatientDto(
    val uid: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val address: String? = null,
    val city: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val allergies: String? = null,
    val medicalHistory: String? = null,
    val age: Int? = null,
    val birthDate: Timestamp? = null,
    val notes: String? = null,
    val profileImageURL: String? = null,
    val medications: List<MedicationDto>? = null,
    val history: List<String>? = null,
    val emergencyContact: EmergencyContactDto? = null,
    val createdAt: Timestamp? = null
)

@Serializable
data class MedicationDto(
    val id: String? = null,
    val name: String? = null
)

@Serializable
data class EmergencyContactDto(
    val name: String? = null,
    val relation: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val age: Int? = null
)

// Nur für Updates mit merge = true
@Serializable
data class EmergencyContactUpdateDto(
    val emergencyContact: EmergencyContactDto
)

@Serializable
data class PatientProfileUpdateDto(
    val firstName: String,
    val lastName: String,
    val address: String,
    val city: String,
    val email: String,
    val phone: String,
    val allergies: String,
    val medicalHistory: String
)

fun PatientDto.toPatient(id: String): Patient = Patient(
    id = id,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    address = address ?: "",
    city = city ?: "",
    phone = phone ?: "",
    email = email ?: "",
    allergies = allergies ?: "",
    medicalHistory = medicalHistory ?: "",
    age = age,
    birthDate = birthDate?.toMillis(),
    notes = notes ?: "",
    profileImageUrl = profileImageURL ?: "",
    medications = medications.orEmpty().mapIndexed { index, med ->
        Medication(
            id = med.id ?: index.toString(),
            name = med.name ?: ""
        )
    },
    history = history.orEmpty(),
    emergencyContact = emergencyContact?.toEmergencyContact()
)

fun EmergencyContactDto.toEmergencyContact(): EmergencyContact = EmergencyContact(
    name = name ?: "",
    relation = relation ?: "",
    phone = phone ?: "",
    email = email ?: "",
    address = address ?: "",
    age = age
)

fun EmergencyContact.toDto(): EmergencyContactDto = EmergencyContactDto(
    name = name,
    relation = relation,
    phone = phone,
    email = email,
    address = address,
    age = age
)
