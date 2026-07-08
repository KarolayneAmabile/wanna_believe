package com.example.wanna_believe.data.repository

import com.example.wanna_believe.data.model.Comment
import com.example.wanna_believe.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Acesso à coleção "posts". Usa addSnapshotListener + callbackFlow para que
 * o feed (requisito 3c) atualize em tempo real automaticamente.
 */
class FirestoreRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val postsCollection = firestore.collection("posts")

    fun observePosts(): Flow<List<Post>> = callbackFlow {
        val registration = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(posts)
            }
        awaitClose { registration.remove() }
    }

    suspend fun createPost(post: Post): Result<Unit> = try {
        postsCollection.add(post).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun toggleLike(postId: String, userId: String): Result<Unit> = try {
        val postRef = postsCollection.document(postId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val likedBy = snapshot.get("likedBy") as? List<String> ?: emptyList()
            val newLikedBy = if (likedBy.contains(userId)) {
                likedBy - userId
            } else {
                likedBy + userId
            }
            transaction.update(postRef, "likedBy", newLikedBy)
            transaction.update(postRef, "likeCount", newLikedBy.size)
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun observeComments(postId: String): Flow<List<Comment>> = callbackFlow {
        val registration = postsCollection.document(postId).collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val comments = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Comment::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(comments)
            }
        awaitClose { registration.remove() }
    }

    suspend fun addComment(postId: String, comment: Comment): Result<Unit> = try {
        postsCollection.document(postId).collection("comments").add(comment).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deletePost(postId: String): Result<Unit> = try {
        // Nota: A exclusão de subcoleções no Firestore não é automática ao deletar o documento pai via SDK.
        // Para um projeto simples, deletar o post pai é o principal.
        postsCollection.document(postId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
