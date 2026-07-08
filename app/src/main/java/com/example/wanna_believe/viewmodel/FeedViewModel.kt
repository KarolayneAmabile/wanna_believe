package com.example.wanna_believe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanna_believe.data.Resource
import com.example.wanna_believe.data.model.Post
import com.example.wanna_believe.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _feedState = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val feedState: StateFlow<Resource<List<Post>>> = _feedState.asStateFlow()

    init {
        observeFeed()
    }

    fun observeFeed() {
        repository.observePosts()
            .onEach { posts -> _feedState.value = Resource.Success(posts) }
            .catch { error -> _feedState.value = Resource.Error(error.message ?: "Erro ao carregar feed") }
            .launchIn(viewModelScope)
    }

    fun deletePost(postId: String) {
        viewModelScope.launch { repository.deletePost(postId) }
    }

    fun toggleLike(postId: String, userId: String) {
        viewModelScope.launch { repository.toggleLike(postId, userId) }
    }
}
