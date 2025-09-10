package com.example.nufianapp.domain.repository

import android.net.Uri
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.model.Response
import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    val currentUser: FirebaseUser?
    suspend fun storeUserData(user: User): Response<Boolean>
    suspend fun getUserData(): Response<User>
    suspend fun getUserById(userId: String): Response<User>
    suspend fun getUserDataById(userId: String): Response<User?>
    suspend fun updateUser(user: User): Response<User>
    suspend fun storeFcmToken(uid: String, fcmToken: String)
    suspend fun clearFcmToken(userId: String)
    suspend fun storeAuthToken(uid: String, authToken: String)
    suspend fun getAllUserTokens(): Response<List<String>>
    suspend fun getCurrentUserFcmToken(): Response<String>
    suspend fun getUserFcmToken(userId: String): Response<String>
    suspend fun getUserIdByToken(token: String): Response<String>
    suspend fun uploadImage(userId: String, imageUri: Uri): String
    suspend fun getImageUrl(userId: String): String
}