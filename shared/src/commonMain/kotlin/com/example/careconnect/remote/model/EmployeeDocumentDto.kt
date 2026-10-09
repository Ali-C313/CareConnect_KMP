package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.EmployeeDocument
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDocumentDto(
    val id: String? = null,
    val fileName: String? = null,
    val downloadURL: String? = null,
    val uploadedAt: Timestamp? = null,
    val storagePath: String? = null
)
fun EmployeeDocumentDto.toEmployeeDocument(docId: String): EmployeeDocument = EmployeeDocument(
    id = docId,
    fileName = fileName ?: "Dokument.pdf",
    downloadUrl = downloadURL ?: "",
    uploadedAt = uploadedAt?.toMillis()
)
