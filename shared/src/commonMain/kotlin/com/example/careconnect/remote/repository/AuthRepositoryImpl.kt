package com.example.careconnect.remote.repository

import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.i18n.tr
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val authState: Flow<String?> = auth.authStateChanged.map { it?.uid }

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override val currentUserEmail: String?
        get() = auth.currentUser?.email

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password)
        Unit
    }

    override suspend fun register(email: String, password: String): Result<String> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password)
        result.user?.uid ?: throw Exception(tr("Registrierung fehlgeschlagen"))
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        auth.signOut()
    }

    override suspend fun changePassword(currentPassword: String?, newPassword: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw Exception(tr("Kein eingeloggter Benutzer gefunden."))
        // Bei Bedarf neu anmelden, sonst lehnt Firebase die Änderung ab
        if (!currentPassword.isNullOrEmpty()) {
            val credential = EmailAuthProvider.credential(user.email ?: "", currentPassword)
            user.reauthenticate(credential)
        }
        user.updatePassword(newPassword)
    }
}
