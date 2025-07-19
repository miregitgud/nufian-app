package com.example.nufianapp.data.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

// Firebase Authentication Helper Class
class FirebaseAuthHelper {
    private val auth = FirebaseAuth.getInstance()

    // Sign in with email and password
    suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    // Create a new user
    suspend fun createUser(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw IllegalStateException("User creation failed")
    }

    // Send password reset email
    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    // Send email verification
    suspend fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()?.await()
    }
}