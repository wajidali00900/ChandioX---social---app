package com.example.ui.reels

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Reel
import com.example.data.model.User
import com.example.ui.components.FacebookAvatar
import com.example.ui.theme.FbBlue
import com.example.ui.theme.ReactionLoveColor

@Composable
fun ReelsScreen(
    reels: List<Reel>,
    currentUser: User,
    onToggleLike: (Reel) -> Unit,
    onToggleSave: (Reel) -> Unit,
    onToggleFollow: (Reel) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (reels.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No reels available", color = Color.White)
            }
        } else {
            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(reels, key = { _, reel -> reel.id }) { index, reel ->
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                    ) {
                        SingleReelItem(
                            reel = reel,
                            onToggleLike = { onToggleLike(reel) },
                            onToggleSave = {
                                onToggleSave(reel)
                                val msg = if (!reel.isSaved) "Saved to Reels collection" else "Removed from saved"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            onToggleFollow = { onToggleFollow(reel) },
                            onShare = {
                                Toast.makeText(context, "Reel link copied!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Top Reel bar: "Reels", Camera icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Reels",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )

            IconButton(
                onClick = {
                    Toast.makeText(context, "Camera opened to record new Reel!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(38.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Create Reel",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun SingleReelItem(
    reel: Reel,
    onToggleLike: () -> Unit,
    onToggleSave: () -> Unit,
    onToggleFollow: () -> Unit,
    onShare: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showPauseOverlay by remember { mutableStateOf(false) }

    // Vinyl spinning rotation
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPlaying = !isPlaying
                showPauseOverlay = true
            }
    ) {
        // Fullscreen visual thumbnail / simulated video
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(reel.videoThumbnail)
                .crossfade(true)
                .build(),
            contentDescription = "Reel video content",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient shade at top and bottom for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Center play/pause indicator overlay
        LaunchedEffect(showPauseOverlay) {
            if (showPauseOverlay) {
                kotlinx.coroutines.delay(800)
                showPauseOverlay = false
            }
        }

        AnimatedVisibility(
            visible = showPauseOverlay || !isPlaying,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Right side floating action buttons (Like, Comment, Share, Save, Music Disc)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Like Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onToggleLike,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (reel.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like reel",
                        tint = if (reel.isLiked) ReactionLoveColor else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = reel.likesCount.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Comment Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { /* Comments dialog */ },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChatBubble,
                        contentDescription = "Comments",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = reel.commentsCount.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Share Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = reel.sharesCount.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Bookmark / Save
            IconButton(
                onClick = onToggleSave,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = if (reel.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Save reel",
                    tint = if (reel.isSaved) Color(0xFFF7B125) else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Spinning Vinyl Disc
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .rotate(if (isPlaying) rotation else 0f)
                    .background(Color.Black, CircleShape)
                    .border(2.dp, Color.DarkGray, CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        // Bottom Left details (Creator info, Caption, Music title)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Creator Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FacebookAvatar(
                    imageUrl = reel.creatorAvatar,
                    size = 38.dp
                )

                Text(
                    text = reel.creatorName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                // Follow button pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (reel.isFollowing) Color.White.copy(alpha = 0.2f) else FbBlue
                        )
                        .clickable(onClick = onToggleFollow)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (reel.isFollowing) "Following" else "Follow",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Caption
            Text(
                text = reel.caption,
                color = Color.White,
                fontSize = 13.sp,
                maxLines = 3,
                lineHeight = 18.sp
            )

            // Music track pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = reel.musicTitle,
                    color = Color.White,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}
