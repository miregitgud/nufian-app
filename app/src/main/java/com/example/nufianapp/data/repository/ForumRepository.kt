package com.example.nufianapp.data.repository

import androidx.paging.PagingData
import com.example.nufianapp.data.model.Comment
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.domain.model.Response
import kotlinx.coroutines.flow.Flow

typealias AddForumResponse = Response<Boolean>

interface ForumRepository {
    fun getForum(): Flow<PagingData<Forum>>
    fun getForumsByUserId(userId: String): Flow<PagingData<Forum>>
    fun getSingleForumData(forumId: String): Flow<Response<Forum>>
    fun getCommentsByForumId(forumId: String): Flow<PagingData<Comment>>
    suspend fun storeForumData(forumItem: Forum): AddForumResponse
    suspend fun storeForumComment(forumId: String, content: String): Response<Boolean>
    suspend fun likeForum(forumId: String): Response<Boolean>
    suspend fun isUserLikedForum(forumId: String, userId: String): Response<Boolean>
    suspend fun getLatestLikes(forumId: String): Response<Int>
    suspend fun getLatestCommentsCount(forumId: String): Response<Int>
    suspend fun deleteForum(forumId: String): Response<Unit>
}