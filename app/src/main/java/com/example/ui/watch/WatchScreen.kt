package com.example.ui.watch

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.WatchVideo
import com.example.ui.components.FacebookAvatar
import com.example.ui.theme.FbBlue
import com.example.ui.theme.ReactionLoveColor

@Composable
fun WatchScreen(
    videos: List<WatchVideo>,
    onToggleLike: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Tech", "Gaming", "Nature", "Food", "Live", "Music")

    val filteredVideos = remember(videos, selectedCategory) {
        if (selectedCategory == "All") {
            videos
        } else {
            videos.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Watch Header & Category Filter
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 0.5.dp
        ) {
            Column(modifier = Modifier.padding(top = 10.dp, bottom = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Watch",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { /* Search videos */ },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search videos",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) FbBlue else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Video Feed
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredVideos, key = { it.id }) { video ->
                WatchVideoCard(
                    video = video,
                    onToggleLike = { onToggleLike(video.id) }
                )
            }
        }
    }
}

@Composable
fun WatchVideoCard(
    video: WatchVideo,
    onToggleLike: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0.2f) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.5.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Video Thumbnail & Player container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.Black)
                    .clickable { isPlaying = !isPlaying }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(video.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Duration badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.duration,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Play / Pause central button
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(52.dp)
                        .background(
                            if (isPlaying) Color.Black.copy(alpha = 0.5f) else FbBlue.copy(alpha = 0.9f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/pause video",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Interactive scrubber bar when playing
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color.White.copy(alpha = 0.3f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(playbackProgress)
                                .fillMaxHeight()
                                .background(Color.Red)
                        )
                    }
                }
            }

            // Video Details Row
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    FacebookAvatar(
                        imageUrl = video.channelAvatar,
                        size = 38.dp
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            lineHeight = 20.sp,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = video.channelName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(text = "•", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = video.views,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(text = "•", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = video.timeAgo,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            isSaved = !isSaved
                            val msg = if (isSaved) "Added to Watch Later" else "Removed from Watch Later"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save video",
                            tint = if (isSaved) Color(0xFFF7B125) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(top = 10.dp, bottom = 6.dp))

                // Video Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like action
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onToggleLike)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (video.isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = "Like",
                            tint = if (video.isLiked) FbBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = video.likesCount.toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (video.isLiked) FbBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Comment action
                    Row(
                        modifier = Modifier
                            .clickable {
                                Toast.makeText(context, "Opening comments...", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Comment",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Share action
                    Row(
                        modifier = Modifier
                            .clickable {
                                Toast.makeText(context, "Video link copied!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Share",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
