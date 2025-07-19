package com.example.nufianapp.data.repository

import android.util.Log
import com.example.nufianapp.domain.model.ErrorUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.CoroutineScope
import com.example.nufianapp.domain.model.Response.Failure
import com.example.nufianapp.domain.model.Response.Success
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : AuthRepository {
    override val currentUser get() = auth.currentUser

    override suspend fun firebaseSignUpWithEmailAndPassword(
        email: String,
        password: String
    ): SignUpResponse {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()

            // Get and store FCM token for the new user (this will replace existing token if user logs in again)
            auth.currentUser?.let { user ->
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                userRepository.storeFcmToken(user.uid, fcmToken)
            }

            Success(true)
        } catch (e: FirebaseAuthUserCollisionException) {
            Failure(Exception("Email already in use"))
        } catch (e: Exception) {
            Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
        }
    }

    override suspend fun firebaseSignInWithEmailAndPassword(
        email: String, password: String
    ) = try {
        auth.signInWithEmailAndPassword(email, password).await()

        auth.currentUser?.let { user ->
            try {
                // More robust token retrieval (this will replace existing token if user logs in again)
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.d("FCMToken", "Retrieved FCM token successfully: $token")

                        // Launch a coroutine to store the token
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                userRepository.storeFcmToken(user.uid, token)
                                Log.d("FCMToken", "Stored FCM token in Firestore")
                            } catch (e: Exception) {
                                Log.e("FCMToken", "Failed to store token: ${e.message}", e)
                            }
                        }
                    } else {
                        Log.e("FCMToken", "Failed to get FCM token: ${task.exception?.message}")
                    }
                }

                // Continue with auth token logic
                val authToken = user.getIdToken(true).await().token
                if (authToken != null) {
                    userRepository.storeAuthToken(user.uid, authToken)
                } else {

                }
            } catch (e: Exception) {
                Log.e("FCMToken", "Error handling FCM token: ${e.message}", e)
            }
        }

        Success(true)
    } catch (e: Exception) {
        Log.e("FCMToken", "Error during sign in: ${e.message}", e)
        Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
    }


    override suspend fun sendEmailVerification() = try {
        auth.currentUser?.sendEmailVerification()?.await()
        Success(true)
    } catch (e: Exception) {
        Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
    }

    override suspend fun reloadFirebaseUser() = try {
        auth.currentUser?.reload()?.await()
        Success(true)
    } catch (e: Exception) {
        Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
    }

    override suspend fun sendPasswordResetEmail(email: String) = try {
        auth.sendPasswordResetEmail(email).await()
        Success(true)
    } catch (e: Exception) {
        Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
    }

    override fun signOut() {
        val currentUser = auth.currentUser
        currentUser?.let {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    FirebaseMessaging.getInstance().deleteToken().await()
                    Log.d("AuthRepository", "Deleted FCM token from device")
                } catch (e: Exception) {
                    Log.e("AuthRepository", "Failed to delete local FCM token: ${e.message}", e)
                }
            }
        }

        auth.signOut()
    }


    suspend fun updateFcmToken(token: String): com.example.nufianapp.domain.model.Response<Boolean> {
        return try {
            auth.currentUser?.let { user ->
                userRepository.storeFcmToken(user.uid, token)
                Success(true)
            } ?: Failure(Exception("No user logged in"))
        } catch (e: Exception) {
            Failure(Exception("Failed to update FCM token: ${e.message}"))
        }
    }


    override suspend fun revokeAccess() = try {
        auth.currentUser?.delete()?.await()
        Success(true)
    } catch (e: Exception) {
        Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
    }

    override fun getAuthState(viewModelScope: CoroutineScope) = callbackFlow {
        val authStateListener = AuthStateListener { auth ->
            trySend(auth.currentUser == null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), auth.currentUser == null)
}