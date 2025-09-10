package com.example.nufianapp.presentation.screens.home.detail.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.nufianapp.R
import com.example.nufianapp.data.model.Comment
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.User
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.presentation.core.components.ButtonIcon
import com.example.nufianapp.presentation.core.components.CommentButtonState
import com.example.nufianapp.presentation.core.components.ImageAvatarUrlPreview
import com.example.nufianapp.presentation.core.components.UserAvatarNameProfile
import com.example.nufianapp.presentation.core.content.ContentResponseLoading
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Graphite
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DetailContent(
    forum: Forum,
    user: User?,
    currentUser: User?,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToProfilePreview: (String) -> Unit = {},
    isLiked: Boolean,
    currentLikes: Int,
    currentComments: Int,
    snackbarHostState: SnackbarHostState,
    onLikeClick: () -> Unit,
    onCommentSubmit: (String) -> Unit,
    onCommentDeleted: () -> Unit,
    lazyPagingItems: LazyPagingItems<Pair<Comment, User?>>
) {
    val listState = rememberLazyListState()
    val commentButtonState = remember { CommentButtonState() }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val isCommentFocused = remember { mutableStateOf(false) }

    LaunchedEffect(isEnabled) {
        if (isEnabled) {
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        LazyColumn(
            modifier = modifier.weight(1f),
            state = listState,
            contentPadding = PaddingValues(16.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ButtonNavigateUpTitle(title = "Forum", navigateBack = navigateBack)
                    Spacer(modifier = Modifier.height(8.dp))
                    user?.let {
                        UserAvatarNameProfile(
                            onClick = {
                                navigateToProfilePreview(it.uid)
                            },
                            user = it,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ForumInformation(forum = forum)
                    Spacer(modifier = Modifier.height(8.dp))
                    DisplayImages(forum.contentImageUrls)
                    ForumActionsDetail(
                        forum = forum.copy(likes = currentLikes, comments = currentComments),
                        isLiked = isLiked,
                        onLikeClick = onLikeClick,
                        onCommentClick = {
                            if (!isCommentFocused.value) {
                                focusRequester.requestFocus()
                            } else {
                                focusManager.clearFocus()
                            }
                            isCommentFocused.value = !isCommentFocused.value
                        }
                    )
                    HorizontalDivider(thickness = 1.dp, color = Graphite)
                    Text(text = "Comments", style = MaterialTheme.typography.headlineSmall, color = Color.Black)
                }
            }
            items(lazyPagingItems.itemCount) { index ->
                val (comment, userData) = lazyPagingItems[index] ?: return@items
                if (userData != null && currentUser != null) {
                    CommentItem(
                        forum = forum,
                        comment = comment,
                        user = userData,
                        currentUser = currentUser,
                        navigateToProfilePreview = navigateToProfilePreview,
                        onDeleteComment = { commentId ->
                            deleteCommentFromFirestore(
                                forumId = forum.forumId,
                                commentId = commentId,
                                onSuccess = {
                                    onCommentDeleted()
                                }
                            )
                        }
                    )
                }

            }
            // Handle different load states
            lazyPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading && lazyPagingItems.itemCount == 0 -> {
                        item {
                            ContentResponseLoading()
                        }
                    }

                    loadState.append is LoadState.Loading -> {
                        item {
                            ContentResponseLoading()
                        }
                    }

                    loadState.refresh is LoadState.Error -> {
                        item {
                            ErrorCommentView()
                        }
                    }

                    loadState.append is LoadState.Error -> {
                        item {
                            ErrorCommentView()
                        }
                    }

                    loadState.append is LoadState.NotLoading && loadState.refresh !is LoadState.Loading && lazyPagingItems.itemCount == 0 -> {
                        item {
                            EmptyCommentView()
                        }
                    }
                }
            }
        }
        currentUser?.let {
            CommentInputSection(
                displayUser = it,
                navigateToProfilePreview = navigateToProfilePreview,
                commentState = commentButtonState.commentState.value,
                onCommentChange = { comments ->
                    commentButtonState.commentState.value = comments
                },
                onCommentSubmit = {
                    onCommentSubmit(commentButtonState.commentState.value)
                    commentButtonState.clearComment()
                    focusManager.clearFocus()
                    isCommentFocused.value = false
                },
                focusRequester = focusRequester,
                isCommentFocused = isCommentFocused
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            focusManager.clearFocus()
        }
    }
}

fun deleteCommentFromFirestore(
    forumId: String,
    commentId: String,
    onSuccess: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val forumRef = db.collection("forums").document(forumId)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            db.collection("forums")
                .document(forumId)
                .collection("comments")
                .document(commentId)
                .delete()
                .addOnSuccessListener {
                    Log.d("DeleteComment", "Comment $commentId deleted")

                    forumRef.get().addOnSuccessListener { forumSnapshot ->
                        val currentComments = forumSnapshot.getLong("comments") ?: 0
                        if (currentComments > 0) {
                            forumRef.update("comments", FieldValue.increment(-1))
                                .addOnSuccessListener {
                                    Log.d("UpdateForum", "Comment count decremented")
                                    onSuccess() // ✅ Trigger UI refresh
                                }
                                .addOnFailureListener { e ->
                                    Log.e("UpdateForum", "Failed to decrement comment count", e)
                                }
                        } else {
                            Log.w("UpdateForum", "Comment count already at 0, no decrement")
                            onSuccess() // Still trigger UI update
                        }
                    }.addOnFailureListener {
                        Log.e("GetForum", "Failed to fetch forum document", it)
                    }

                }
                .addOnFailureListener {
                    Log.e("DeleteComment", "Failed to delete comment", it)
                }

        } catch (e: Exception) {
            Log.e("DeleteComment", "Error deleting comment", e)
        }
    }
}



