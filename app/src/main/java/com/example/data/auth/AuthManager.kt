package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AuthManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("chandiox_auth", Context.MODE_PRIVATE)

    // Demo primary account
    val defaultUser = User(
        id = "user_me_01",
        name = "Zohaib Chandio",
        email = "zohaib.chandio@chandiox.com",
        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
        coverUrl = "https://images.unsplash.com/photo-1707343843437-caacff5cfa74?w=1200&auto=format&fit=crop&q=80",
        bio = "Founder & Creator of ChandioX Social 🌐 | Building impactful digital experiences. Connect with me!",
        work = "Lead Engineer at ChandioX Tech",
        education = "Software Engineering Graduate",
        location = "Hyderabad / Silicon Valley",
        friendsCount = 1845,
        followersCount = 4210
    )

    private val _currentUser = MutableStateFlow<User?>(loadUserFromPrefs() ?: defaultUser)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", true))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        if (_currentUser.value == null) {
            _currentUser.value = defaultUser
        }
    }

    private fun loadUserFromPrefs(): User? {
        val id = prefs.getString("user_id", null) ?: return null
        val name = prefs.getString("user_name", "Zohaib Chandio") ?: "Zohaib Chandio"
        val email = prefs.getString("user_email", "user@chandiox.com") ?: "user@chandiox.com"
        val avatar = prefs.getString("user_avatar", defaultUser.avatarUrl) ?: defaultUser.avatarUrl
        val cover = prefs.getString("user_cover", defaultUser.coverUrl) ?: defaultUser.coverUrl
        val bio = prefs.getString("user_bio", defaultUser.bio) ?: defaultUser.bio
        val work = prefs.getString("user_work", defaultUser.work) ?: defaultUser.work
        val education = prefs.getString("user_education", defaultUser.education) ?: defaultUser.education
        val location = prefs.getString("user_location", defaultUser.location) ?: defaultUser.location
        val friends = prefs.getInt("user_friends", defaultUser.friendsCount)
        val followers = prefs.getInt("user_followers", defaultUser.followersCount)

        return User(
            id = id,
            name = name,
            email = email,
            avatarUrl = avatar,
            coverUrl = cover,
            bio = bio,
            work = work,
            education = education,
            location = location,
            friendsCount = friends,
            followersCount = followers
        )
    }

    private fun saveUserToPrefs(user: User) {
        prefs.edit().apply {
            putString("user_id", user.id)
            putString("user_name", user.name)
            putString("user_email", user.email)
            putString("user_avatar", user.avatarUrl)
            putString("user_cover", user.coverUrl)
            putString("user_bio", user.bio)
            putString("user_work", user.work)
            putString("user_education", user.education)
            putString("user_location", user.location)
            putInt("user_friends", user.friendsCount)
            putInt("user_followers", user.followersCount)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun login(email: String, pass: String): Result<User> {
        if (email.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("Please enter email and password"))
        }
        if (pass.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }

        val name = email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
        val user = _currentUser.value?.copy(email = email, name = name) ?: defaultUser.copy(
            id = UUID.randomUUID().toString(),
            email = email,
            name = name
        )
        _currentUser.value = user
        _isLoggedIn.value = true
        saveUserToPrefs(user)
        return Result.success(user)
    }

    fun signUp(name: String, email: String, pass: String, bio: String): Result<User> {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            return Result.failure(Exception("All fields are required"))
        }
        if (!email.contains("@") || !email.contains(".")) {
            return Result.failure(Exception("Please enter a valid email address"))
        }
        if (pass.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }

        val newUser = User(
            id = "user_" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            email = email.trim(),
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
            coverUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1200&auto=format&fit=crop&q=80",
            bio = if (bio.isNotBlank()) bio.trim() else "Hey there! I am using ChandioX Social.",
            work = "Works at Tech Innovators",
            education = "Higher Studies",
            location = "Islamabad, PK",
            friendsCount = 12,
            followersCount = 45
        )
        _currentUser.value = newUser
        _isLoggedIn.value = true
        saveUserToPrefs(newUser)
        return Result.success(newUser)
    }

    fun quickDemoLogin() {
        val user = defaultUser
        _currentUser.value = user
        _isLoggedIn.value = true
        saveUserToPrefs(user)
    }

    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
        _isLoggedIn.value = false
    }

    fun updateProfile(
        name: String,
        bio: String,
        work: String,
        education: String,
        location: String,
        avatarUrl: String,
        coverUrl: String
    ) {
        val current = _currentUser.value ?: defaultUser
        val updated = current.copy(
            name = name,
            bio = bio,
            work = work,
            education = education,
            location = location,
            avatarUrl = avatarUrl,
            coverUrl = coverUrl
        )
        _currentUser.value = updated
        saveUserToPrefs(updated)
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthManager? = null

        fun getInstance(context: Context): AuthManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
