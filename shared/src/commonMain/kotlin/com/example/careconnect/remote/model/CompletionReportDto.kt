package com.example.careconnect.remote.model

import com.example.careconnect.domain.model.CompletionReport
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class CompletionReportDto(
    val note: String? = null,
    val signature: String? = null,
    val signedBy: String? = null,
    val completedAt: Timestamp? = null,
    val completedBy: String? = null,
    val completedByName: String? = null
)
fun CompletionReportDto.toReport(): CompletionReport = CompletionReport(
    note = note ?: "",
    signature = signature ?: "",
    signedBy = signedBy ?: "",
    completedAt = completedAt?.toMillis(),
    completedBy = completedBy ?: "",
    completedByName = completedByName ?: ""
)
fun CompletionReport.toDto(): CompletionReportDto = CompletionReportDto(
    note = note,
    signature = signature,
    signedBy = signedBy,
    completedAt = completedAt?.toTimestamp(),
    completedBy = completedBy,
    completedByName = completedByName
)
