package com.example.nufianapp.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nufianapp.main.navigation.ScreenCustom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun SetPasswordScreen(
    onPasswordSet: () -> Unit,
    navController: NavController
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val user = FirebaseAuth.getInstance().currentUser
    val db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Username & Set Password", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it.trim() },
            label = { Text("Unique Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth()
        )

        passwordError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                error = null

                if (username.isBlank()) {
                    error = "Username is required"
                    return@Button
                }

                if (password != confirmPassword) {
                    error = "Passwords do not match"
                    return@Button
                }

                if (user == null) {
                    error = "User not found"
                    return@Button
                }

                if (password.length < 6) {
                    passwordError = "Password must be at least 6 characters"
                    return@Button
                }

                loading = true
                val uid = user.uid
                val email = user.email ?: ""
                val usernamesRef = db.collection("usernames").document(username)

                usernamesRef.get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            loading = false
                            error = "Username already taken"
                        } else {
                            usernamesRef.set(mapOf(
                                "uid" to uid,
                                "email" to email
                            )).addOnSuccessListener {
                                db.collection("users").document(uid).set(
                                    mapOf(
                                        "username" to username,
                                        "uid" to uid,
                                        "email" to email,
                                        "firstLogin" to true
                                    )
                                ).addOnSuccessListener {
                                    user.updatePassword(password)
                                        .addOnSuccessListener {
                                            loading = false
                                            navController.navigate(ScreenCustom.CompleteProfileScreenCustom.route) {
                                                popUpTo(0) // Clear backstack so user can't go back to setup
                                                launchSingleTop = true
                                            }
                                        }

                                        .addOnFailureListener {
                                            loading = false
                                            error = "Failed to set password: ${it.localizedMessage}"
                                        }
                                }.addOnFailureListener {
                                    loading = false
                                    error = "Failed to save user data: ${it.localizedMessage}"
                                }
                            }.addOnFailureListener {
                                loading = false
                                error = "Username already taken or failed to reserve"
                            }
                        }
                    }
                    .addOnFailureListener {
                        loading = false
                        error = "Failed to check username: ${it.localizedMessage}"
                    }
            },
            enabled = username.isNotBlank() &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank() &&
                    !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saving...")
            } else {
                Text("Save")
            }
        }
    }
}
