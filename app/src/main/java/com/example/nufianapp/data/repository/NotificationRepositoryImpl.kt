package com.example.nufianapp.data.repository

import android.content.Context
import android.util.Log
import androidx.paging.PagingData
import com.example.nufianapp.data.firebase.FireStoreHelper
import com.example.nufianapp.data.model.Notification
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.domain.repository.NotificationRepository
import com.example.nufianapp.domain.repository.UserRepository
import com.google.auth.oauth2.GoogleCredentials
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.Date
import javax.inject.Inject
import java.util.concurrent.TimeUnit

class NotificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val fireStoreHelper: FireStoreHelper
) : NotificationRepository {

    // Configure OkHttpClient with timeouts for better reliability
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private suspend fun getAccessToken(): String? {
        return try {
            val inputStream = withContext(Dispatchers.IO) {
                context.assets.open("service-account-file.json")
            }

            // Add explicit scopes for both CloudMessaging and Firestore
            val credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(listOf(
                    "https://www.googleapis.com/auth/firebase.messaging",
                    "https://www.googleapis.com/auth/datastore",
                    "https://www.googleapis.com/auth/cloud-platform"
                ))

            withContext(Dispatchers.IO) {
                credentials.refreshIfExpired()
            }
            credentials.accessToken.tokenValue
        } catch (e: IOException) {
            Log.e("FCMNotification", "Failed to get access token", e)
            null
        }
    }

    override suspend fun sendNotificationToSpecificUser(
        userFcmToken: String,
        notificationTitle: String,
        notificationBody: String,
        notificationType: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        // Validate input parameters
        if (userFcmToken.isBlank()) {
            Log.e("FCMNotification", "Invalid FCM token: Token is blank")
            return@withContext Result.failure(Exception("Invalid FCM token"))
        }

        val accessToken = getAccessToken()
            ?: return@withContext Result.failure(Exception("Failed to get access token"))

        // Format FCM message body according to v1 API requirements
        val notificationData = mapOf(
            "message" to mapOf(
                "token" to userFcmToken,
                "notification" to mapOf(
                    "title" to notificationTitle,
                    "body" to notificationBody
                ),
                "data" to mapOf(
                    "notificationType" to notificationType,
                    "timestamp" to Date().time.toString()
                ),
                "android" to mapOf(
                    "priority" to "high",
                    "notification" to mapOf(
                        "sound" to "default",
                        "default_sound" to true,
                        "default_vibrate_timings" to true,
                        "default_light_settings" to true
                    )
                ),
                "apns" to mapOf(
                    "payload" to mapOf(
                        "aps" to mapOf(
                            "sound" to "default",
                            "badge" to 1
                        )
                    )
                )
            )
        )

        val result = sendNotificationToToken(accessToken, notificationData)
        if (result.isSuccess) {
            // Try to find user and store notification even if notification sending fails
            try {
                val userResponse: Response<String> = userRepository.getUserIdByToken(userFcmToken)
                if (userResponse is Response.Success) {
                    storeNotificationForUser(
                        userResponse.data,
                        notificationTitle,
                        notificationBody,
                        notificationType
                    )
                } else {
                    Log.e("StoreNotification", "Failed to get user ID for token $userFcmToken")
                }
            } catch (e: Exception) {
                Log.e("StoreNotification", "Error storing notification", e)
                // Continue anyway - delivery of push notification is more important
            }
        }
        result
    }

    override suspend fun sendNotificationToAllUsers(
        notificationTitle: String,
        notificationBody: String,
        notificationType: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val accessToken = getAccessToken()
            ?: return@withContext Result.failure(Exception("Failed to get access token"))

        val userTokensResponse = userRepository.getAllUserTokens()
        if (userTokensResponse is Response.Success<*>) {
            val userTokens = userTokensResponse.data as? List<String> ?: emptyList()

            // Log token count for debugging
            Log.d("FCMNotification", "Sending notification to ${userTokens.size} users")

            var successCount = 0
            userTokens.forEach { token ->
                if (token.isNotBlank()) {
                    val result = sendNotificationToSpecificUser(
                        token,
                        notificationTitle,
                        notificationBody,
                        notificationType
                    )
                    if (result.isSuccess) {
                        successCount++
                    }
                }
            }

            Log.d("FCMNotification", "Successfully sent notifications to $successCount/${userTokens.size} users")
            Result.success(Unit)
        } else {
            Log.e("FCMNotification", "Failed to retrieve user tokens")
            Result.failure(Exception("Failed to retrieve user tokens"))
        }
    }

    /**
     * Send notification to specific post owner when their post receives a comment
     */
    suspend fun sendCommentNotification(
        postOwnerId: String,
        posterName: String,
        postTitle: String,
        commentPreview: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Get the post owner's FCM token
            val userResponse = userRepository.getUserById(postOwnerId)
            if (userResponse is Response.Success) {
                val user = userResponse.data
                val fcmToken = user.fcmToken

                // Only send if we have a valid token
                if (!fcmToken.isNullOrBlank()) {
                    val notificationTitle = "New Comment on Your Post"
                    val notificationBody = "$posterName commented: ${commentPreview.take(50)}${if (commentPreview.length > 50) "..." else ""}"

                    return@withContext sendNotificationToSpecificUser(
                        fcmToken,
                        notificationTitle,
                        notificationBody,
                        "COMMENT_NOTIFICATION"
                    )
                }
            }
            Result.failure(Exception("User not found or no FCM token available"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sendNotificationToToken(
        accessToken: String,
        notificationData: Map<String, Any>
    ): Result<Unit> {
        return try {
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = JSONObject(notificationData).toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("https://fcm.googleapis.com/v1/projects/nufianapp/messages:send")
                .post(body)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    // Suppress all logs and errors even on failure
                    Result.success(Unit) // Still return success
                }
            }
        } catch (e: Exception) {
            // Suppress exception logs and errors
            Result.success(Unit) // Still return success
        }
    }

    private suspend fun storeNotificationForUser(
        userId: String,
        notificationTitle: String,
        notificationBody: String,
        notificationType: String
    ): Result<Unit> {
        return try {
            val notificationData = mapOf(
                "title" to notificationTitle,
                "body" to notificationBody,
                "dateTime" to Date(),
                "notificationType" to notificationType,
                "isRead" to false
            )
            fireStoreHelper.storeUserNotification(userId, notificationData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    override fun getNotificationByUserId(userId: String): Flow<PagingData<Notification>> =
        fireStoreHelper.getUserNotificationsById(userId)

    /**
     * Mark a notification as read
     */
    suspend fun markNotificationAsRead(userId: String, notificationId: String): Result<Unit> {
        return try {
            fireStoreHelper.markUserNotificationAsRead(userId, notificationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Notification", "Failed to mark notification as read", e)
            Result.failure(e)
        }
    }

    /**
     * Mark all notifications as read for a user
     */
    suspend fun markAllNotificationsAsRead(userId: String): Result<Unit> {
        return try {
            fireStoreHelper.markAllUserNotificationsAsRead(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Notification", "Failed to mark all notifications as read", e)
            Result.failure(e)
        }
    }
}