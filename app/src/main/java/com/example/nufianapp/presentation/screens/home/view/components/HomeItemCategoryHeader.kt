package com.example.nufianapp.presentation.screens.home.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nufianapp.R
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.NeonWhite

@Composable
fun HomeItemCategoryHeader(
    userId: String?,
    navigateToNotification: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
        }
        IconButton(
            onClick = {
                if (userId != null) {
                    navigateToNotification(userId)
                }
            }, modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(NeonWhite)
        ) {
            Icon(
                tint = Blue,
                painter = painterResource(id = R.drawable.icon_item_notification),
                contentDescription = "Image Search",
                modifier = Modifier.size(22.dp)
            )
        }

    }
}