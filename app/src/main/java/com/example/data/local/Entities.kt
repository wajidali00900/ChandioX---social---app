package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Post
import com.example.data.model.ReactionType
import com.example.data.model.Comment
import com.example.data.model.Reel
import com.example.data.model.User

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val mediaUrl: String?,
    val timestamp: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val userReactionName: String?,
    val isBookmarked: Boolean
) {
    fun toPost(): Post = Post(
        id = id,
        userId = userId,
        authorName = authorName,
        authorAvatar = authorAvatar,
        content = content,
        mediaUrl = mediaUrl,
        timestamp = timestamp,
        likesCount = likesCount,
        commentsCount = commentsCount,
        sharesCount = sharesCount,
        userReaction = ReactionType.fromName(userReactionName),
        isBookmarked = isBookmarked
    )

    companion object {
        fun fromPost(post: Post): PostEntity = PostEntity(
            id = post.id,
            userId = post.userId,
            authorName = post.authorName,
            authorAvatar = post.authorAvatar,
            content = post.content,
            mediaUrl = post.mediaUrl,
            timestamp = post.timestamp,
            likesCount = post.likesCount,
            commentsCount = post.commentsCount,
            sharesCount = post.sharesCount,
            userReactionName = post.userReaction?.name,
            isBookmarked = post.isBookmarked
        )
    }
}

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val userId: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: Long,
    val likesCount: Int,
    val isLiked: Boolean
) {
    fun toComment(): Comment = Comment(
        id = id,
        postId = postId,
        userId = userId,
        authorName = authorName,
        authorAvatar = authorAvatar,
        content = content,
        timestamp = timestamp,
        likesCount = likesCount,
        isLiked = isLiked
    )

    companion object {
        fun fromComment(comment: Comment): CommentEntity = CommentEntity(
            id = comment.id,
            postId = comment.postId,
            userId = comment.userId,
            authorName = comment.authorName,
            authorAvatar = comment.authorAvatar,
            content = comment.content,
            timestamp = comment.timestamp,
            likesCount = comment.likesCount,
            isLiked = comment.isLiked
        )
    }
}

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey val id: String,
    val creatorId: String,
    val creatorName: String,
    val creatorAvatar: String,
    val videoThumbnail: String,
    val caption: String,
    val musicTitle: String,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean,
    val isFollowing: Boolean
) {
    fun toReel(): Reel = Reel(
        id = id,
        creatorId = creatorId,
        creatorName = creatorName,
        creatorAvatar = creatorAvatar,
        videoThumbnail = videoThumbnail,
        caption = caption,
        musicTitle = musicTitle,
        likesCount = likesCount,
        commentsCount = commentsCount,
        sharesCount = sharesCount,
        isLiked = isLiked,
        isSaved = isSaved,
        isFollowing = isFollowing
    )

    companion object {
        fun fromReel(reel: Reel): ReelEntity = ReelEntity(
            id = reel.id,
            creatorId = reel.creatorId,
            creatorName = reel.creatorName,
            creatorAvatar = reel.creatorAvatar,
            videoThumbnail = reel.videoThumbnail,
            caption = reel.caption,
            musicTitle = reel.musicTitle,
            likesCount = reel.likesCount,
            commentsCount = reel.commentsCount,
            sharesCount = reel.sharesCount,
            isLiked = reel.isLiked,
            isSaved = reel.isSaved,
            isFollowing = reel.isFollowing
        )
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val coverUrl: String,
    val bio: String,
    val work: String,
    val education: String,
    val location: String,
    val friendsCount: Int,
    val followersCount: Int
) {
    fun toUser(): User = User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        coverUrl = coverUrl,
        bio = bio,
        work = work,
        education = education,
        location = location,
        friendsCount = friendsCount,
        followersCount = followersCount
    )

    companion object {
        fun fromUser(user: User): UserEntity = UserEntity(
            id = user.id,
            name = user.name,
            email = user.email,
            avatarUrl = user.avatarUrl,
            coverUrl = user.coverUrl,
            bio = user.bio,
            work = user.work,
            education = user.education,
            location = user.location,
            friendsCount = user.friendsCount,
            followersCount = user.followersCount
        )
    }
}
