package com.example.nufianapp.presentation.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nufianapp.data.model.User
import com.example.nufianapp.ui.theme.Charcoal

@Composable
fun UserAvatarNameProfile(
    onClick: () -> Unit,
    user: User,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {

    Row(
        verticalAlignment = verticalAlignment,
    ) {
        Box {
            Row(
                verticalAlignment = verticalAlignment,
            ) {
                ImageAvatarUrlPreview(
                    onClick = { onClick() },
                    avatarUrl = user.avatarUrl,
                    isAdmin = user.userType == "admin",
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = user.displayName.ifEmpty { "Deleted User" },
                        style = MaterialTheme.typography.labelMedium,
                        color = Charcoal,
                    )
                    Text(
                        text = if (user.userType == "admin") {
                            user.interest
                        }
                        else if (user.uid == "") {
                            "User not found"
                        }
                        else {
                            user.interest + " " + user.batch + " • " + user.status
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Charcoal,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}