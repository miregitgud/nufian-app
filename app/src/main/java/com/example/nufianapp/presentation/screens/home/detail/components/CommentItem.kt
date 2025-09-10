package com.example.nufianapp.presentation.screens.home.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nufianapp.data.model.Comment
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.User
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.presentation.core.components.ImageAvatarUrlPreview
import com.example.nufianapp.ui.theme.Graphite
import com.example.nufianapp.ui.theme.NeonWhite

@Composable
fun CommentItem(
    modifier: Modifier = Modifier,
    navigateToProfilePreview: (String) -> Unit,
    comment: Comment,
    forum: Forum,
    user: User,
    onDeleteComment: (String) -> Unit,
    currentUser: User
) {
    var expanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }


    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            ImageAvatarUrlPreview(
                avatarUrl = user.avatarUrl,
                isAdmin = false,
                onClick = { navigateToProfilePreview(user.uid) },
            )
            Spacer(modifier = modifier.width(8.dp))

            Box(
                modifier
                    .fillMaxWidth()
                    .background(NeonWhite)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            if (comment.userId == currentUser.uid ||
                                currentUser.userType == "admin" ||
                                currentUser.uid == forum.forumUserPostId) {
                                expanded = true
                            }
                        },
                        indication = rememberRipple(
                            color = Graphite.copy(alpha = 0.2f),
                            bounded = true
                        ),
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            showConfirmDialog = true
                        }
                    )
                }
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = user.displayName.ifEmpty { "Deleted User" },
                            style = MaterialTheme.typography.labelMedium
                        )

                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = Utils().calculateTimeAgo(comment.dateTime),
                                style = MaterialTheme.typography.bodySmall,
                                color = Graphite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = if (user.userType == "admin") {
                            user.interest
                        } else if (user.uid == "") {
                            "User not found"
                        } else {
                            "${user.interest} ${user.batch} • ${user.status}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Graphite
                    )

                    Utils().SpacerHeightSmall()
                    Text(
                        comment.content,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 4
                    )
                }
            }
        }

        Box(
            modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Graphite)
        )
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text("Delete Comment")
            },
            text = {
                Text("Are you sure you want to delete this comment?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        onDeleteComment(comment.commentId)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}