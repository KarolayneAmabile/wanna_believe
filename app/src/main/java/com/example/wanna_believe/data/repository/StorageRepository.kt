package com.example.wanna_believe.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Upload de imagens de publicações para o Firebase Cloud Storage (requisito 3d).
 */
class StorageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    suspend fun uploadPostImage(imageUri: Uri): Result<String> = try {
        val ref = storage.reference.child("posts/${UUID.randomUUID()}.jpg")
        ref.putFile(imageUri).await()
        val url = ref.downloadUrl.await()
        Result.success(url.toString())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun uploadProfileImage(imageUri: Uri, userId: String): Result<String> = try {
        val ref = storage.reference.child("profiles/$userId.jpg")
        ref.putFile(imageUri).await()
        val url = ref.downloadUrl.await()
        Result.success(url.toString())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
