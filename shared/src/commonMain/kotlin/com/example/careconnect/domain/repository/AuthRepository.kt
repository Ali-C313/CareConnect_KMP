package com.example.careconnect.domain.repository

import com.example.careconnect.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // UID des eingeloggten Users oder null
    val authState: Flow<String?>
    val currentUserId: String?
    val currentUserEmail: String?

    fun observeRole(uid: String): Flow<UserRole?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<String>
    suspend fun logout(): Result<Unit>
    suspend fun changePassword(currentPassword: String?, newPassword: String): Result<Unit>
}
