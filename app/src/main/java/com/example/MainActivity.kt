package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.auth.AuthManager
import com.example.data.local.AppDatabase
import com.example.data.repository.SocialRepository
import com.example.ui.MainSocialApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = SocialRepository(database)
        val authManager = AuthManager.getInstance(applicationContext)

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainSocialApp(
                        repository = repository,
                        authManager = authManager
                    )
                }
            }
        }
    }
}
