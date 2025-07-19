package com.example.nufianapp.data.repository

import android.net.Uri
import android.util.Log
import com.example.nufianapp.data.firebase.FireStoreHelper
import com.example.nufianapp.data.firebase.FirebaseModule.firestore
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.model.Response
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStoreHelper: FireStoreHelper,
    private val firebaseMessaging: FirebaseMessaging
) : UserRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun storeUserData(user: User): Response<Boolean> {
        return try {
            fireStoreHelper.storeUserData(user)
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getUserData(): Response<User> {
        val userId = currentUser?.uid ?: return Response.Failure(Exception("User not authenticated"))
        return try {
            val userData = fireStoreHelper.getUserData(userId)
            Response.Success(userData)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun storeFcmToken(uid: String, fcmtoken: String) {
        try {
            Log.d("FCMToken", "Attempting to store token for user: $uid")
            Log.d("FCMToken", "Token value: $fcmtoken")

            firestore.collection("users").document(uid)
                .update("fcmToken", fcmtoken)
                .await()

            Log.d("FCMToken", "Token stored successfully")
        } catch (e: Exception) {
            Log.e("FCMToken", "Error storing token: ${e.message}", e)
            // If this fails, it might be because the document doesn't exist yet
            // Try to create it instead
            try {
                firestore.collection("users").document(uid)
                    .set(mapOf("fcmToken" to fcmtoken), SetOptions.merge())
                    .await()
                Log.d("FCMToken", "Created user document with token")
            } catch (e2: Exception) {
                Log.e("FCMToken", "Failed to create user document: ${e2.message}", e2)
                throw e2
            }
        }
    }

    override suspend fun clearFcmToken(userId: String) {
        firestore.collection("users").document(userId)
            .update("fcmToken", "")
            .await()
    }

    override suspend fun storeAuthToken(uid: String, authToken: String) {
        Firebase.firestore.collection("users")
            .document(uid)
            .update("authToken", authToken)
    }

    override suspend fun getUserById(userId: String): Response<User> {
        return try {
            val userData = fireStoreHelper.getUserData(userId)
            Response.Success(userData)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getUserDataById(userId: String): Response<User?> {
        return try {
            val userData = fireStoreHelper.getUserDataById(userId)
            Response.Success(userData)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun updateUser(user: User): Response<User> {
        return fireStoreHelper.updateUser(user)
    }

    override suspend fun getAllUserTokens(): Response<List<String>> {
        return try {
            val tokens = fireStoreHelper.getAllUserTokens()
            Response.Success(tokens)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getCurrentUserFcmToken(): Response<String> {
        val userId = currentUser?.uid ?: return Response.Failure(Exception("User not authenticated"))
        return getUserFcmToken(userId)
    }

    override suspend fun getUserFcmToken(userId: String): Response<String> {
        return try {
            val token = fireStoreHelper.getUserFcmToken(userId)
            Response.Success(token)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun getUserIdByToken(token: String): Response<String> {
        return try {
            val userId = fireStoreHelper.getUserIdByToken(token)
            Response.Success(userId)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun uploadImage(userId: String, imageUri: Uri): String {
        return fireStoreHelper.uploadImage(userId, imageUri)
    }

    override suspend fun getImageUrl(userId: String): String {
        return fireStoreHelper.getImageUrl(userId)
    }
}