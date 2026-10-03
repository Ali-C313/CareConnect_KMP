package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.EmergencyContact
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.domain.repository.PatientRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.model.EmergencyContactUpdateDto
import com.example.careconnect.remote.model.PatientDto
import com.example.careconnect.remote.model.PatientProfileUpdateDto
import com.example.careconnect.remote.model.toDto
import com.example.careconnect.remote.model.toPatient
import com.example.careconnect.remote.model.toTimestamp
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PatientRepositoryImpl(
    private val db: FirebaseFirestore
) : PatientRepository {

    private val patientsRef = db.collection(CollectionNames.PATIENTS)

    override fun getPatients(): Flow<List<Patient>> = patientsRef.snapshots
        .map { snapshot ->
            snapshot.documents
                .mapNotNull { doc ->
                    runCatching { doc.data<PatientDto>().toPatient(doc.id) }.getOrNull()
                }
                .sortedBy { it.name }
        }
        .catch { emit(emptyList()) }

    override fun getPatient(id: String): Flow<Patient?> = patientsRef.document(id).snapshots
        .map { doc ->
            if (!doc.exists) null
            else runCatching { doc.data<PatientDto>().toPatient(doc.id) }.getOrNull()
        }
        .catch { emit(null) }

    override suspend fun createPatient(
        uid: String,
        firstName: String,
        lastName: String,
        email: String,
        birthDate: Long?,
        address: String,
        city: String,
        phone: String,
        profileImageUrl: String
    ): Result<Unit> = runCatching {
        val dto = PatientDto(
            uid = uid,
            firstName = firstName,
            lastName = lastName,
            email = email,
            birthDate = birthDate?.toTimestamp(),
            address = address,
            city = city,
            phone = phone,
            notes = "",
            age = 0,
            profileImageURL = profileImageUrl,
            createdAt = Timestamp.now()
        )
        patientsRef.document(uid).set(PatientDto.serializer(), dto, merge = true)
    }

    override suspend fun updateProfile(patient: Patient): Result<Unit> = runCatching {
        val dto = PatientProfileUpdateDto(
            firstName = patient.firstName,
            lastName = patient.lastName,
            address = patient.address,
            city = patient.city,
            email = patient.email,
            phone = patient.phone,
            allergies = patient.allergies,
            medicalHistory = patient.medicalHistory
        )
        patientsRef.document(patient.id).set(PatientProfileUpdateDto.serializer(), dto, merge = true)
    }

    override suspend fun updateProfileImage(id: String, url: String): Result<Unit> = runCatching {
        patientsRef.document(id).updateFields {
            "profileImageURL" to url
        }
    }

    override suspend fun saveEmergencyContact(id: String, contact: EmergencyContact): Result<Unit> = runCatching {
        patientsRef.document(id).set(
            EmergencyContactUpdateDto.serializer(),
            EmergencyContactUpdateDto(contact.toDto()),
            merge = true
        )
    }
}
