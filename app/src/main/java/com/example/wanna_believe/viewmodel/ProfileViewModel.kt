package com.example.wanna_believe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanna_believe.data.Resource
import com.example.wanna_believe.data.model.User
import com.example.wanna_believe.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import android.net.Uri
import com.example.wanna_believe.data.repository.StorageRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<User>>(Resource.Loading)
    val profileState: StateFlow<Resource<User>> = _profileState.asStateFlow()

    private val _updateState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val updateState: StateFlow<Resource<Unit>> = _updateState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = Resource.Loading
            val uid = authRepository.currentUser?.uid
            if (uid == null) {
                _profileState.value = Resource.Error("Usuário não autenticado")
                return@launch
            }
            val result = authRepository.getUserProfile(uid)
            _profileState.value = result.fold(
                onSuccess = { Resource.Success(it) },
                onFailure = { Resource.Error(it.message ?: "Erro ao carregar perfil") }
            )
        }
    }

    fun updateProfile(name: String, imageUri: Uri?) {
        viewModelScope.launch {
            _updateState.value = Resource.Loading
            val uid = authRepository.currentUser?.uid ?: return@launch
            
            try {
                var photoUrl = (profileState.value as? Resource.Success)?.data?.photoUrl ?: ""
                
                if (imageUri != null) {
                    val uploadResult = storageRepository.uploadProfileImage(imageUri, uid)
                    photoUrl = uploadResult.getOrThrow()
                }

                val updates = mapOf(
                    "name" to name,
                    "photoUrl" to photoUrl
                )
                
                firestore.collection("users").document(uid).update(updates).await()
                loadProfile()
                _updateState.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _updateState.value = Resource.Error(e.message ?: "Erro ao atualizar perfil")
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun resetUpdateState() {
        _updateState.value = Resource.Idle
    }
}
