package com.example.wanna_believe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanna_believe.data.Resource
import com.example.wanna_believe.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<Unit>>(Resource.Idle)
    val authState: StateFlow<Resource<Unit>> = _authState.asStateFlow()

    val isLoggedIn: Boolean get() = repository.currentUser != null

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = Resource.Loading
            val result = repository.login(email, password)
            _authState.value = result.fold(
                onSuccess = { Resource.Success(Unit) },
                onFailure = { Resource.Error(it.message ?: "Erro ao fazer login") }
            )
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = Resource.Loading
            val result = repository.register(name, email, password)
            _authState.value = result.fold(
                onSuccess = { Resource.Success(Unit) },
                onFailure = { Resource.Error(it.message ?: "Erro ao criar conta") }
            )
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = Resource.Idle
    }

    fun resetState() {
        _authState.value = Resource.Idle
    }
}
