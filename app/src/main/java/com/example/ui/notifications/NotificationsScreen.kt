package com.example.ui.notifications

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.ui.components.FacebookAvatar
import com.example.ui.theme.FbBlue
import com.example.ui.theme.FbBlueLight
import com.example.ui.theme.ReactionLoveColor

@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    onMarkRead: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Notifications Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 0.5.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Notifications",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = {
                        notifications.forEach { onMarkRead(it.id) }
                        Toast.makeText(context, "All notifications marked as read", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Mark all read",
                        tint = FbBlue
                    )
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notifications right now",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    NotificationRow(
                        item = notif,
                        onClick = { onMarkRead(notif.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationRow(
    item: NotificationItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var isHandledRequest by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (!item.isRead) FbBlueLight.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar with action type indicator badge
            Box {
                FacebookAvatar(
                    imageUrl = item.avatarUrl,
                    size = 52.dp
                )

                val (badgeIcon, badgeColor) = when (item.actionType) {
                    "LIKE" -> Pair(Icons.Default.Favorite, ReactionLoveColor)
                    "COMMENT" -> Pair(Icons.Default.ChatBubble, FbBlue)
                    "FRIEND_REQUEST" -> Pair(Icons.Default.PersonAdd, FbBlue)
                    else -> Pair(Icons.Default.Notifications, Color(0xFFF7B125))
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(22.dp)
                        .background(badgeColor, CircleShape)
                        .padding(3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = badgeIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (item.message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.message,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.timeAgo,
                    fontSize = 12.sp,
                    color = if (!item.isRead) FbBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.Normal
                )

                // Friend Request buttons
                if (item.actionType == "FRIEND_REQUEST" && !isHandledRequest) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                isHandledRequest = true
                                Toast.makeText(context, "Friend request confirmed!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FbBlue),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Confirm", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        FilledTonalButton(
                            onClick = {
                                isHandledRequest = true
                                Toast.makeText(context, "Request removed", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Delete", fontSize = 13.sp)
                        }
                    }
                }
            }

            // Unread blue dot indicator
            if (!item.isRead) {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(8.dp)
                        .background(FbBlue, CircleShape)
                )
            }
        }
    }
}
