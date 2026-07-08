package com.example.wanna_believe.ui.screens.comments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wanna_believe.R
import com.example.wanna_believe.data.Resource
import com.example.wanna_believe.data.repository.AuthRepository
import com.example.wanna_believe.ui.components.ErrorMessage
import com.example.wanna_believe.ui.components.LoadingIndicator
import com.example.wanna_believe.viewmodel.CommentsViewModel

/**
 * Tela de Comentários
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    postId: String,
    onBack: () -> Unit,
    viewModel: CommentsViewModel = viewModel()
) {
    // Pegamos a lista de comentários que vem do Firestore através do ViewModel
    val commentsState by viewModel.commentsState.collectAsState()
    
    // Estado local para controlar o que o usuário está digitando no campo de texto
    var commentText by remember { mutableStateOf("") }
    
    // Repositório de autenticação para pegarmos os dados de quem está comentando
    val authRepository = remember { AuthRepository() }
    var authorName by remember { mutableStateOf("") }
    var authorPhotoUrl by remember { mutableStateOf("") }

    // Efeito disparado ao entrar na tela: começa a observar os comentários desse post específico
    LaunchedEffect(postId) {
        viewModel.observeComments(postId)
        
        // Buscamos o perfil do usuário logado para já ter o nome e a foto na hora de comentar
        val uid = authRepository.currentUser?.uid
        if (uid != null) {
            authRepository.getUserProfile(uid).onSuccess {
                authorName = it.name
                authorPhotoUrl = it.photoUrl
            }
        }
    }

    // O Scaffold é o nosso "esqueleto" da tela, facilitando a colocação da barra superior e inferior
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comentários") },
                // Botão de voltar padrão do sistema, conforme o design system do projeto
                navigationIcon = { 
                    TextButton(onClick = onBack) { 
                        Text("Voltar", color = MaterialTheme.colorScheme.primary) 
                    } 
                }
            )
        },
        bottomBar = {
            // A barra inferior contém o campo de entrada de texto
            Surface(
                tonalElevation = 8.dp,
                // navigationBarsPadding é CRUCIAL para não ficar colado nos botões do Android
                modifier = Modifier.navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Campo onde o usuário escreve. Usei cantos arredondados (24.dp) para ficar moderno.
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escreva um comentário...") },
                        maxLines = 3,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(postId, commentText, authorName, authorPhotoUrl)
                                commentText = "" // Limpa o campo depois de enviar
                            }
                        },
                        enabled = commentText.isNotBlank(),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    ) { padding ->
        // O corpo da tela lida com os estados do carregamento (Loading, Error, Success)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = commentsState) {
                // Enquanto carrega os dados do Firebase
                is Resource.Loading -> LoadingIndicator(modifier = Modifier.fillMaxSize())
                
                // Se der algum erro (ex: falta de internet)
                is Resource.Error -> ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.observeComments(postId) },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Quando os dados chegam com sucesso
                is Resource.Success -> {
                    if (state.data.isEmpty()) {
                        // Feedback visual caso ninguém tenha comentado ainda
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Nenhum comentário ainda. Seja o primeiro!", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        // Lista performática de comentários
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.data) { comment ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Foto do autor do comentário (redonda)
                                    AsyncImage(
                                        model = comment.authorPhotoUrl.ifEmpty { R.drawable.ic_launcher_foreground },
                                        contentDescription = "Foto de ${comment.authorName}",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    
                                    // Bloco de texto do comentário
                                    Column(
                                        modifier = Modifier
                                            .padding(start = 12.dp)
                                            .weight(1f)
                                    ) {
                                        Text(
                                            text = comment.authorName,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(0.dp, 12.dp, 12.dp, 12.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier.padding(top = 4.dp)
                                        ) {
                                            Text(
                                                text = comment.text,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
