package com.example.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.User
import com.example.ui.components.FacebookAvatar
import com.example.ui.theme.FbBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(
    currentUser: User,
    onDismiss: () -> Unit,
    onSubmit: (content: String, imageUrl: String?) -> Unit
) {
    var contentText by remember { mutableStateOf("") }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var customImageInput by remember { mutableStateOf("") }
    var showImageInputRow by remember { mutableStateOf(false) }

    val presetImages = listOf(
        "https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=1080&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1080&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=1080&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=1080&auto=format&fit=crop&q=80"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Top header bar: Close, Title, Post Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_create_post_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel post",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Create post",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = {
                            if (contentText.isNotBlank() || selectedImageUrl != null) {
                                onSubmit(contentText, selectedImageUrl)
                                onDismiss()
                            }
                        },
                        enabled = contentText.isNotBlank() || selectedImageUrl != null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FbBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("submit_post_button")
                    ) {
                        Text(text = "POST", fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Author details
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FacebookAvatar(
                            imageUrl = currentUser.avatarUrl,
                            size = 46.dp
                        )

                        Column {
                            Text(
                                text = currentUser.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Privacy pill
                            Row(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = "Public",
                                    modifier = Modifier.size(13.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Public",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Post content text field
                    TextField(
                        value = contentText,
                        onValueChange = { contentText = it },
                        placeholder = {
                            Text(
                                text = "What's on your mind, ${currentUser.name.substringBefore(" ")}?",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp)
                            .testTag("post_content_input")
                    )

                    // Selected Image Preview
                    if (selectedImageUrl != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(selectedImageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Attached photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            IconButton(
                                onClick = { selectedImageUrl = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Choose image presets
                    if (showImageInputRow) {
                        Text(
                            text = "Choose Photo Preset or enter URL:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            items(presetImages) { url ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedImageUrl = url }
                                        .border(
                                            width = if (selectedImageUrl == url) 2.dp else 1.dp,
                                            color = if (selectedImageUrl == url) FbBlue else Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customImageInput,
                                onValueChange = { customImageInput = it },
                                placeholder = { Text("https://example.com/photo.jpg", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (customImageInput.isNotBlank()) {
                                        selectedImageUrl = customImageInput.trim()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FbBlue)
                            ) {
                                Text("Add")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Bottom toolbar: "Add to your post"
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.surfaceVariant
                    ),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Add to your post",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Photo action
                            IconButton(
                                onClick = { showImageInputRow = !showImageInputRow },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Attach photo",
                                    tint = Color(0xFF45BD62),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Tag friends
                            IconButton(
                                onClick = {
                                    if (!contentText.contains("@")) {
                                        contentText += " with @Ayesha and @Hamza"
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "Tag people",
                                    tint = FbBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Feeling
                            IconButton(
                                onClick = {
                                    if (!contentText.contains("feeling")) {
                                        contentText += " — 😊 feeling wonderful"
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mood,
                                    contentDescription = "Feeling/activity",
                                    tint = Color(0xFFF7B125),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Location
                            IconButton(
                                onClick = {
                                    if (!contentText.contains("at")) {
                                        contentText += " 📍 at ChandioX Headquarters"
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "Check in",
                                    tint = Color(0xFFF3425F),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
