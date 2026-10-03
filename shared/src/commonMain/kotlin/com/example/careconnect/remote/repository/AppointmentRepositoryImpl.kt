package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.domain.repository.AppointmentRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.model.AppointmentDto
import com.example.careconnect.remote.model.toAppointment
import com.example.careconnect.remote.model.toDto
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.serializer

class AppointmentRepositoryImpl(
    private val db: FirebaseFirestore
) : AppointmentRepository {

    private fun appointmentsRef(patientId: String) = db.collection(CollectionNames.PATIENTS)
        .document(patientId)
        .collection(CollectionNames.APPOINTMENTS)

    override fun getAppointments(patientId: String): Flow<List<Appointment>> = appointmentsRef(patientId).snapshots
        .map { snapshot ->
            snapshot.documents
                .mapNotNull { doc ->
                    runCatching { doc.data<AppointmentDto>().toAppointment(doc.id) }.getOrNull()
                }
                .sortedBy { it.date }
        }
        .catch { emit(emptyList()) }

    override suspend fun addAppointment(patientId: String, appointment: Appointment): Result<Unit> = runCatching {
        appointmentsRef(patientId).add(AppointmentDto.serializer(), appointment.toDto())
        Unit
    }

    override suspend fun updateAppointment(patientId: String, appointment: Appointment): Result<Unit> = runCatching {
        appointmentsRef(patientId).document(appointment.id).set(AppointmentDto.serializer(), appointment.toDto(), merge = false)
    }

    override suspend fun deleteAppointment(patientId: String, appointmentId: String): Result<Unit> = runCatching {
        appointmentsRef(patientId).document(appointmentId).delete()
    }
}
