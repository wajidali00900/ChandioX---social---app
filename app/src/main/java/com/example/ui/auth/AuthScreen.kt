package com.example.ui.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthManager
import com.example.ui.theme.FbBlue
import com.example.ui.theme.FbBlueLight

@Composable
fun AuthScreen(
    authManager: AuthManager,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Official ChandioX Logo
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.chandiox_logo),
                contentDescription = "ChandioX Logo",
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "ChandioX",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-1.0).sp,
                color = FbBlue
            )

            Text(
                text = "Connect with friends and the world around you.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // Form Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isSignUp) "Create a new account" else "Log In to ChandioX",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Error Message Banner
                    AnimatedVisibility(visible = errorMessage != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Name (only for Sign Up)
                    if (isSignUp) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                errorMessage = null
                            },
                            label = { Text("Full Name") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = FbBlue)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_field"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("Bio / Headline (Optional)") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = FbBlue)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Mobile number or email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = FbBlue)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_field"),
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = FbBlue)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_field"),
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Primary Submit Button
                    Button(
                        onClick = {
                            if (isSignUp) {
                                val result = authManager.signUp(name, email, password, bio)
                                result.onSuccess {
                                    Toast.makeText(context, "Welcome to ChandioX Social, ${it.name}!", Toast.LENGTH_SHORT).show()
                                    onAuthSuccess()
                                }.onFailure {
                                    errorMessage = it.message
                                }
                            } else {
                                val result = authManager.login(email, password)
                                result.onSuccess {
                                    Toast.makeText(context, "Logged in as ${it.name}", Toast.LENGTH_SHORT).show()
                                    onAuthSuccess()
                                }.onFailure {
                                    errorMessage = it.message
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = FbBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isSignUp) "Sign Up" else "Log In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Demo 1-Click Login for effortless testing
                    OutlinedButton(
                        onClick = {
                            authManager.quickDemoLogin()
                            Toast.makeText(context, "Logged in as Zohaib Chandio (Demo Account)", Toast.LENGTH_SHORT).show()
                            onAuthSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("quick_demo_login_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, FbBlue)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = FbBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Instant 1-Click Demo Login",
                            color = FbBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Toggle between Login and Sign Up
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSignUp) "Already have an account? Log In" else "Don't have an account? Create new account",
                            color = FbBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    isSignUp = !isSignUp
                                    errorMessage = null
                                }
                                .padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
