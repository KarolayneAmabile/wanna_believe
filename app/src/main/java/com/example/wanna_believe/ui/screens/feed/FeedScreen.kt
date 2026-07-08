package com.example.wanna_believe.ui.screens.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanna_believe.data.Resource
import com.example.wanna_believe.ui.components.ErrorMessage
import com.example.wanna_believe.ui.components.LoadingIndicator
import com.example.wanna_believe.data.repository.AuthRepository
import com.example.wanna_believe.ui.components.PostItem
import com.example.wanna_believe.viewmodel.FeedViewModel

/**
 * Tela principal: lista de publicações em ordem cronológica
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onNavigateToNewPost: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToComments: (String) -> Unit,
    viewModel: FeedViewModel = viewModel()
) {
    // Observamos o estado do feed que vem do ViewModel
    val feedState by viewModel.feedState.collectAsState()
    val authRepository = remember { AuthRepository() }
    // Precisamos do ID do usuário atual para saber se ele pode deletar um post (só o autor pode)
    val currentUserId = authRepository.currentUser?.uid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("I want to believe") },
                actions = {
                    // Botão para ir ver o nosso perfil
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            // FAB (Botão de Ação Flutuante) para criar um post novo
            FloatingActionButton(
                onClick = onNavigateToNewPost,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nova publicação")
            }
        }
    ) { padding ->
        // Aqui checamos se está carregando, se deu erro ou se temos os dados
        when (val state = feedState) {
            is Resource.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is Resource.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.observeFeed() },
                modifier = Modifier.padding(padding)
            )
            is Resource.Success -> {
                if (state.data.isEmpty()) {
                    // Se não tiver nada, mostramos um aviso
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhuma publicação ainda. Seja o primeiro!")
                    }
                } else {
                    // LazyColumn é como o RecyclerView, só carrega o que tá na tela
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        items(state.data, key = { it.id }) { post ->
                            // Cada post é um componente separado para não poluir aqui
                            PostItem(
                                post = post,
                                currentUserId = currentUserId,
                                onLikeClick = { currentUserId?.let { uid -> viewModel.toggleLike(post.id, uid) } },
                                onDeleteClick = { viewModel.deletePost(post.id) },
                                onCommentsClick = { onNavigateToComments(post.id) }
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }
}
