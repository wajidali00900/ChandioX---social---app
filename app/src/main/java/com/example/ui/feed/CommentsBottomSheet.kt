package com.example.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Comment
import com.example.data.model.Post
import com.example.data.model.User
import com.example.ui.components.FacebookAvatar
import com.example.ui.components.formatTimeAgo
import com.example.ui.theme.FbBlue
import com.example.ui.theme.ReactionLoveColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    post: Post,
    comments: List<Comment>,
    currentUser: User,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val quickEmojis = listOf("❤️", "🔥", "🙌", "👏", "😂", "💯", "😍")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Comments (${comments.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close comments",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider()

            // Comments List
            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No comments yet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Be the first to comment on ${post.authorName}'s post!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(comments, key = { it.id }) { comment ->
                        CommentItemRow(comment = comment)
                    }
                }
            }

            // Quick emoji row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickEmojis) { emoji ->
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                            .clickable {
                                commentText += emoji
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 16.sp)
                    }
                }
            }

            // Bottom Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FacebookAvatar(
                    imageUrl = currentUser.avatarUrl,
                    size = 36.dp
                )

                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text("Write a comment...", fontSize = 14.sp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comment_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    maxLines = 4
                )

                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onAddComment(commentText.trim())
                            commentText = ""
                        }
                    },
                    enabled = commentText.isNotBlank(),
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (commentText.isNotBlank()) FbBlue else Color.LightGray.copy(alpha = 0.4f),
                            CircleShape
                        )
                        .testTag("send_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send comment",
                        tint = if (commentText.isNotBlank()) Color.White else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItemRow(comment: Comment) {
    var isLiked by remember { mutableStateOf(comment.isLiked) }
    var likesCount by remember { mutableStateOf(comment.likesCount) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        FacebookAvatar(
            imageUrl = comment.authorAvatar,
            size = 36.dp
        )

        Column(modifier = Modifier.weight(1f)) {
            // Facebook gray bubble
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = comment.authorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = comment.content,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Bottom metadata & like row
            Row(
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimeAgo(comment.timestamp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = if (isLiked) "Liked" else "Like",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLiked) FbBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable {
                        isLiked = !isLiked
                        likesCount = if (isLiked) likesCount + 1 else likesCount - 1
                    }
                )

                Text(
                    text = "Reply",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (likesCount > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = ReactionLoveColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = likesCount.toString(),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
