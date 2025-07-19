package com.example.nufianapp.data.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.app.NotificationCompat
import com.example.nufianapp.R
import com.example.nufianapp.data.repository.AuthRepositoryImpl
import com.example.nufianapp.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var authRepository: AuthRepositoryImpl

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            sendNotification(it.title, it.body, remoteMessage.data)
            Log.d("FORUM_NOTIF", "onMessageReceived: ${remoteMessage.data["forumId"]}")
        }
    }

    fun updateUserFcmToken(userId: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCMToken", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Update token in Firestore for current user
            val db = Firebase.firestore
            db.collection("users").document(userId)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    Log.d("FCMToken", "Token updated in Firestore for user: $userId")
                }
                .addOnFailureListener { e ->
                    Log.e("FCMToken", "Failed to update token: ${e.message}", e)
                }
        }
    }

    fun clearUserFcmToken(userId: String) {
        val db = Firebase.firestore
        db.collection("users").document(userId)
            .update("fcmToken", "")
            .addOnSuccessListener {
                Log.d("FCMToken", "Token cleared in Firestore for user: $userId")
            }
            .addOnFailureListener { e ->
                Log.e("FCMToken", "Failed to clear token: ${e.message}", e)
            }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMToken", "Refreshed token: $token")

        // Use the injected authRepository to update the token
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = authRepository.updateFcmToken(token)
                if (result is com.example.nufianapp.domain.model.Response.Success) {
                    Log.d("FCMToken", "Token updated successfully in Firestore")
                } else {
                    Log.e("FCMToken", "Failed to update token: ${(result as? com.example.nufianapp.domain.model.Response.Failure)}")
                }
            } catch (e: Exception) {
                Log.e("FCMToken", "Error updating token: ${e.message}", e)
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private fun sendNotification(title: String?, body: String?, data: Map<String, String>) {
        val channelId = "forum_notifications"
        val channelName = "Forum Notifications"
        val notificationId = 0

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for forum notifications"
        }
        notificationManager.createNotificationChannel(channel)

        // Extract necessary data from notification payload
        val forumId = data["forumId"]
        val forumUserPostId = data["forumUserPostId"]
        val notificationType = data["notificationType"]

        Log.d("FORUM_ID_FCM", "sendNotification: $forumId")
        Log.d("FORUM_ID_FCM", "sendNotification: $forumUserPostId")
        data.forEach { forumIda ->
            Log.d("FORUM_ID_FCM_DATA", "sendNotification: $forumIda")
        }

        // Intent to launch when the notification is clicked
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("forumId", forumId)
            putExtra("forumUserPostId", forumUserPostId)
        }

        val pendingIntent: PendingIntent =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE for Android S and above
                )
            } else {
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT // Use FLAG_UPDATE_CURRENT for versions below Android S
                )
            }

        // Customize notification based on notificationType
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.img_logo_app)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        when (notificationType) {
            "comment" -> {
                notificationBuilder
                    .setContentTitle(title)
                    .setContentText(body ?: "New comment on your post")
            }

            "like" -> {
                notificationBuilder
                    .setContentTitle("New Like on Your Post")
                    .setContentText("Someone liked your post")
            }

            else -> {
                notificationBuilder
                    .setContentTitle(title)
                    .setContentText(body ?: "New activity on your post")
            }
        }

        // Show notification
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
