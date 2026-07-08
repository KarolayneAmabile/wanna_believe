package com.example.wanna_believe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wanna_believe.data.Resource
import com.example.wanna_believe.data.model.Comment
import com.example.wanna_believe.data.repository.AuthRepository
import com.example.wanna_believe.data.repository.FirestoreRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _commentsState = MutableStateFlow<Resource<List<Comment>>>(Resource.Loading)
    val commentsState: StateFlow<Resource<List<Comment>>> = _commentsState.asStateFlow()

    fun observeComments(postId: String) {
        firestoreRepository.observeComments(postId)
            .onEach { comments -> _commentsState.value = Resource.Success(comments) }
            .catch { error -> _commentsState.value = Resource.Error(error.message ?: "Erro ao carregar comentários") }
            .launchIn(viewModelScope)
    }

    fun addComment(postId: String, text: String, authorName: String, authorPhotoUrl: String) {
        val uid = authRepository.currentUser?.uid ?: return
        val comment = Comment(
            authorUid = uid,
            authorName = authorName,
            authorPhotoUrl = authorPhotoUrl,
            text = text,
            timestamp = Timestamp.now()
        )
        viewModelScope.launch {
            firestoreRepository.addComment(postId, comment)
        }
    }
}
