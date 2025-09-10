package com.example.nufianapp.presentation.screens.notification.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nufianapp.data.model.Notification
import com.example.nufianapp.utils.Utils
import com.example.nufianapp.presentation.core.components.ImageAvatarUrl
import com.example.nufianapp.ui.theme.Graphite

@Composable
fun NotificationItem(notification: Notification, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageAvatarUrl(
                isNotification = notification.notificationType,
            )
            Spacer(modifier = modifier.width(16.dp))
            Column(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = modifier.height(8.dp))
                Text(
                    text = Utils().calculateTimeAgo(notification.dateTime),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Graphite
                    )
                )
            }
        }
    }
}
