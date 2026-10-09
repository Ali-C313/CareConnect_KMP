package com.example.careconnect.remote.repository

import com.example.careconnect.domain.model.UserProfile
import com.example.careconnect.domain.repository.UserRepository
import com.example.careconnect.remote.CollectionNames
import com.example.careconnect.remote.model.UserProfileDto
import com.example.careconnect.remote.model.toTimestamp
import com.example.careconnect.remote.model.toUserProfile
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val db: FirebaseFirestore
) : UserRepository {

    private fun userRef(uid: String) = db.collection(CollectionNames.USERS).document(uid)

    override fun observeProfile(uid: String): Flow<UserProfile?> = userRef(uid).snapshots
        .map { doc ->
            if (!doc.exists) null
            else runCatching { doc.data<UserProfileDto>().toUserProfile(doc.id) }.getOrNull()
        }
        .catch { emit(null) }

    override suspend fun getProfile(uid: String): Result<UserProfile?> = runCatching {
        val doc = userRef(uid).get()
        if (!doc.exists) null else doc.data<UserProfileDto>().toUserProfile(doc.id)
    }

    override suspend fun createProfile(profile: UserProfile): Result<Unit> = runCatching {
        val dto = UserProfileDto(
            firstName = profile.firstName,
            lastName = profile.lastName,
            email = profile.email,
            birthDate = profile.birthDate?.toTimestamp(),
            address = profile.address,
            city = profile.city,
            phone = profile.phone,
            profileImageURL = profile.profileImageUrl,
            activeOrgId = null,
            createdAt = Timestamp.now()
        )
        userRef(profile.uid).set(UserProfileDto.serializer(), dto, merge = true)
    }

    override suspend fun setActiveOrg(uid: String, orgId: String): Result<Unit> = runCatching {
        userRef(uid).updateFields {
            "activeOrgId" to orgId
        }
    }
}
