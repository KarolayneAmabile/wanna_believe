package com.example.wanna_believe.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wanna_believe.R
import com.example.wanna_believe.data.Resource
import com.example.wanna_believe.ui.components.ErrorMessage
import com.example.wanna_believe.ui.components.LoadingIndicator
import com.example.wanna_believe.viewmodel.ProfileViewModel

/**
 * Tela de Perfil - Aqui o usuário pode ver seus dados, trocar a foto e o nome
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    // Estados do ViewModel: um para carregar o perfil e outro para o status do salvamento
    val profileState by viewModel.profileState.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    
    // Estados locais para edição antes de mandar pro Firebase
    var name by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Abre a galeria do celular pra escolher uma foto
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    // Quando o perfil carrega com sucesso, a preenche o campo de nome com o que veio do banco
    LaunchedEffect(profileState) {
        if (profileState is Resource.Success) {
            name = (profileState as Resource.Success).data.name
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil") },
                navigationIcon = { 
                    // Botão "Voltar" customizado seguindo o guia de estilo
                    TextButton(onClick = onBack) { 
                        Text("Voltar", color = MaterialTheme.colorScheme.primary) 
                    } 
                }
            )
        }
    ) { padding ->
        when (val state = profileState) {
            // Mostra o spinner enquanto o Firestore responde
            is Resource.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            
            // Se der erro, mostra o botão de tentar novamente
            is Resource.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadProfile() },
                modifier = Modifier.padding(padding)
            )
            
            is Resource.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Avatar (Foto): se o usuário escolheu uma nova, mostra ela, senão mostra a do banco
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape) // Corta em círculo
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { imagePicker.launch("image/*") } // Abre a galeria ao clicar na foto
                    ) {
                        AsyncImage(
                            model = imageUri ?: state.data.photoUrl.ifEmpty { R.drawable.ic_launcher_foreground },
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Ícone de câmera pequeno no canto da foto pra indicar que dá pra editar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }

                    Text(
                        text = "Toque para alterar a foto",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Campo de edição do nome
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Seu Nome") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Se estiver salvando no Firebase, mostra o progresso. Senão, mostra o botão.
                    if (updateState is Resource.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else {
                        Button(
                            onClick = { viewModel.updateProfile(name, imageUri) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = name.isNotBlank(), // Só deixa clicar se tiver algo escrito
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Salvar Alterações", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    // Spacer com peso 1 empurra o botão de "Sair" lá pra baixo
                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = {
                            viewModel.logout()
                            onLogout()
                        },
                        modifier = Modifier.padding(bottom = 24.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Sair da conta", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            else -> {}
        }
    }
}
