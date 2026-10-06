package com.example.ui.feed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Story
import com.example.ui.components.FacebookAvatar
import com.example.ui.components.formatTimeAgo
import com.example.ui.theme.ReactionLoveColor
import kotlinx.coroutines.delay

@Composable
fun StoryViewerDialog(
    stories: List<Story>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit,
    onStoryCompleted: (String) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(initialIndex) }
    val currentStory = stories.getOrNull(currentIndex) ?: run {
        onDismiss()
        return
    }

    val progress = remember(currentIndex) { Animatable(0f) }

    LaunchedEffect(currentIndex) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5000, easing = LinearEasing)
        )
        onStoryCompleted(currentStory.id)
        if (currentIndex < stories.lastIndex) {
            currentIndex++
        } else {
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Story background image
            AsyncImage(
                model = currentStory.mediaUrl,
                contentDescription = "Story visual",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Top gradient shade
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                        )
                    )
            )

            // Bottom gradient shade
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            // Left & Right tap zones for navigation
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (currentIndex > 0) currentIndex--
                        }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (currentIndex < stories.lastIndex) currentIndex++ else onDismiss()
                        }
                )
            }

            // Top Story Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp, start = 16.dp, end = 16.dp)
            ) {
                // Multi-segment progress indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    stories.forEachIndexed { index, _ ->
                        val itemProgress = when {
                            index < currentIndex -> 1f
                            index == currentIndex -> progress.value
                            else -> 0f
                        }
                        LinearProgressIndicator(
                            progress = { itemProgress },
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.35f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Author Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FacebookAvatar(
                            imageUrl = currentStory.userAvatar,
                            size = 36.dp
                        )
                        Column {
                            Text(
                                text = currentStory.userName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = formatTimeAgo(currentStory.timestamp),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_story_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close story",
                            tint = Color.White
                        )
                    }
                }
            }

            // Caption overlay
            if (!currentStory.caption.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, end = 20.dp, bottom = 80.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = currentStory.caption,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Bottom reply bar
            var replyMessage by remember { mutableStateOf("") }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = replyMessage,
                    onValueChange = { replyMessage = it },
                    placeholder = { Text("Send message...", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White.copy(alpha = 0.8f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                IconButton(
                    onClick = { /* React with Love */ },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Love reaction",
                        tint = ReactionLoveColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
