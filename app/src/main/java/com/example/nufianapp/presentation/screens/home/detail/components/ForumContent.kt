package com.example.nufianapp.presentation.screens.home.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.nufianapp.R
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.User
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.presentation.core.components.ButtonIcon
import com.example.nufianapp.presentation.core.components.UserAvatarNameProfile
import com.example.nufianapp.presentation.screens.home.components.BlueStyleForumActions
import com.example.nufianapp.ui.theme.Charcoal
import com.example.nufianapp.ui.theme.Graphite
import com.example.nufianapp.ui.theme.White

@Composable
fun ForumCardContent(
    forum: Forum,
    user: User? = null,
    isLiked: Boolean = false,
    currentLikes: Int = forum.likes,
    currentComments: Int = forum.comments,
    showActions: Boolean = false,
    showUserHeader: Boolean = false,
    onDeletePost: (() -> Unit)? = null,
    onLikeClick: (() -> Unit)? = null,
    onCommentClick: (() -> Unit)? = null,
    navigateToProfilePreview: ((String) -> Unit)? = null,
) {
    var showDropdown by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (showUserHeader && user != null && navigateToProfilePreview != null) {
                    UserAvatarNameProfile(
                        onClick = { navigateToProfilePreview(user.uid) },
                        user = user,
                        verticalAlignment = Alignment.CenterVertically
                    )
                }
                onDeletePost?.let {
                    Box(modifier = Modifier.zIndex(1f)) {
                        ButtonIcon(
                            onClickButton = { showDropdown = true },
                            iconRes = R.drawable.icon_triple_dots,
                            tint = Graphite
                        )

                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete Post") },
                                onClick = {
                                    showDropdown = false
                                    onDeletePost()
                                }
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = forum.topic,
                    style = MaterialTheme.typography.bodySmall,
                    color = White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(
                            Utils().getColorForTopic(forum.topic),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(12.dp, 4.dp)
                )
            }

            Text(
                text = forum.subject,
                style = MaterialTheme.typography.headlineSmall.copy(
                    lineHeight = 20.sp
                ),
                color = Charcoal,
                fontSize = 16.sp
            )

            if (forum.contentImageUrls.isNotEmpty()) {
                DisplayImages(
                    forum.contentImageUrls,
                    modifier = Modifier
                        .height(150.dp)
                )
            } else {
                Text(
                    text = forum.content + " ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = Utils().calculateTimeAgo(forum.dateTime),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Start
            )
        }
        if (showActions && onLikeClick != null && onCommentClick != null) {
            BlueStyleForumActions(
                forum = forum.copy(likes = currentLikes, comments = currentComments),
                isLiked = isLiked,
                onLikeClick = onLikeClick,
                onCommentButtonClick = onCommentClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp) // optional spacing
            )
        }
    }
}