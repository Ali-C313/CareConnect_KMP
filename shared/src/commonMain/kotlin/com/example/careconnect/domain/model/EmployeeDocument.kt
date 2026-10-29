package com.example.careconnect.domain.model

// Hochgeladenes PDF eines Mitarbeiters
data class EmployeeDocument(
    val id: String,
    val fileName: String,
    val downloadUrl: String,
    val uploadedAt: Long?
)
