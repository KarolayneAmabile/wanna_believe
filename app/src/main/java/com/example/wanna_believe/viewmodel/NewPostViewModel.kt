package com.example.wanna_believe.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanna_believe.data.Resource
import com.example.wanna_believe.data.model.Post
import com.example.wanna_believe.data.repository.AuthRepository
import com.example.wanna_believe.data.repository.FirestoreRepository
import com.example.wanna_believe.data.repository.StorageRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewPostViewModel(
    private val storageRepository: StorageRepository = StorageRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _postState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val postState: StateFlow<Resource<Unit>> = _postState.asStateFlow()

    fun publishPost(description: String, imageUri: Uri?, authorName: String, authorPhotoUrl: String) {
        viewModelScope.launch {
            _postState.value = Resource.Loading
            val uid = authRepository.currentUser?.uid
            if (uid == null) {
                _postState.value = Resource.Error("Usuário não autenticado")
                return@launch
            }

            if (imageUri != null) {
                // 1) Upload da imagem para o Storage
                val uploadResult = storageRepository.uploadPostImage(imageUri)
                uploadResult.fold(
                    onSuccess = { imageUrl ->
                        savePostToFirestore(uid, authorName, authorPhotoUrl, imageUrl, description)
                    },
                    onFailure = { _postState.value = Resource.Error(it.message ?: "Erro no upload da imagem") }
                )
            } else {
                // Apenas texto
                savePostToFirestore(uid, authorName, authorPhotoUrl, "", description)
            }
        }
    }

    private suspend fun savePostToFirestore(uid: String, authorName: String, authorPhotoUrl: String, imageUrl: String, description: String) {
        val post = Post(
            authorUid = uid,
            authorName = authorName,
            authorPhotoUrl = authorPhotoUrl,
            imageUrl = imageUrl,
            description = description,
            timestamp = Timestamp.now()
        )
        val createResult = firestoreRepository.createPost(post)
        _postState.value = createResult.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = { Resource.Error(it.message ?: "Erro ao publicar") }
        )
    }

    fun resetState() {
        _postState.value = Resource.Idle
    }
}
