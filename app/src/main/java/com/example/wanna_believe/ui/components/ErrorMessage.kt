package com.example.wanna_believe.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Componente de tratamento de erro amigável (item extra sugerido no PDF, seção 5).
 */
@Composable
fun ErrorMessage(message: String, onRetry: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        if (onRetry != null) {
            Button(onClick = onRetry) { Text("Tentar novamente") }
        }
    }
}
