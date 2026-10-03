package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.UserRole
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.remote.CollectionNames
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepository {

    override val authState: Flow<String?> = auth.authStateChanged.map { it?.uid }

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override val currentUserEmail: String?
        get() = auth.currentUser?.email

    // Rolle ergibt sich daraus, in welcher Collection das Profil liegt
    override fun observeRole(uid: String): Flow<UserRole?> = combine(
        db.collection(CollectionNames.EMPLOYEES).document(uid).snapshots,
        db.collection(CollectionNames.PATIENTS).document(uid).snapshots
    ) { employee, patient ->
        when {
            employee.exists -> UserRole.EMPLOYEE
            patient.exists -> UserRole.PATIENT
            else -> null
        }
    }.catch { emit(null) }

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password)
        Unit
    }

    override suspend fun register(email: String, password: String): Result<String> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password)
        result.user?.uid ?: throw Exception("Registrierung fehlgeschlagen")
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        auth.signOut()
    }

    override suspend fun changePassword(currentPassword: String?, newPassword: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw Exception("Kein eingeloggter Benutzer gefunden.")
        // Bei Bedarf neu anmelden, sonst lehnt Firebase die Änderung ab
        if (!currentPassword.isNullOrEmpty()) {
            val credential = EmailAuthProvider.credential(user.email ?: "", currentPassword)
            user.reauthenticate(credential)
        }
        user.updatePassword(newPassword)
    }
}
