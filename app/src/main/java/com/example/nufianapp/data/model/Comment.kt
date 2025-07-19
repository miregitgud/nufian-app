package com.example.nufianapp.data.model

import java.util.Date

data class Comment(
    val commentId: String = "",
    val forumId: String = "",
    val userId: String = "",
    val content: String = "",
    val dateTime: Date = Date()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "commentId" to commentId,
            "forumId" to forumId,
            "userId" to userId,
            "content" to content,
            "dateTime" to dateTime
        )
    }
}
