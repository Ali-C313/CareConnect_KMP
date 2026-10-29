package com.example.careconnect.domain.model

// Einladung mit Code für neue Mitglieder
data class Invite(
    val code: String,
    val orgId: String,
    val orgName: String,
    val orgType: OrganizationType,
    val role: MemberRole
)
