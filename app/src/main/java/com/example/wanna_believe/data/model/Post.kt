package com.example.wanna_believe.data.model

import com.google.firebase.Timestamp

/**
 * Representa o documento de uma publicação na coleção "posts" do Firestore.
 * Requisito 3d do enunciado: imageUrl, description, uid do autor e timestamp.
 */
data class Post(
    val id: String = "",
    val authorUid: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val timestamp: Timestamp? = null,
    val likeCount: Int = 0,
    val likedBy: List<String> = emptyList()
)
