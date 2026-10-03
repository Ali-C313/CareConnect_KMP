package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.Employee
import com.example.careconnect.domain.model.EmployeeDocument
import com.example.careconnect.domain.model.StaffStatus
import com.example.careconnect.domain.model.WorkSchedule
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDto(
    val uid: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: String? = null,
    val status: String? = null,
    val email: String? = null,
    val birthDate: Timestamp? = null,
    val profileImageURL: String? = null,
    val workSchedule: WorkScheduleDto? = null
)

@Serializable
data class WorkScheduleDto(
    val weeklyHours: Int? = null,
    val shiftStart: String? = null,
    val shiftEnd: String? = null,
    val workDays: List<String>? = null
)

@Serializable
data class EmployeeDocumentDto(
    val id: String? = null,
    val fileName: String? = null,
    val downloadURL: String? = null,
    val uploadedAt: Timestamp? = null,
    val storagePath: String? = null
)

// Nur für Updates mit merge = true
@Serializable
data class EmployeeProfileUpdateDto(
    val firstName: String,
    val lastName: String,
    val role: String,
    val email: String
)

fun EmployeeDto.toEmployee(id: String): Employee = Employee(
    id = id,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    role = role ?: "",
    status = StaffStatus.fromValue(status),
    email = email ?: "",
    birthDate = birthDate?.toMillis(),
    profileImageUrl = profileImageURL ?: "",
    workSchedule = workSchedule.toWorkSchedule()
)

fun WorkScheduleDto?.toWorkSchedule(): WorkSchedule {
    val default = WorkSchedule()
    if (this == null) return default
    return WorkSchedule(
        weeklyHours = weeklyHours ?: default.weeklyHours,
        shiftStart = shiftStart ?: default.shiftStart,
        shiftEnd = shiftEnd ?: default.shiftEnd,
        workDays = workDays ?: default.workDays
    )
}

fun EmployeeDocumentDto.toEmployeeDocument(docId: String): EmployeeDocument = EmployeeDocument(
    id = docId,
    fileName = fileName ?: "Dokument.pdf",
    downloadUrl = downloadURL ?: "",
    uploadedAt = uploadedAt?.toMillis()
)
