package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.CommentEntity
import com.example.data.local.PostEntity
import com.example.data.local.ReelEntity
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class SocialRepository(
    private val database: AppDatabase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val postDao = database.postDao()
    private val commentDao = database.commentDao()
    private val reelDao = database.reelDao()

    // Stories state
    private val _stories = MutableStateFlow<List<Story>>(getInitialStories())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    // Watch videos state
    private val _watchVideos = MutableStateFlow<List<WatchVideo>>(getInitialWatchVideos())
    val watchVideos: StateFlow<List<WatchVideo>> = _watchVideos.asStateFlow()

    // Notifications state
    private val _notifications = MutableStateFlow<List<NotificationItem>>(getInitialNotifications())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    init {
        coroutineScope.launch {
            seedDatabaseIfEmpty()
        }
    }

    val allPosts: Flow<List<Post>> = postDao.getAllPosts().map { list ->
        list.map { it.toPost() }
    }

    fun getPostsByUserId(userId: String): Flow<List<Post>> = postDao.getPostsByUserId(userId).map { list ->
        list.map { it.toPost() }
    }

    val savedPosts: Flow<List<Post>> = postDao.getSavedPosts().map { list ->
        list.map { it.toPost() }
    }

    val allReels: Flow<List<Reel>> = reelDao.getAllReels().map { list ->
        list.map { it.toReel() }
    }

    fun getCommentsForPost(postId: String): Flow<List<Comment>> = commentDao.getCommentsForPost(postId).map { list ->
        list.map { it.toComment() }
    }

    suspend fun createPost(
        author: User,
        content: String,
        mediaUrl: String? = null
    ) {
        val newPost = PostEntity(
            id = "post_" + UUID.randomUUID().toString(),
            userId = author.id,
            authorName = author.name,
            authorAvatar = author.avatarUrl,
            content = content,
            mediaUrl = mediaUrl,
            timestamp = System.currentTimeMillis(),
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            userReactionName = null,
            isBookmarked = false
        )
        postDao.insertPost(newPost)
    }

    suspend fun reactToPost(post: Post, reaction: ReactionType) {
        val currentReaction = post.userReaction
        val (newReaction, countDelta) = when {
            currentReaction == reaction -> Pair(null, -1) // remove reaction
            currentReaction == null -> Pair(reaction, 1)   // add reaction
            else -> Pair(reaction, 0)                      // change reaction type
        }

        val updated = PostEntity.fromPost(
            post.copy(
                userReaction = newReaction,
                likesCount = (post.likesCount + countDelta).coerceAtLeast(0)
            )
        )
        postDao.updatePost(updated)
    }

    suspend fun toggleBookmark(post: Post) {
        val updated = PostEntity.fromPost(
            post.copy(isBookmarked = !post.isBookmarked)
        )
        postDao.updatePost(updated)
    }

    suspend fun deletePost(postId: String) {
        postDao.deletePost(postId)
    }

    suspend fun sharePost(post: Post, shareAsNewPost: Boolean, currentUser: User) {
        // Increment share count
        val updated = PostEntity.fromPost(
            post.copy(sharesCount = post.sharesCount + 1)
        )
        postDao.updatePost(updated)

        if (shareAsNewPost) {
            val sharedPost = PostEntity(
                id = "post_shared_" + UUID.randomUUID().toString(),
                userId = currentUser.id,
                authorName = currentUser.name,
                authorAvatar = currentUser.avatarUrl,
                content = "Shared ${post.authorName}'s post:\n\n\"${post.content}\"",
                mediaUrl = post.mediaUrl,
                timestamp = System.currentTimeMillis(),
                likesCount = 0,
                commentsCount = 0,
                sharesCount = 0,
                userReactionName = null,
                isBookmarked = false
            )
            postDao.insertPost(sharedPost)
        }
    }

    suspend fun addComment(postId: String, user: User, content: String) {
        val comment = CommentEntity(
            id = "comment_" + UUID.randomUUID().toString(),
            postId = postId,
            userId = user.id,
            authorName = user.name,
            authorAvatar = user.avatarUrl,
            content = content,
            timestamp = System.currentTimeMillis(),
            likesCount = 0,
            isLiked = false
        )
        commentDao.insertComment(comment)

        // Increment commentsCount in post
        val post = postDao.getAllPosts()
        // dao doesn't have direct single get, so we query or handle via update
    }

    suspend fun toggleReelLike(reel: Reel) {
        val updated = ReelEntity.fromReel(
            reel.copy(
                isLiked = !reel.isLiked,
                likesCount = if (reel.isLiked) reel.likesCount - 1 else reel.likesCount + 1
            )
        )
        reelDao.updateReel(updated)
    }

    suspend fun toggleReelSave(reel: Reel) {
        val updated = ReelEntity.fromReel(
            reel.copy(isSaved = !reel.isSaved)
        )
        reelDao.updateReel(updated)
    }

    suspend fun toggleReelFollow(reel: Reel) {
        val updated = ReelEntity.fromReel(
            reel.copy(isFollowing = !reel.isFollowing)
        )
        reelDao.updateReel(updated)
    }

    fun addStory(user: User, mediaUrl: String, caption: String?) {
        val newStory = Story(
            id = "story_" + UUID.randomUUID().toString(),
            userId = user.id,
            userName = user.name,
            userAvatar = user.avatarUrl,
            mediaUrl = mediaUrl,
            caption = caption,
            timestamp = System.currentTimeMillis(),
            isViewed = false
        )
        _stories.value = listOf(newStory) + _stories.value
    }

    fun markStoryViewed(storyId: String) {
        _stories.value = _stories.value.map {
            if (it.id == storyId) it.copy(isViewed = true) else it
        }
    }

    fun toggleWatchVideoLike(videoId: String) {
        _watchVideos.value = _watchVideos.value.map {
            if (it.id == videoId) {
                val newLiked = !it.isLiked
                it.copy(
                    isLiked = newLiked,
                    likesCount = if (newLiked) it.likesCount + 1 else it.likesCount - 1
                )
            } else it
        }
    }

    fun markNotificationRead(notifId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notifId) it.copy(isRead = true) else it
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        if (postDao.getCount() == 0) {
            val seedPosts = listOf(
                PostEntity(
                    id = "post_1",
                    userId = "user_chandiox_official",
                    authorName = "ChandioX Official",
                    authorAvatar = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80",
                    content = "🎉 Welcome to ChandioX Social! Connect with your friends, share reels, stream trending videos, and discover what's happening around the world.\n\nDrop a ❤️ if you are loving the clean new experience!",
                    mediaUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1080&auto=format&fit=crop&q=80",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 35, // 35 mins ago
                    likesCount = 384,
                    commentsCount = 52,
                    sharesCount = 28,
                    userReactionName = "LOVE",
                    isBookmarked = false
                ),
                PostEntity(
                    id = "post_2",
                    userId = "user_ayesha",
                    authorName = "Ayesha Khan",
                    authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                    content = "Golden hour vibes in the northern valleys 🏔️✨ Nature always has the best therapy to offer. What's your favorite travel destination this season?",
                    mediaUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1080&auto=format&fit=crop&q=80",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 120, // 2 hrs ago
                    likesCount = 189,
                    commentsCount = 24,
                    sharesCount = 7,
                    userReactionName = "LIKE",
                    isBookmarked = true
                ),
                PostEntity(
                    id = "post_3",
                    userId = "user_tech_insider",
                    authorName = "Tech Pulse Weekly",
                    authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
                    content = "Artificial Intelligence and Jetpack Compose are reshaping modern software engineering! Are you writing native declarative UIs or still relying on older frameworks? Let's discuss in the comments 👇",
                    mediaUrl = null,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 240, // 4 hrs ago
                    likesCount = 512,
                    commentsCount = 98,
                    sharesCount = 45,
                    userReactionName = null,
                    isBookmarked = false
                ),
                PostEntity(
                    id = "post_4",
                    userId = "user_david",
                    authorName = "David Miller",
                    authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
                    content = "Brewed a fresh cup of artisan Ethiopian roast this morning ☕ Ready to tackle a brand new week of coding. Have a productive week everyone! 🚀",
                    mediaUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=1080&auto=format&fit=crop&q=80",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 600, // 10 hrs ago
                    likesCount = 92,
                    commentsCount = 11,
                    sharesCount = 2,
                    userReactionName = "CARE",
                    isBookmarked = false
                )
            )
            postDao.insertPosts(seedPosts)

            // Seed sample comments
            val seedComments = listOf(
                CommentEntity(
                    id = "c1",
                    postId = "post_1",
                    userId = "user_ayesha",
                    authorName = "Ayesha Khan",
                    authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                    content = "The interface looks so slick and fast! Congratulations team 🎊",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                    likesCount = 14,
                    isLiked = true
                ),
                CommentEntity(
                    id = "c2",
                    postId = "post_1",
                    userId = "user_david",
                    authorName = "David Miller",
                    authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
                    content = "Reels and Watch tabs are super smooth 🔥 Love the dark/blue aesthetics.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
                    likesCount = 8,
                    isLiked = false
                ),
                CommentEntity(
                    id = "c3",
                    postId = "post_2",
                    userId = "user_me_01",
                    authorName = "Zohaib Chandio",
                    authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                    content = "Breathtaking views! Northern areas are truly unmatched 🏔️",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 90,
                    likesCount = 5,
                    isLiked = true
                )
            )
            commentDao.insertComments(seedComments)
        }

        if (reelDao.getCount() == 0) {
            val seedReels = listOf(
                ReelEntity(
                    id = "reel_1",
                    creatorId = "cr_sarah",
                    creatorName = "Sarah Jenkins",
                    creatorAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80",
                    videoThumbnail = "https://images.unsplash.com/photo-1518791841217-8f162f1e1131?w=1080&auto=format&fit=crop&q=80",
                    caption = "When you finally fix that one bug that has been keeping you up all night! 😹👩‍💻 #developer #techlife #codinghumor",
                    musicTitle = "Lofi Study Beats - Code Chill",
                    likesCount = 14320,
                    commentsCount = 684,
                    sharesCount = 1205,
                    isLiked = true,
                    isSaved = false,
                    isFollowing = false
                ),
                ReelEntity(
                    id = "reel_2",
                    creatorId = "cr_ali",
                    creatorName = "Ali Raza Travels",
                    creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                    videoThumbnail = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=1080&auto=format&fit=crop&q=80",
                    caption = "Sunrise above the clouds in Hunza Valley 🌄 You have to see this in person at least once in your life! #wanderlust #nature",
                    musicTitle = "Original Audio - Cinematic Atmosphere",
                    likesCount = 28900,
                    commentsCount = 1410,
                    sharesCount = 3890,
                    isLiked = false,
                    isSaved = true,
                    isFollowing = true
                ),
                ReelEntity(
                    id = "reel_3",
                    creatorId = "cr_chef_mario",
                    creatorName = "Chef Mario Rossi",
                    creatorAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=400&auto=format&fit=crop&q=80",
                    videoThumbnail = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=1080&auto=format&fit=crop&q=80",
                    caption = "Traditional Neapolitan Pizza Margherita in 60 seconds 🍕🔥 Crispy airy crust perfection! #foodie #cooking #pizza",
                    musicTitle = "Italian Mambo Vibes - Accordion Mix",
                    likesCount = 8920,
                    commentsCount = 390,
                    sharesCount = 850,
                    isLiked = false,
                    isSaved = false,
                    isFollowing = false
                ),
                ReelEntity(
                    id = "reel_4",
                    creatorId = "cr_fitness_pro",
                    creatorName = "Maya Fitness",
                    creatorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
                    videoThumbnail = "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=1080&auto=format&fit=crop&q=80",
                    caption = "5-minute morning mobility routine to awaken your spine and posture 🧘‍♀️ No equipment needed! #workout #healthy",
                    musicTitle = "Focus Energy - Motivational Beat",
                    likesCount = 19400,
                    commentsCount = 720,
                    sharesCount = 2400,
                    isLiked = true,
                    isSaved = true,
                    isFollowing = true
                )
            )
            reelDao.insertReels(seedReels)
        }
    }

    private fun getInitialStories(): List<Story> {
        return listOf(
            Story(
                id = "story_1",
                userId = "user_ayesha",
                userName = "Ayesha Khan",
                userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1080&auto=format&fit=crop&q=80",
                caption = "Beach weekend getaway 🏖️🌊",
                isViewed = false
            ),
            Story(
                id = "story_2",
                userId = "user_hamza",
                userName = "Hamza Tariq",
                userAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
                mediaUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=1080&auto=format&fit=crop&q=80",
                caption = "Concert night under the stadium lights 🎸⚡",
                isViewed = false
            ),
            Story(
                id = "story_3",
                userId = "user_fatima",
                userName = "Fatima Noor",
                userAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
                mediaUrl = "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?w=1080&auto=format&fit=crop&q=80",
                caption = "Sunday hiking adventure trails 🌿🎒",
                isViewed = false
            ),
            Story(
                id = "story_4",
                userId = "user_zain",
                userName = "Zain Malik",
                userAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
                mediaUrl = "https://images.unsplash.com/photo-1551218808-94e220e084d2?w=1080&auto=format&fit=crop&q=80",
                caption = "Late night rooftop dinner with team 🍲✨",
                isViewed = true
            )
        )
    }

    private fun getInitialWatchVideos(): List<WatchVideo> {
        return listOf(
            WatchVideo(
                id = "wv_1",
                title = "The Evolution of Mobile Computing: What to Expect in the Next Decade",
                channelName = "Future Tech Studios",
                channelAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
                thumbnailUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=1080&auto=format&fit=crop&q=80",
                duration = "14:22",
                views = "240K views",
                timeAgo = "2 days ago",
                category = "Tech",
                likesCount = 14500,
                isLiked = true,
                description = "In-depth documentary exploring artificial intelligence, spatial computing, and next-generation battery innovations."
            ),
            WatchVideo(
                id = "wv_2",
                title = "World's Top 10 Hidden Natural Wonders Caught on 8K Drones",
                channelName = "Earth Odyssey",
                channelAvatar = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=400&auto=format&fit=crop&q=80",
                thumbnailUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=1080&auto=format&fit=crop&q=80",
                duration = "21:40",
                views = "1.2M views",
                timeAgo = "1 week ago",
                category = "Nature",
                likesCount = 68000,
                isLiked = false,
                description = "Embark on an awe-inspiring aerial journey across hidden glaciers, tropical waterfalls, and desert sand dunes."
            ),
            WatchVideo(
                id = "wv_3",
                title = "Live Championship Finals: Cyber Rush Season 5 Highlights",
                channelName = "Esports Arena",
                channelAvatar = "https://images.unsplash.com/photo-1566492031773-4f4e44671857?w=400&auto=format&fit=crop&q=80",
                thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=1080&auto=format&fit=crop&q=80",
                duration = "35:12",
                views = "890K views",
                timeAgo = "3 hours ago",
                category = "Gaming",
                likesCount = 42300,
                isLiked = false,
                description = "Watch the most electrifying plays, clutch moments, and champion crowning from the Grand Finals."
            ),
            WatchVideo(
                id = "wv_4",
                title = "How to Cook Like a 3-Star Michelin Chef at Home",
                channelName = "Culinary Masterclass",
                channelAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=400&auto=format&fit=crop&q=80",
                thumbnailUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=1080&auto=format&fit=crop&q=80",
                duration = "18:05",
                views = "510K views",
                timeAgo = "5 days ago",
                category = "Food",
                likesCount = 31200,
                isLiked = false,
                description = "Fundamental culinary techniques, pan sauces, and knife skills that will transform your kitchen game."
            )
        )
    }

    private fun getInitialNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(
                id = "notif_1",
                title = "Ayesha Khan loved your photo.",
                message = "\"Golden hour vibes in the northern valleys...\"",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
                timeAgo = "15m",
                isRead = false,
                actionType = "LIKE"
            ),
            NotificationItem(
                id = "notif_2",
                title = "Hamza Tariq sent you a friend request.",
                message = "45 mutual friends including Zohaib and Sarah.",
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
                timeAgo = "1h",
                isRead = false,
                actionType = "FRIEND_REQUEST"
            ),
            NotificationItem(
                id = "notif_3",
                title = "ChandioX Official mentioned you in a comment.",
                message = "\"Welcome to ChandioX Social! Connect with your friends...\"",
                avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=400&auto=format&fit=crop&q=80",
                timeAgo = "3h",
                isRead = true,
                actionType = "COMMENT"
            ),
            NotificationItem(
                id = "notif_4",
                title = "Tech Pulse Weekly published a new trending video.",
                message = "The Evolution of Mobile Computing: What to Expect...",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
                timeAgo = "Yesterday",
                isRead = true,
                actionType = "SHARE"
            )
        )
    }
}
