package com.example.nufianapp.data.model

import android.net.Uri
import java.util.Date

data class News(
    var newsId: String = "",
    var newsUserPostId: String = "",
    val subject: String = "",
    val content: String = "",
    var contentImageUrls: List<String> = listOf(),
    val contentImageUris: List<Uri>? = null,
    val share: Int = 0,
    val dateTime: Date = Date()
)