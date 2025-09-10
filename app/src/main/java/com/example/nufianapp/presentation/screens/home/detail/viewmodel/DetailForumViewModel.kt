package com.example.nufianapp.presentation.screens.home.detail.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.nufianapp.data.model.Comment
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.repository.ForumRepository
import com.example.nufianapp.domain.repository.NotificationRepository
import com.example.nufianapp.domain.repository.UserRepository
import com.example.nufianapp.domain.model.ErrorUtils
import com.example.nufianapp.domain.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailForumViewModel @Inject constructor(
    private val forumRepository: ForumRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _forumByIdData = MutableStateFlow<Response<Forum>>(Response.Loading)
    val forumByIdData: StateFlow<Response<Forum>> = _forumByIdData.asStateFlow()

    private val _userByIdData = MutableStateFlow<Response<User?>>(Response.Loading)
    val userByIdData: StateFlow<Response<User?>> = _userByIdData.asStateFlow()

    private val _pagingFlow = MutableStateFlow<PagingData<Pair<Comment, User?>>>(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<Pair<Comment, User?>>> = _pagingFlow.asStateFlow()

    private val _forumState = MutableStateFlow(ForumState())
    val forumState: StateFlow<ForumState> = _forumState.asStateFlow()

    private val _snackBarFlow = MutableSharedFlow<String>()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow.asSharedFlow()

    fun fetchForumById(forumId: String, forumUserPostId: String) {
        viewModelScope.launch {
            try {
                val forumJob = async {
                    forumRepository.getSingleForumData(forumId).collect { response ->
                        _forumByIdData.value = response
                        if (response is Response.Success) {
                            fetchCommentsAndLikes(forumId)
                        }
                    }
                }

                val userJob = async {
                    _userByIdData.value = userRepository.getUserDataById(forumUserPostId)
                }

                forumJob.await()
                userJob.await()
            } catch (e: Exception) {
                _forumByIdData.value = Response.Failure(e)
                _userByIdData.value = Response.Failure(e)
            }
        }
    }

    fun getCommentsFlow(forumId: String): Flow<PagingData<Pair<Comment, User?>>> {
        return forumRepository.getCommentsByForumId(forumId)
            .distinctUntilChanged()
            .map { pagingData ->
                pagingData.map { comment ->
                    val user = (userRepository.getUserDataById(comment.userId) as? Response.Success)?.data
                    comment to user
                }
            }
            .cachedIn(viewModelScope)
    }


    private fun fetchCommentsAndLikes(forumId: String) {
        viewModelScope.launch {
            val commentsJob = async { fetchCommentsWithUserData(forumId) }
            val likesJob = async { fetchLatestLikes(forumId) }
            commentsJob.await()
            likesJob.await()
        }
    }

    private suspend fun fetchCommentsWithUserData(forumId: String) {
        forumRepository.getCommentsByForumId(forumId)
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .catch { e -> _snackBarFlow.emit(ErrorUtils.getFriendlyErrorMessage(e)) }
            .collectLatest { pagingData ->
                val updatedPagingData = pagingData.map { comment ->
                    val user =
                        (userRepository.getUserDataById(comment.userId) as? Response.Success)?.data
                    comment to user
                }
                _pagingFlow.value = updatedPagingData
            }
    }

    fun checkIfUserLikedForum(forumId: String) {
        viewModelScope.launch {
            val userId = userRepository.currentUser?.uid ?: return@launch
            val response = forumRepository.isUserLikedForum(forumId, userId)
            if (response is Response.Success) {
                _forumState.update { it.copy(likeStates = it.likeStates + (forumId to response.data)) }
            } else if (response is Response.Failure) {
                _snackBarFlow.emit(ErrorUtils.getFriendlyErrorMessage(response.e))
            }
        }
    }

    fun likeForum(forum: Forum) {
        viewModelScope.launch {
            val userId = userRepository.currentUser?.uid ?: return@launch
            val userResponse = userRepository.getUserDataById(userId)
            val username = (userResponse as? Response.Success)?.data?.displayName ?: "Someone"

            val response = forumRepository.likeForum(forum.forumId)
            if (response is Response.Success) {
                val updatedLikeState = response.data
                val likeNotificationSent =
                    _forumState.value.likeNotificationSent[forum.forumId] ?: false

                updateLikeState(forum, updatedLikeState)

                if (updatedLikeState && !likeNotificationSent) {
                    sendNotification(
                        forum.forumUserPostId,
                        "$username liked your forum",
                        "Your forum '${forum.subject}' received a new like.",
                        "like"
                    )
                    _forumState.update { it.copy(likeNotificationSent = it.likeNotificationSent + (forum.forumId to true)) }
                }
            } else if (response is Response.Failure) {
                _snackBarFlow.emit(ErrorUtils.getFriendlyErrorMessage(response.e))
            }
        }
    }

    fun addComment(forumId: String, forumUserPostId: String, content: String) {
        if (content.isBlank()) {
            viewModelScope.launch {
                _snackBarFlow.emit("Comment cannot be empty")
            }
            return
        }

        viewModelScope.launch {
            try {
                val userId = userRepository.currentUser?.uid ?: return@launch
                val userResponse = userRepository.getUserDataById(userId)
                val username = (userResponse as? Response.Success)?.data?.displayName ?: "Someone"

                forumRepository.storeForumComment(forumId, content)
                sendNotification(
                    forumUserPostId,
                    "$username commented on your post",
                    content,
                    "comment"
                )
                _snackBarFlow.emit("Comment added successfully!")
                fetchCommentsWithUserData(forumId)
                fetchLatestComments(forumId)
            } catch (e: Exception) {
                _snackBarFlow.emit(ErrorUtils.getFriendlyErrorMessage(e))
            }
        }
    }


    private suspend fun sendNotification(
        userId: String,
        title: String,
        message: String,
        type: String
    ) {
        val response = userRepository.getUserFcmToken(userId)
        if (response is Response.Success) {
            val notificationResult = notificationRepository.sendNotificationToSpecificUser(
                response.data,
                title,
                message,
                type
            )
            if (notificationResult.isFailure) {
                _snackBarFlow.emit("Failed to send notification")
            }
        } else {
            _snackBarFlow.emit("Failed to fetch FCM token for user")
        }
    }

    private fun updateLikeState(forum: Forum, updatedLikeState: Boolean) {
        val currentLikes = _forumState.value.latestLikes[forum.forumId] ?: forum.likes
        val updatedLikes = if (updatedLikeState) currentLikes + 1 else currentLikes - 1
        _forumState.update {
            it.copy(
                likeStates = it.likeStates + (forum.forumId to updatedLikeState),
                latestLikes = it.latestLikes + (forum.forumId to updatedLikes)
            )
        }
    }

    fun fetchLatestLikes(forumId: String) {
        viewModelScope.launch {
            val response = forumRepository.getLatestLikes(forumId)
            if (response is Response.Success) {
                _forumState.update { it.copy(latestLikes = it.latestLikes + (forumId to response.data)) }
            } else if (response is Response.Failure) {
                _snackBarFlow.emit(ErrorUtils.getFriendlyErrorMessage(response.e))
            }
        }
    }

    fun fetchLatestComments(forumId: String) {
        viewModelScope.launch {
            val response = forumRepository.getLatestCommentsCount(forumId)
            if (response is Response.Success) {
                _forumState.update { it.copy(latestComments = it.latestComments + (forumId to response.data)) }
            } else if (response is Response.Failure) {
                _snackBarFlow.emit(ErrorUtils.getFriendlyErrorMessage(response.e))
            }
        }
    }

}


data class ForumState(
    val likeStates: Map<String, Boolean> = emptyMap(),
    val latestLikes: Map<String, Int> = emptyMap(),
    val latestComments: Map<String, Int> = emptyMap(),
    val likeNotificationSent: Map<String, Boolean> = emptyMap()  // Add a new map to track if notification was sent
)