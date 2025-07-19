package com.example.nufianapp.data.repository

import android.util.Log
import androidx.paging.PagingData
import com.example.nufianapp.data.firebase.FireStoreHelper
import com.example.nufianapp.data.firebase.StorageHelper
import com.example.nufianapp.data.model.Comment
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.util.DataPreparer
import com.example.nufianapp.domain.model.ErrorUtils
import com.example.nufianapp.domain.model.Response
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ForumRepositoryImpl @Inject constructor(
    private val fireStoreHelper: FireStoreHelper,
    private val storageHelper: StorageHelper,
    private val userRepository: UserRepository
) : ForumRepository {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun getForum(): Flow<PagingData<Forum>> = flow {
        fireStoreHelper.getForum()
            .collect {
                emit(it)
            }
    }.catch { e ->
        emit(PagingData.empty())
        Log.e("ForumRepositoryImpl", ErrorUtils.getFriendlyErrorMessage(e))
    }


    override fun getForumsByUserId(userId: String): Flow<PagingData<Forum>> = flow {
        fireStoreHelper.getForumByUserId(userId)
            .collect {
                emit(it)
            }
    }.catch { e ->
        emit(PagingData.empty())
        Log.e("ForumRepositoryImpl", ErrorUtils.getFriendlyErrorMessage(e))
    }



    override fun getSingleForumData(forumId: String): Flow<Response<Forum>> = flow {
        emit(Response.Loading)
        val forum = fireStoreHelper.getSingleForumData(forumId)
        emit(Response.Success(forum))
    }.catch { e ->
        emit(Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e))))
    }

    override suspend fun storeForumData(forumItem: Forum): AddForumResponse =
        withContext(ioScope.coroutineContext) {
            return@withContext try {
                val imagePaths = forumItem.contentImageUris?.map { uri ->
                    "content_images/forum/${forumItem.dateTime}/${uri.lastPathSegment}"
                } ?: emptyList()
                val contentImageUrls = if (imagePaths.isNotEmpty()) {
                    storageHelper.uploadImages(imagePaths, forumItem.contentImageUris!!)
                } else {
                    emptyList()
                }

                val forumData = DataPreparer.prepareForumData(forumItem, contentImageUrls)
                fireStoreHelper.storeForumData(forumData.toMap())
                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }

    override suspend fun storeForumComment(forumId: String, content: String): Response<Boolean> =
        withContext(ioScope.coroutineContext) {
            val currentUser = userRepository.currentUser
            return@withContext if (currentUser != null) {
                try {
                    val comment = Comment(
                        commentId = fireStoreHelper.generateCommentId(forumId),
                        forumId = forumId,
                        userId = currentUser.uid,
                        content = content
                    )
                    fireStoreHelper.storeForumComment(comment)
                    Response.Success(true)
                } catch (e: Exception) {
                    Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
                }
            } else {
                Response.Failure(Exception("User not authenticated"))
            }
        }

    override fun getCommentsByForumId(forumId: String): Flow<PagingData<Comment>> = flow {
        fireStoreHelper.getCommentsByForumId(forumId)
            .collect {
                emit(it)
            }
    }.catch { e ->
        emit(PagingData.empty())
        Log.e("ForumRepositoryImpl", ErrorUtils.getFriendlyErrorMessage(e))
    }

    override suspend fun likeForum(forumId: String): Response<Boolean> =
        withContext(ioScope.coroutineContext) {
            val currentUser = userRepository.currentUser
            return@withContext if (currentUser != null) {
                try {
                    val result = fireStoreHelper.likeForum(forumId, currentUser.uid)
                    Response.Success(result)
                } catch (e: Exception) {
                    Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
                }
            } else {
                Response.Failure(Exception("User not authenticated"))
            }
        }

    override suspend fun isUserLikedForum(forumId: String, userId: String): Response<Boolean> =
        withContext(ioScope.coroutineContext) {
            return@withContext try {
                val isLiked = fireStoreHelper.isUserLikedForum(forumId, userId)
                Response.Success(isLiked)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }

    override suspend fun getLatestLikes(forumId: String): Response<Int> =
        withContext(ioScope.coroutineContext) {
            return@withContext try {
                val likes = fireStoreHelper.getLatestLikes(forumId)
                Response.Success(likes)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }

    override suspend fun getLatestCommentsCount(forumId: String): Response<Int> =
        withContext(ioScope.coroutineContext) {
            return@withContext try {
                val commentsCount = fireStoreHelper.getLatestCommentsCount(forumId)
                Response.Success(commentsCount)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }


    override suspend fun deleteForum(forumId: String): Response<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                fireStoreHelper.deleteUserForum(forumId)
                Response.Success(Unit)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }
}
