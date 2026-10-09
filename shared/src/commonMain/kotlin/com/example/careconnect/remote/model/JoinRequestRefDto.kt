package com.example.careconnect.remote.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

// Nur die Organisations-ID, damit der Patient seine Anfragen wiederfindet
@Serializable
data class JoinRequestRefDto(
    val orgId: String? = null,
    val createdAt: Timestamp? = null
)
