package com.example.wanna_believe.data

/**
 * Wrapper genérico de estado de UI para operações assíncronas
 * (login, cadastro, feed, upload, etc). Usado pelos ViewModels via StateFlow.
 */
sealed class Resource<out T> {
    data object Idle : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}
