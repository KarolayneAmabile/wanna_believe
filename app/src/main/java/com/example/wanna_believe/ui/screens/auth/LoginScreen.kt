package com.example.wanna_believe.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanna_believe.data.Resource
import com.example.wanna_believe.viewmodel.AuthViewModel

/**
 * Aqui o usuário entra com e-mail e senha pra acessar o app.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    // states para guardar o que o usuário digita nos campos
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Pegamos o estado lá do ViewModel pra saber se o login deu certo, se tá carregando, etc.
    val authState by viewModel.authState.collectAsState()

    // Este efeito "escuta" o authState. Se mudar para Success, a gente avisa que logou.
    LaunchedEffect(authState) {
        if (authState is Resource.Success) {
            onLoginSuccess()
            viewModel.resetState() // Limpa o estado pra não ficar "sucesso" pra sempre
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título principal do app
            Text(
                text = "I want to believe",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
            // Slogan atualizado conforme pedido!
            Text(
                text = "Conecte-se com seus amigos",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            // Campo de e-mail com cantos arredondados de 12.dp
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de senha (esconde o que a gente digita)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Se der erro no login, a gente mostra a mensagem aqui embaixo em vermelho
            if (authState is Resource.Error) {
                Text(
                    text = (authState as Resource.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Enquanto o Firebase tá pensando, a gente mostra uma rodinha de progresso
            if (authState is Resource.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                // O botão de entrar só habilita se os campos não estiverem vazios
                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = email.isNotBlank() && password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Entrar", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Pra quem ainda não tem conta, leva pra tela de cadastro
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    "Não tem uma conta? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Cadastre-se",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
