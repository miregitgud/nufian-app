package com.example.nufianapp.data.model

import java.util.Date

data class Notification(
    val notificationId: String = "",
    val title: String = "",
    val body: String = "",
    val notificationType: String = "",
    val dateTime: Date = Date(),
)
