package com.example.nufianapp.data.model

import android.net.Uri
import java.util.Date

data class Forum(
    var forumId: String = "",
    var forumUserPostId: String = "",
    var topic: String = "",
    val subject: String = "",
    val content: String = "",
    var contentImageUrls: List<String> = listOf(),
    val contentImageUris: List<Uri>? = null,
    var likes: Int = 0,
    val comments: Int = 0,
    val dateTime: Date = Date(),
    val likedBy: MutableList<String> = mutableListOf()
) {
    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "forumId" to forumId,
            "forumUserPostId" to forumUserPostId,
            "topic" to topic,
            "subject" to subject,
            "content" to content,
            "contentImageUrls" to contentImageUrls,
            "likes" to likes,
            "comments" to comments,
            "dateTime" to dateTime,
            "likedBy" to likedBy
        )

        contentImageUris?.let {
            map["contentImageUris"] = it.map(Uri::toString)
        }

        return map
    }
}
