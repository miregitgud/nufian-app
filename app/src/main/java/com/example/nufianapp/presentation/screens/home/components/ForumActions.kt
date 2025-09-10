package com.example.nufianapp.presentation.screens.home.components

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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nufianapp.R
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.presentation.core.components.DetailForumButton

@Composable
fun ForumActions(
    forum: Forum,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            DetailForumButton(
                isSelected = isLiked,
                onClick = {
                    onLikeClick()
                },
                text = forum.likes.toString(),
                iconPainter = painterResource(R.drawable.icon_item_like),
                contentDescription = "Like"
            )
            Spacer(modifier = Modifier.width(8.dp))
            DetailForumButton(
                isSelected = false,
                onClick = {
                    onCommentButtonClick()
                },
                text = forum.comments.toString(),
                iconPainter = painterResource(R.drawable.icon_item_comment),
                contentDescription = "Comment",
            )
        }
        Text(
            text = Utils().calculateTimeAgo(forum.dateTime),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}
