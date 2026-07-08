package com.example.wanna_believe.data.model

import com.google.firebase.Timestamp

data class Comment(
    val id: String = "",
    val authorUid: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String = "",
    val text: String = "",
    val timestamp: Timestamp? = null
)
