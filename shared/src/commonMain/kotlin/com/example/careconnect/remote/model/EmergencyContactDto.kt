package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.EmergencyContact
import kotlinx.serialization.Serializable

// Notfallkontakt in Firestore
@Serializable
data class EmergencyContactDto(
    val name: String? = null,
    val relation: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val age: Int? = null
)

// Firestore -> App-Model
fun EmergencyContactDto.toEmergencyContact(): EmergencyContact = EmergencyContact(
    name = name ?: "",
    relation = relation ?: "",
    phone = phone ?: "",
    email = email ?: "",
    address = address ?: "",
    age = age
)

// App-Model -> Firestore
fun EmergencyContact.toDto(): EmergencyContactDto = EmergencyContactDto(
    name = name,
    relation = relation,
    phone = phone,
    email = email,
    address = address,
    age = age
)
