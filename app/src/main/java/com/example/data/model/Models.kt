package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val coverUrl: String,
    val bio: String,
    val work: String = "Software Engineer at TechCorp",
    val education: String = "Studied Computer Science at Stanford",
    val location: String = "San Francisco, California",
    val friendsCount: Int = 1420,
    val followersCount: Int = 2850
)

enum class ReactionType(val label: String, val emoji: String, val color: Color) {
    LIKE("Like", "👍", ReactionLikeColor),
    LOVE("Love", "❤️", ReactionLoveColor),
    CARE("Care", "🥰", ReactionCareColor),
    HAHA("Haha", "😂", ReactionHahaColor),
    WOW("Wow", "😮", ReactionWowColor),
    SAD("Sad", "😢", ReactionSadColor),
    ANGRY("Angry", "😡", ReactionAngryColor);

    companion object {
        fun fromName(name: String?): ReactionType? {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
        }
    }
}

data class Post(
    val id: String,
    val userId: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val mediaUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val userReaction: ReactionType? = null,
    val isBookmarked: Boolean = false
)

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class Story(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val mediaUrl: String,
    val caption: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false
)

data class Reel(
    val id: String,
    val creatorId: String,
    val creatorName: String,
    val creatorAvatar: String,
    val videoThumbnail: String,
    val caption: String,
    val musicTitle: String = "Original Sound - Trending Audio",
    val likesCount: Int = 1240,
    val commentsCount: Int = 89,
    val sharesCount: Int = 45,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isFollowing: Boolean = false
)

data class WatchVideo(
    val id: String,
    val title: String,
    val channelName: String,
    val channelAvatar: String,
    val thumbnailUrl: String,
    val duration: String,
    val views: String,
    val timeAgo: String,
    val category: String,
    val likesCount: Int = 420,
    val isLiked: Boolean = false,
    val description: String = ""
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val avatarUrl: String,
    val timeAgo: String,
    val isRead: Boolean = false,
    val actionType: String = "LIKE" // LIKE, COMMENT, FRIEND_REQUEST, SHARE
)
