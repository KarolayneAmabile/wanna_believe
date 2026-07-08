package com.example.wanna_believe.data.model

/**
 * Representa o documento do usuário na coleção "users" do Firestore.
 * Requisito 3b do enunciado: uid, nome e e-mail no mínimo.
 */
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = ""
)
