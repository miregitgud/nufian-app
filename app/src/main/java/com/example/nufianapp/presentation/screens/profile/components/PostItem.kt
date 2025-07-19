package com.example.nufianapp.presentation.screens.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.User
import com.example.nufianapp.presentation.screens.home.detail.components.ForumCardContent
import com.example.nufianapp.presentation.screens.home.detail.viewmodel.DetailForumViewModel
import com.example.nufianapp.ui.theme.Blue

@Composable
fun PostItem(
    forum: Forum,
    modifier: Modifier = Modifier,
    user: User? = null,
    isProfile: Boolean = false,
    onButtonClick: () -> Unit = {},
    onDeletePost: (Forum) -> Unit,
    viewModel: DetailForumViewModel = hiltViewModel(),
) {
    LaunchedEffect(forum.forumId) {
        viewModel.checkIfUserLikedForum(forum.forumId)
        viewModel.fetchLatestLikes(forum.forumId)
        viewModel.fetchLatestComments(forum.forumId)
        viewModel.fetchForumById(forum.forumId, forum.forumUserPostId)
    }

    val forumState by viewModel.forumState.collectAsState()
    val isLiked = forumState.likeStates[forum.forumId] ?: false
    val currentLikes = forumState.latestLikes[forum.forumId] ?: forum.likes
    val currentComments = forumState.latestComments[forum.forumId] ?: forum.comments

    // Determine if this is an official announcement
    val isOfficialAnnouncement = forum.topic == "Official Announcement"

    // Add border for official announcements
    val borderStroke = if (isOfficialAnnouncement) {
        BorderStroke(4.dp, Blue)
    } else {
        null
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        border = borderStroke,
        modifier = modifier
    ) {
        ForumCardContent(
            forum = forum,
            user = user,
            isLiked = isLiked,
            currentLikes = currentLikes,
            currentComments = currentComments,
            showActions = true,
            showUserHeader = true,
            onLikeClick = { viewModel.likeForum(forum) },
            onCommentClick = onButtonClick,
            onDeletePost = if (isProfile) { { onDeletePost(forum) } } else null,
        )
    }
}