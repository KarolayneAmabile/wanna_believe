package com.example.wanna_believe.data.repository

import com.example.wanna_believe.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Encapsula Firebase Authentication + criação do perfil correspondente no Firestore.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // Gerenciamento de sessão automático: o SDK do Firebase Auth já persiste
    // a sessão localmente entre reinicializações do app.
    val currentUser get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<Unit> = try {
        auth.signInWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun register(name: String, email: String, password: String): Result<Unit> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw IllegalStateException("Falha ao criar usuário")

        // Requisito 3b: criar documento em users/{uid} no momento do cadastro
        val user = User(uid = uid, name = name, email = email)
        firestore.collection("users").document(uid).set(user).await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getUserProfile(uid: String): Result<User> = try {
        val snapshot = firestore.collection("users").document(uid).get().await()
        val user = snapshot.toObject(User::class.java)
            ?: throw IllegalStateException("Usuário não encontrado")
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
