package com.example.nufianapp.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nufianapp.R
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.DisabledColor
import com.example.nufianapp.ui.theme.White

@Composable
fun BlueStyleForumActions(
    forum: Forum,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentButtonClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(Blue)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button
                BlueStyleActionButton(
                    isSelected = isLiked,
                    count = forum.likes,
                    iconRes = R.drawable.icon_item_like,
                    contentDescription = "Like",
                    onClick = onLikeClick
                )

                // Comment button
                BlueStyleActionButton(
                    isSelected = false,
                    count = forum.comments,
                    iconRes = R.drawable.icon_item_comment,
                    contentDescription = "Comment",
                    onClick = onCommentButtonClick
                )
            }
        }
    }
}

@Composable
fun BlueStyleActionButton(
    isSelected: Boolean,
    count: Int,
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    val icon =
        if (isSelected) R.drawable.icon_item_like
        else R.drawable.icon_item_like_inactive

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(if(contentDescription == "Like") { icon } else { iconRes }),
            contentDescription = contentDescription,
            tint = White,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = White
        )
    }
}