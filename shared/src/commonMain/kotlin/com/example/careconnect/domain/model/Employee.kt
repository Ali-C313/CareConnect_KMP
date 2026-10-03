package com.example.careconnect.domain.model

data class Employee(
    val id: String,
    val firstName: String = "",
    val lastName: String = "",
    val role: String = "",
    val status: StaffStatus = StaffStatus.AVAILABLE,
    val email: String = "",
    val birthDate: Long? = null,
    val profileImageUrl: String = "",
    val workSchedule: WorkSchedule = WorkSchedule()
) {
    val name: String
        get() = "$firstName $lastName".trim()
}

data class EmployeeDocument(
    val id: String,
    val fileName: String,
    val downloadUrl: String,
    val uploadedAt: Long?
)

data class WorkSchedule(
    val weeklyHours: Int = 40,
    val shiftStart: String = "08:00",
    val shiftEnd: String = "16:30",
    val workDays: List<String> = listOf("Mo", "Di", "Mi", "Do", "Fr")
)
