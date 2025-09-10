package com.example.nufianapp.presentation.screens.home.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nufianapp.R
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.presentation.core.components.DetailForumButton

@Composable
fun ForumActionsDetail(
    forum: Forum,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            DetailForumButtonLike(
                forum = forum,
                isLiked = isLiked,
                onLikeClick = onLikeClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            DetailForumButtonComment(
                forum = forum,
                onCommentClick = onCommentClick
            )
        }
        Text(
            text = Utils().calculateTimeAgo(forum.dateTime),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black
        )
    }
}

@Composable
fun DetailForumButtonLike(
    forum: Forum,
    isLiked: Boolean,
    onLikeClick: () -> Unit
) {
    DetailForumButton(
        isSelected = isLiked,
        onClick = {
            onLikeClick()
        },
        text = forum.likes.toString(),
        iconPainter = painterResource(R.drawable.icon_item_like),
        contentDescription = "Like"
    )
}

@Composable
fun DetailForumButtonComment(
    forum: Forum,
    onCommentClick: () -> Unit,
) {
    DetailForumButton(
        isSelected = false,
        onClick = {
            onCommentClick()
        },
        text = forum.comments.toString(),
        iconPainter = painterResource(R.drawable.icon_item_comment),
        contentDescription = "Comment",
    )
}