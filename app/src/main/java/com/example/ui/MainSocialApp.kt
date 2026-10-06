package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.auth.AuthManager
import com.example.data.model.Comment
import com.example.data.model.Post
import com.example.data.model.Story
import com.example.data.repository.SocialRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.components.*
import com.example.ui.feed.CommentsBottomSheet
import com.example.ui.feed.FeedScreen
import com.example.ui.feed.StoryViewerDialog
import com.example.ui.monetization.MonetizationScreen
import com.example.ui.notifications.NotificationsScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.reels.ReelsScreen
import com.example.ui.theme.FbBlue
import com.example.ui.watch.WatchScreen
import kotlinx.coroutines.launch

@Composable
fun MainSocialApp(
    repository: SocialRepository,
    authManager: AuthManager,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isLoggedIn by authManager.isLoggedIn.collectAsStateWithLifecycle()
    val currentUser by authManager.currentUser.collectAsStateWithLifecycle()

    val posts by repository.allPosts.collectAsStateWithLifecycle(initialValue = emptyList())
    val stories by repository.stories.collectAsStateWithLifecycle()
    val watchVideos by repository.watchVideos.collectAsStateWithLifecycle()
    val notifications by repository.notifications.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(NavigationTab.FEED) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Dialog & Sheet States
    var selectedPostForComments by remember { mutableStateOf<Post?>(null) }
    var selectedStoryIndex by remember { mutableStateOf<Int?>(null) }
    var showAddStoryDialog by remember { mutableStateOf(false) }
    var showMessengerDialog by remember { mutableStateOf(false) }
    var showMonetizationScreen by remember { mutableStateOf(false) }

    if (!isLoggedIn || currentUser == null) {
        AuthScreen(
            authManager = authManager,
            onAuthSuccess = { currentTab = NavigationTab.FEED }
        )
        return
    }

    val user = currentUser!!

    if (showMonetizationScreen) {
        MonetizationScreen(
            user = user,
            onBack = { showMonetizationScreen = false }
        )
        return
    }
    val userPosts = remember(posts, user.id) {
        posts.filter { it.userId == user.id }
    }
    val unreadNotifs = remember(notifications) {
        notifications.count { !it.isRead }
    }

    // Observe comments for active post
    val currentPostComments by produceState<List<Comment>>(
        initialValue = emptyList(),
        key1 = selectedPostForComments?.id
    ) {
        selectedPostForComments?.let { post ->
            repository.getCommentsForPost(post.id).collect { list ->
                value = list
            }
        } ?: run {
            value = emptyList()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isSearchActive) {
                    // Search Bar Header
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = {
                                isSearchActive = false
                                searchQuery = ""
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Close search")
                            }

                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search ChandioX...", fontSize = 15.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("global_search_input"),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent
                                ),
                                singleLine = true
                            )

                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search text")
                                }
                            }
                        }
                    }
                } else {
                    // Facebook Top Bar
                    FacebookTopAppBar(
                        onSearchClick = { isSearchActive = true },
                        onMessengerClick = { showMessengerDialog = true },
                        unreadMessagesCount = 2
                    )
                }

                // Facebook 5-Tab Navigation Bar
                FacebookTabBar(
                    selectedTab = currentTab,
                    onTabSelected = { currentTab = it },
                    userAvatarUrl = user.avatarUrl,
                    unreadNotifCount = unreadNotifs
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.FEED -> {
                    FeedScreen(
                        posts = posts,
                        stories = stories,
                        currentUser = user,
                        searchQuery = searchQuery,
                        onCreatePost = { text, media ->
                            coroutineScope.launch {
                                repository.createPost(user, text, media)
                                Toast.makeText(context, "Post shared successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onReactToPost = { post, reaction ->
                            coroutineScope.launch {
                                repository.reactToPost(post, reaction)
                            }
                        },
                        onToggleBookmark = { post ->
                            coroutineScope.launch {
                                repository.toggleBookmark(post)
                                val msg = if (!post.isBookmarked) "Saved to your bookmarks" else "Removed from bookmarks"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDeletePost = { postId ->
                            coroutineScope.launch {
                                repository.deletePost(postId)
                                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onSharePost = { post, repost ->
                            coroutineScope.launch {
                                repository.sharePost(post, repost, user)
                            }
                        },
                        onOpenComments = { post ->
                            selectedPostForComments = post
                        },
                        onOpenStory = { index ->
                            selectedStoryIndex = index
                        },
                        onAddStory = {
                            showAddStoryDialog = true
                        }
                    )
                }

                NavigationTab.WATCH -> {
                    WatchScreen(
                        videos = watchVideos,
                        onToggleLike = { videoId ->
                            repository.toggleWatchVideoLike(videoId)
                        }
                    )
                }

                NavigationTab.REELS -> {
                    val reels by repository.allReels.collectAsStateWithLifecycle(initialValue = emptyList())
                    ReelsScreen(
                        reels = reels,
                        currentUser = user,
                        onToggleLike = { reel ->
                            coroutineScope.launch {
                                repository.toggleReelLike(reel)
                            }
                        },
                        onToggleSave = { reel ->
                            coroutineScope.launch {
                                repository.toggleReelSave(reel)
                            }
                        },
                        onToggleFollow = { reel ->
                            coroutineScope.launch {
                                repository.toggleReelFollow(reel)
                                val msg = if (!reel.isFollowing) "Following ${reel.creatorName}" else "Unfollowed ${reel.creatorName}"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                NavigationTab.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onMarkRead = { notifId ->
                            repository.markNotificationRead(notifId)
                        }
                    )
                }

                NavigationTab.PROFILE -> {
                    ProfileScreen(
                        user = user,
                        userPosts = userPosts,
                        onUpdateProfile = { name, bio, work, edu, loc, av, cov ->
                            authManager.updateProfile(name, bio, work, edu, loc, av, cov)
                        },
                        onCreatePost = { text, media ->
                            coroutineScope.launch {
                                repository.createPost(user, text, media)
                                Toast.makeText(context, "Post created!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onReactToPost = { post, reaction ->
                            coroutineScope.launch {
                                repository.reactToPost(post, reaction)
                            }
                        },
                        onToggleBookmark = { post ->
                            coroutineScope.launch {
                                repository.toggleBookmark(post)
                            }
                        },
                        onDeletePost = { postId ->
                            coroutineScope.launch {
                                repository.deletePost(postId)
                                Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onOpenComments = { post ->
                            selectedPostForComments = post
                        },
                        onOpenMonetization = {
                            showMonetizationScreen = true
                        },
                        onLogout = {
                            authManager.logout()
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Comments Bottom Sheet
    if (selectedPostForComments != null) {
        val targetPost = selectedPostForComments!!
        CommentsBottomSheet(
            post = targetPost,
            comments = currentPostComments,
            currentUser = user,
            onDismiss = { selectedPostForComments = null },
            onAddComment = { commentContent ->
                coroutineScope.launch {
                    repository.addComment(targetPost.id, user, commentContent)
                }
            }
        )
    }

    // Story Viewer Dialog
    if (selectedStoryIndex != null) {
        StoryViewerDialog(
            stories = stories,
            initialIndex = selectedStoryIndex!!,
            onDismiss = { selectedStoryIndex = null },
            onStoryCompleted = { storyId ->
                repository.markStoryViewed(storyId)
            }
        )
    }

    // Create Story Dialog
    if (showAddStoryDialog) {
        var storyCaption by remember { mutableStateOf("") }
        val storyPresets = listOf(
            "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1080&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1080&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?w=1080&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=1080&auto=format&fit=crop&q=80"
        )
        var selectedStoryUrl by remember { mutableStateOf(storyPresets.first()) }

        AlertDialog(
            onDismissRequest = { showAddStoryDialog = false },
            title = { Text("Create a Story") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select a photo for your 24h story:", fontSize = 13.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        storyPresets.forEach { url ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                                    .clickable { selectedStoryUrl = url }
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            ) {
                                coil.compose.AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (selectedStoryUrl == url) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(FbBlue.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = storyCaption,
                        onValueChange = { storyCaption = it },
                        label = { Text("Add a caption...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.addStory(user, selectedStoryUrl, storyCaption.ifBlank { null })
                        showAddStoryDialog = false
                        Toast.makeText(context, "Story published!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FbBlue)
                ) {
                    Text("Share to Story")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Messenger Dialog
    if (showMessengerDialog) {
        val sampleChats = listOf(
            Triple("Ayesha Khan", "Hey Zohaib! Are you joining the meetup?", "12m"),
            Triple("Hamza Tariq", "Loved your latest tech post 🔥", "1h"),
            Triple("ChandioX Community", "New group discussion starting soon!", "3h")
        )

        AlertDialog(
            onDismissRequest = { showMessengerDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.ChatBubble, contentDescription = null, tint = FbBlue)
                    Text("Chats & Messenger")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    sampleChats.forEach { (sender, msg, time) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    Toast.makeText(context, "Opening conversation with $sender", Toast.LENGTH_SHORT).show()
                                    showMessengerDialog = false
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(FbBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sender.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(sender, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(msg, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            }

                            Text(time, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showMessengerDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FbBlue)
                ) {
                    Text("Close")
                }
            }
        )
    }
}