@Composable
fun CommentInputSection(
    displayUser: User,
    commentState: String,
    onCommentChange: (String) -> Unit,
    onCommentSubmit: () -> Unit,
    navigateToProfilePreview: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    isCommentFocused: MutableState<Boolean>
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Graphite)
        )
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ImageAvatarUrlPreview(
                avatarUrl = displayUser.avatarUrl,
                isAdmin = displayUser.userType == "admin",
                onClick = { navigateToProfilePreview(displayUser.uid) },
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                modifier = modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                value = commentState,
                onValueChange = onCommentChange,
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black.copy(0.4f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Blue,
                    focusedLabelColor = Blue,
                    unfocusedLabelColor = Blue
                ),
                shape = RoundedCornerShape(16.dp),
                placeholder = {
                    Text(
                        text = "Add your comment...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Graphite.copy(0.5f)
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_item_send_comment),
                        contentDescription = null,
                        tint = Blue,
                        modifier = Modifier.clickable {
                            onCommentSubmit()
                        }
                    )
                }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            focusManager.clearFocus()
            isCommentFocused.value = false
        }
    }
}

@Composable
fun ButtonNavigateUpTitle(title: String, navigateBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ButtonIcon(onClickButton = navigateBack)
        Text(text = title, style = MaterialTheme.typography.headlineSmall, color = Color.Black)
    }
}

@Composable
fun ForumInformation(forum: Forum) {
    // Check if the topic is "Official Announcement"
    val isOfficialAnnouncement = forum.topic == "Official Announcement"

    // Apply border modifier if it's an official announcement
    val contentModifier = if (isOfficialAnnouncement) {
        Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    } else {
        Modifier.fillMaxWidth()
    }

    Column(modifier = contentModifier) {
        Text(
            modifier = Modifier
                .background(Utils().getColorForTopic(forum.topic), RoundedCornerShape(32.dp))
                .padding(vertical = 8.dp, horizontal = 16.dp),
            text = forum.topic,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = forum.subject, style = MaterialTheme.typography.headlineSmall, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = forum.content, style = MaterialTheme.typography.bodyMedium, color = Color.Black)
    }
}