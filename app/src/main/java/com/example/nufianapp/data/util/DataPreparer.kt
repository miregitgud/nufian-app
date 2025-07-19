package com.example.nufianapp.data.util

import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.News


object DataPreparer {
    fun prepareNewsData(
        newsItem: News,
        contentImageUrls: List<String> = newsItem.contentImageUrls
    ): Map<String, Any> {
        return hashMapOf(
            "newsId" to newsItem.newsId,
            "subject" to newsItem.subject,
            "content" to newsItem.content,
            "contentImageUrls" to contentImageUrls,
            "share" to newsItem.share,
            "dateTime" to newsItem.dateTime
        )
    }

    fun prepareForumData(
        forumItem: Forum,
        contentImageUrls: List<String> = forumItem.contentImageUrls
    ): Map<String, Any> {
        return hashMapOf(
            "forumId" to forumItem.forumId,
            "forumUserPostId" to forumItem.forumUserPostId,
            "topic" to forumItem.topic,
            "subject" to forumItem.subject,
            "content" to forumItem.content,
            "contentImageUrls" to contentImageUrls,
            "likes" to forumItem.likes,
            "likedBy" to forumItem.likedBy,
            "comments" to forumItem.comments,
            "dateTime" to forumItem.dateTime
        )
    }
}
