package com.example.nufianapp.data.repository

import androidx.paging.PagingData
import com.example.nufianapp.data.firebase.FirebaseModule.firestore
import com.example.nufianapp.data.model.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

interface NotificationRepository {

    suspend fun sendNotificationToSpecificUser(
        userFcmToken: String,
        notificationTitle: String, notificationBody: String,
        notificationType: String
    ): Result<Unit>

    suspend fun sendNotificationToAllUsers(
        notificationTitle: String, notificationBody: String,
        notificationType: String
    ): Result<Unit>

    fun getNotificationByUserId(userId: String): Flow<PagingData<Notification>>

    suspend fun clearAllNotificationsForUser(userId: String) {
        val notificationsRef = firestore
            .collection("users")
            .document(userId)
            .collection("notifications")

        val snapshot = notificationsRef.get().await()

        snapshot.documents.forEach { document ->
            document.reference.delete().await()
        }
    }


}
