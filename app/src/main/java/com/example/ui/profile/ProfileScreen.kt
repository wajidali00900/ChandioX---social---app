package com.example.ui.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Post
import com.example.data.model.ReactionType
import com.example.data.model.User
import com.example.ui.components.FacebookAvatar
import com.example.ui.feed.CreatePostBar
import com.example.ui.feed.CreatePostDialog
import com.example.ui.feed.PostCard
import com.example.ui.theme.FbBlue
import com.example.ui.theme.FbSecondaryButtonBg

@Composable
fun ProfileScreen(
    user: User,
    userPosts: List<Post>,
    onUpdateProfile: (name: String, bio: String, work: String, education: String, location: String, avatar: String, cover: String) -> Unit,
    onCreatePost: (String, String?) -> Unit,
    onReactToPost: (Post, ReactionType) -> Unit,
    onToggleBookmark: (Post) -> Unit,
    onDeletePost: (String) -> Unit,
    onOpenComments: (Post) -> Unit,
    onOpenMonetization: () -> Unit = {},
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sampleFriends = listOf(
        Pair("Ayesha Khan", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80"),
        Pair("Hamza Tariq", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80"),
        Pair("Sarah Jenkins", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80"),
        Pair("David Miller", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80"),
        Pair("Fatima Noor", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80"),
        Pair("Ali Raza", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Profile Header Container (Cover, Avatar, Name, Bio, Buttons)
        item(key = "profile_header") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 0.5.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Cover Photo & Profile Pic Stack
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                    ) {
                        // Cover Photo
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user.coverUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Cover photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )

                        // Camera button on cover photo
                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 130.dp, end = 12.dp)
                                .size(36.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Edit cover photo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Big Profile Picture
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 16.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user.avatarUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = user.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )

                            // Camera badge on avatar
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (-4).dp, y = (-4).dp)
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                    .clickable { showEditProfileDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Edit profile photo",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // User Info Details
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            text = user.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (user.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.bio,
                                fontSize = 14.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Profile Actions Row: "+ Add to story", "Edit profile", "..."
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Adding photo to story...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = FbBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add to story", fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit profile", fontWeight = FontWeight.Bold)
                            }

                            Box {
                                IconButton(
                                    onClick = { showMenu = true },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                        .size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreHoriz,
                                        contentDescription = "Profile settings",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Monetization & Payouts") },
                                        leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = FbBlue) },
                                        onClick = {
                                            showMenu = false
                                            onOpenMonetization()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Activity Log") },
                                        leadingIcon = { Icon(Icons.Default.List, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            Toast.makeText(context, "Activity Log opened", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Privacy Shortcuts") },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            Toast.makeText(context, "Privacy shortcuts", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Log Out", color = MaterialTheme.colorScheme.error) },
                                        leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            showMenu = false
                                            showLogoutConfirm = true
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Professional Dashboard / Monetization Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onOpenMonetization)
                                .testTag("monetization_dashboard_button"),
                            color = FbBlue.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, FbBlue.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(FbBlue, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MonetizationOn,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Professional Dashboard",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Monetization: 1k followers, 10k likes, 10k views, 10k plays",
                                            fontSize = 11.sp,
                                            color = FbBlue,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = FbBlue
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                        // Details Section (Work, Education, Location, Followers)
                        Text(
                            text = "Details",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        DetailItem(icon = Icons.Default.Work, text = user.work)
                        DetailItem(icon = Icons.Default.School, text = user.education)
                        DetailItem(icon = Icons.Default.LocationOn, text = "Lives in ${user.location}")
                        DetailItem(icon = Icons.Default.RssFeed, text = "Followed by ${user.followersCount} people")

                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                        // Friends Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Friends",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${user.friendsCount} friends",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            TextButton(onClick = { Toast.makeText(context, "Friends list", Toast.LENGTH_SHORT).show() }) {
                                Text("Find Friends", color = FbBlue, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Friends 3x2 Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sampleFriends.take(3).forEach { (friendName, avatar) ->
                                FriendGridTile(name = friendName, avatar = avatar, modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sampleFriends.drop(3).take(3).forEach { (friendName, avatar) ->
                                FriendGridTile(name = friendName, avatar = avatar, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Create Post bar for Profile
        item(key = "profile_create_post") {
            CreatePostBar(
                currentUser = user,
                onClick = { showCreatePostDialog = true }
            )
        }

        // Section header: Posts
        item(key = "posts_header") {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Posts",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { /* Filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Filters")
                    }
                }
            }
        }

        // User Posts List
        if (userPosts.isEmpty()) {
            item(key = "no_user_posts") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No posts yet. Share your thoughts!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(userPosts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    isCurrentUserAuthor = true,
                    onReact = { reaction -> onReactToPost(post, reaction) },
                    onCommentClick = { onOpenComments(post) },
                    onShareClick = {
                        Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                    },
                    onBookmarkClick = { onToggleBookmark(post) },
                    onDeleteClick = { onDeletePost(post.id) }
                )
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, bio, work, education, loc, av, cov ->
                onUpdateProfile(name, bio, work, education, loc, av, cov)
                showEditProfileDialog = false
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Create Post Dialog
    if (showCreatePostDialog) {
        CreatePostDialog(
            currentUser = user,
            onDismiss = { showCreatePostDialog = false },
            onSubmit = { content, media ->
                onCreatePost(content, media)
                showCreatePostDialog = false
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out of ChandioX Social?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailItem(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FriendGridTile(name: String, avatar: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = avatar,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (name: String, bio: String, work: String, education: String, location: String, avatar: String, cover: String) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var bio by remember { mutableStateOf(user.bio) }
    var work by remember { mutableStateOf(user.work) }
    var education by remember { mutableStateOf(user.education) }
    var location by remember { mutableStateOf(user.location) }
    var avatar by remember { mutableStateOf(user.avatarUrl) }
    var cover by remember { mutableStateOf(user.coverUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = work,
                    onValueChange = { work = it },
                    label = { Text("Work / Company") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = education,
                    onValueChange = { education = it },
                    label = { Text("Education / College") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / City") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(name, bio, work, education, location, avatar, cover)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FbBlue)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
