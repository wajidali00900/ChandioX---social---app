package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.ReactionType
import com.example.ui.theme.*

@Composable
fun FacebookAvatar(
    imageUrl: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isOnline: Boolean = false,
    showStoryRing: Boolean = false,
    contentDescription: String = "User profile picture",
    onClick: (() -> Unit)? = null
) {
    val ringModifier = if (showStoryRing) {
        Modifier
            .size(size + 6.dp)
            .border(
                border = BorderStroke(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(FbBlue, Color(0xFF00C6FF), FbBlueDark)
                    )
                ),
                shape = CircleShape
            )
            .padding(2.5.dp)
    } else {
        Modifier.size(size)
    }

    Box(
        modifier = modifier
            .then(ringModifier)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(size * 0.3f)
                    .align(Alignment.BottomEnd)
                    .background(FbGreenOnline, CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}

@Composable
fun FacebookTopAppBar(
    onSearchClick: () -> Unit,
    onMessengerClick: () -> Unit,
    unreadMessagesCount: Int = 3,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Facebook stylized branding text with the new ChandioX logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.chandiox_logo),
                    contentDescription = "ChandioX Logo",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "chandiox",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-1.2).sp,
                    color = FbBlue,
                    modifier = Modifier.testTag("app_logo_title")
                )
            }

            // Right action icons (Search, Messenger)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search circle button
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .testTag("top_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Messenger circle button with badge
                Box {
                    IconButton(
                        onClick = onMessengerClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .testTag("top_messenger_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "Messenger",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (unreadMessagesCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .background(FbRedBadge, CircleShape)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (unreadMessagesCount > 9) "9+" else unreadMessagesCount.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class NavigationTab(val title: String) {
    FEED("Feed"),
    WATCH("Watch"),
    REELS("Reels"),
    NOTIFICATIONS("Notifications"),
    PROFILE("Profile")
}

@Composable
fun FacebookTabBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    userAvatarUrl: String,
    unreadNotifCount: Int = 2,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FacebookTabItem(
                tab = NavigationTab.FEED,
                selected = selectedTab == NavigationTab.FEED,
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                onSelect = { onTabSelected(NavigationTab.FEED) }
            )

            FacebookTabItem(
                tab = NavigationTab.WATCH,
                selected = selectedTab == NavigationTab.WATCH,
                activeIcon = Icons.Filled.OndemandVideo,
                inactiveIcon = Icons.Outlined.OndemandVideo,
                onSelect = { onTabSelected(NavigationTab.WATCH) }
            )

            FacebookTabItem(
                tab = NavigationTab.REELS,
                selected = selectedTab == NavigationTab.REELS,
                activeIcon = Icons.Filled.Movie,
                inactiveIcon = Icons.Outlined.Movie,
                onSelect = { onTabSelected(NavigationTab.REELS) }
            )

            FacebookTabItem(
                tab = NavigationTab.NOTIFICATIONS,
                selected = selectedTab == NavigationTab.NOTIFICATIONS,
                activeIcon = Icons.Filled.Notifications,
                inactiveIcon = Icons.Outlined.Notifications,
                badgeCount = unreadNotifCount,
                onSelect = { onTabSelected(NavigationTab.NOTIFICATIONS) }
            )

            // Profile / Menu Tab with avatar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clickable { onTabSelected(NavigationTab.PROFILE) }
                    .testTag("tab_profile"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    FacebookAvatar(
                        imageUrl = userAvatarUrl,
                        size = 28.dp,
                        showStoryRing = selectedTab == NavigationTab.PROFILE
                    )
                }

                if (selectedTab == NavigationTab.PROFILE) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(3.dp)
                            .background(FbBlue, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.FacebookTabItem(
    tab: NavigationTab,
    selected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    badgeCount: Int = 0,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onSelect
            )
            .testTag("tab_${tab.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Box {
                Icon(
                    imageVector = if (selected) activeIcon else inactiveIcon,
                    contentDescription = tab.title,
                    tint = if (selected) FbBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )

                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-4).dp)
                            .background(FbRedBadge, CircleShape)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badgeCount.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(3.dp)
                    .background(FbBlue, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun FacebookReactionOverlay(
    onReactionSelected: (ReactionType) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp))
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(32.dp)),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReactionType.entries.forEach { reaction ->
                var isHovered by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(
                    targetValue = if (isHovered) 1.35f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "reaction_scale"
                )

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onReactionSelected(reaction)
                            onDismiss()
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reaction.emoji,
                        fontSize = 26.sp
                    )
                }
            }
        }
    }
}

fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 7 -> "${days}d"
        else -> "${days / 7}w"
    }
}
