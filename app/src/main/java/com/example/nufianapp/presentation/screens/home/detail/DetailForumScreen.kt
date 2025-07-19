package com.example.nufianapp.presentation.screens.home.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.components.content.ContentResponseError
import com.example.nufianapp.presentation.core.components.content.ContentResponseLoading
import com.example.nufianapp.presentation.screens.home.detail.components.DetailContent
import com.example.nufianapp.presentation.screens.home.detail.viewmodel.DetailForumViewModel
import com.example.nufianapp.presentation.screens.profile.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun DetailForumScreen(
    modifier: Modifier = Modifier,
    forumId: String,
    forumUserPostId: String,
    isEnabled: Boolean = false,
    navigateBack: () -> Unit,
    navigateToProfilePreview: (String) -> Unit,
    viewModel: DetailForumViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val forumByIdDataResponse by viewModel.forumByIdData.collectAsState()
    val userByIdDataResponse by viewModel.userByIdData.collectAsState()
    val refreshTrigger = remember { mutableStateOf(0) }
    val lazyPagingItems = remember(forumId, refreshTrigger.value) {
        viewModel.getCommentsFlow(forumId)
    }.collectAsLazyPagingItems()
    val userDataResponse by userViewModel.userByIdData.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userViewModel.currentUserId) {
        userViewModel.currentUserId?.let {
            userViewModel.fetchUserById(it)
        }
    }

    LaunchedEffect(forumId, forumUserPostId) {
        viewModel.fetchForumById(forumId, forumUserPostId)
        viewModel.checkIfUserLikedForum(forumId)
    }

    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = snackBarHostState) {
        viewModel.snackBarFlow.collect { message ->
            snackBarHostState.showSnackbar(message)
        }
    }

    val forumState by viewModel.forumState.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = modifier.padding(paddingValues)
        ) {
            when (forumByIdDataResponse) {
                is Response.Loading -> ContentResponseLoading()
                is Response.Success -> {
                    val forum = (forumByIdDataResponse as Response.Success).data
                    val user = (userByIdDataResponse as? Response.Success)?.data
                    val currentUser = (userDataResponse as? Response.Success)?.data
                    DetailContent(
                        forum = forum,
                        user = user,
                        currentUser = currentUser,
                        modifier = modifier,
                        isEnabled = isEnabled,
                        isLiked = forumState.likeStates[forum.forumId] ?: false,
                        currentLikes = forumState.latestLikes[forum.forumId] ?: forum.likes,
                        currentComments = forumState.latestComments[forum.forumId]
                            ?: forum.comments,
                        onLikeClick = { viewModel.likeForum(forum) },
                        onCommentSubmit = { comment ->
                            viewModel.addComment(forum.forumId, forum.forumUserPostId, comment)
                            refreshTrigger.value++
                        },
                        lazyPagingItems = lazyPagingItems,
                        navigateBack = navigateBack,
                        navigateToProfilePreview = navigateToProfilePreview,
                        onCommentDeleted = {
                            refreshTrigger.value++
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar("Comment deleted successfully")
                            }
                        },
                        snackbarHostState = snackBarHostState
                    )
                }

                is Response.Failure -> ContentResponseError(
                    modifier = modifier,
                    message = "Failed to load forum data. Please try again.",
                    navigateBack = navigateBack,
                    onRetry = { viewModel.fetchForumById(forumId, forumUserPostId) }
                )
            }
        }
    }
}
