package com.example.wanna_believe.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wanna_believe.R
import com.example.wanna_believe.data.model.Post

@Composable
fun PostItem(
    post: Post,
    currentUserId: String?,
    onLikeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCommentsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLiked = post.likedBy.contains(currentUserId)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = post.authorPhotoUrl.ifEmpty { R.drawable.ic_launcher_foreground },
                    contentDescription = "Foto do autor",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                if (post.authorUid == currentUserId) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Deletar",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            if (post.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = post.description,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${post.likeCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = onCommentsClick) {
                    Icon(
                        Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comentários",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
