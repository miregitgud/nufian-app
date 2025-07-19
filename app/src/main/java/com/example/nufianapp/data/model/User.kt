package com.example.nufianapp.data.model

import com.google.firebase.firestore.PropertyName
import java.util.Date

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val interest: String = "",
    val batch: Int = 0,
    val status: String = "",
    val bioData: String = "",
    var userType: String = "student",
    var avatarUrl: String? = "",
    val avatarUri: String? = null,
    val createdAt: Date = Date(),
    val fcmToken: String? = "",
    val linkedinUrl: String? = null,
    val instagramUrl: String? = null,
    val username: String = "",
    val firstLogin: Boolean? = null,
    @get:PropertyName("isBanned") @set:PropertyName("isBanned")
    var isBanned: Boolean? = false
)