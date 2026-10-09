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
import com.example.careconnect.remote.org
import com.example.careconnect.session.OrgSession
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PatientRepositoryImpl(
    private val db: FirebaseFirestore,
    private val session: OrgSession
) : PatientRepository {

    private fun patientsRef(orgId: String = session.requireOrgId()) =
        db.org(orgId).collection(CollectionNames.PATIENTS)

    override fun getPatients(): Flow<List<Patient>> = session.inOrg(emptyList()) { orgId ->
        patientsRef(orgId).snapshots
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { doc ->
                        runCatching { doc.data<PatientDto>().toPatient(doc.id) }.getOrNull()
                    }
                    .sortedBy { it.name }
            }
            .catch { emit(emptyList()) }
    }

    override fun getPatient(id: String): Flow<Patient?> = session.inOrg(null) { orgId ->
        patientsRef(orgId).document(id).snapshots
            .map { doc ->
                if (!doc.exists) null
                else runCatching { doc.data<PatientDto>().toPatient(doc.id) }.getOrNull()
            }
            .catch { emit(null) }
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
        patientsRef().document(patient.id).set(PatientProfileUpdateDto.serializer(), dto, merge = true)
    }

    override suspend fun updateProfileImage(id: String, url: String): Result<Unit> = runCatching {
        patientsRef().document(id).updateFields {
            "profileImageURL" to url
        }
    }

    override suspend fun saveEmergencyContact(id: String, contact: EmergencyContact): Result<Unit> = runCatching {
        patientsRef().document(id).set(
            EmergencyContactUpdateDto.serializer(),
            EmergencyContactUpdateDto(contact.toDto()),
            merge = true
        )
    }
}
