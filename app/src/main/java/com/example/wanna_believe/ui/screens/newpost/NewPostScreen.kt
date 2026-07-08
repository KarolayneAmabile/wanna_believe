package com.example.wanna_believe.ui.screens.newpost

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wanna_believe.data.Resource
import com.example.wanna_believe.data.repository.AuthRepository
import com.example.wanna_believe.viewmodel.NewPostViewModel

/**
 * Tela de nova publicação (requisito 3d): campo de descrição + seleção de
 * imagem da galeria + upload para Storage e persistência no Firestore.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(
    onPostPublished: () -> Unit,
    viewModel: NewPostViewModel = viewModel()
) {
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var authorName by remember { mutableStateOf("") }
    var authorPhotoUrl by remember { mutableStateOf("") }
    val postState by viewModel.postState.collectAsState()
    val authRepository = remember { AuthRepository() }

    LaunchedEffect(Unit) {
        val uid = authRepository.currentUser?.uid
        if (uid != null) {
            authRepository.getUserProfile(uid).onSuccess {
                authorName = it.name
                authorPhotoUrl = it.photoUrl
            }
        }
    }

    LaunchedEffect(postState) {
        if (postState is Resource.Success) {
            onPostPublished()
            viewModel.resetState()
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nova publicação") }) },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Button(
                    onClick = { viewModel.publishPost(description, imageUri, authorName, authorPhotoUrl) },
                    enabled = description.isNotBlank() && postState !is Resource.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Publicar")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Imagem selecionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedButton(onClick = { imagePicker.launch("image/*") }) {
                Text(if (imageUri == null) "Selecionar imagem" else "Trocar imagem")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("O que você viu?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (postState is Resource.Error) {
                Text(text = (postState as Resource.Error).message, color = MaterialTheme.colorScheme.error)
            }
            if (postState is Resource.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
