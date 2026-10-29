package com.example.careconnect.ui.organization

import com.example.careconnect.domain.model.Organization
import com.example.careconnect.domain.model.OrganizationType

// Zustand beim Erstellen oder Beitreten einer Organisation
data class OrganizationUiState(
    val code: String = "",
    val orgName: String = "",
    val orgType: OrganizationType = OrganizationType.PFLEGEDIENST,
    val orgAddress: String = "",
    val searchQuery: String = "",
    val searchResults: List<Organization> = emptyList(),
    val isSearching: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
