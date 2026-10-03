package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.repository.MedicineRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.model.MedicineDto
import com.example.careconnect.remote.model.toDto
import com.example.careconnect.remote.model.toMedicineEntry
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class MedicineRepositoryImpl(
    private val db: FirebaseFirestore
) : MedicineRepository {

    private fun medicinesRef(patientId: String) = db.collection(CollectionNames.PATIENTS)
        .document(patientId)
        .collection(CollectionNames.MEDICINES)

    override fun getMedicines(patientId: String): Flow<List<MedicineEntry>> = medicinesRef(patientId).snapshots
        .map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                runCatching { doc.data<MedicineDto>().toMedicineEntry(doc.id) }.getOrNull()
            }
        }
        .catch { emit(emptyList()) }

    override suspend fun addMedicine(patientId: String, medicine: MedicineEntry): Result<Unit> = runCatching {
        medicinesRef(patientId).add(MedicineDto.serializer(), medicine.toDto())
        Unit
    }

    override suspend fun deleteMedicine(patientId: String, medicineId: String): Result<Unit> = runCatching {
        medicinesRef(patientId).document(medicineId).delete()
    }
}
