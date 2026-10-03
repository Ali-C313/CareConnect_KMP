package com.example.careconnect.remote.repository

import com.example.careconnect.domain.repository.StorageRepository
import com.example.careconnect.platform.toStorageData
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.storageMetadata

class StorageRepositoryImpl(
    private val storage: FirebaseStorage
) : StorageRepository {

    override suspend fun uploadFile(path: String, bytes: ByteArray, contentType: String): Result<String> = runCatching {
        val ref = storage.reference(path)
        val type = contentType
        ref.putData(
            bytes.toStorageData(),
            storageMetadata { this.contentType = type }
        )
        ref.getDownloadUrl()
    }

    override suspend fun deleteFile(path: String): Result<Unit> = runCatching {
        storage.reference(path).delete()
    }
}
