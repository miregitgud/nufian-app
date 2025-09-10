package com.example.nufianapp.presentation.screens.discover.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.nufianapp.R
import com.example.nufianapp.data.model.User
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Charcoal
import com.example.nufianapp.ui.theme.Graphite
import com.example.nufianapp.ui.theme.Orange
import com.example.nufianapp.ui.theme.Red
import com.example.nufianapp.ui.theme.White

@Composable
fun ConnectItem(
    user: User,
    imageId: Int,
    modifier: Modifier = Modifier,
    currentUserType: String,
    onBanUnbanConfirmed: (Boolean) -> Unit
) {
    val userInterest = user.interest.lowercase()

    val decider = when {
        userInterest.contains("admin") -> user.interest
        else -> user.interest + " " + user.batch + " • " + user.status
    }

    var menuExpanded by remember { mutableStateOf(false) }
    var dialogVisible by remember { mutableStateOf(false) }

    val isBanned = user.isBanned ?: false

    val cardColor = when {
        userInterest.contains("ti") && !isBanned -> Blue
        userInterest.contains("si") && !isBanned -> Orange
        userInterest.contains("bd") && !isBanned -> Red
        userInterest.contains("admin") && !isBanned -> Charcoal
        else -> Graphite
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp),
        ) {
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(color = cardColor)
            )

            // Triple-dot icon & dropdown only if current user is admin
            if (currentUserType.lowercase() == "admin") {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = White
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (!isBanned) "Ban User" else "Unban User",
                                    color = if (!isBanned) Color.Red else Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                dialogVisible = true
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarImage(
                    avatarUrl = user.avatarUrl ?: "",
                    modifier = Modifier.size(64.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = user.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = White,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = decider,
                        style = MaterialTheme.typography.bodyMedium,
                        color = White,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user.bioData.ifEmpty { "No biodata" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        maxLines = 2,
                        minLines = 2
                    )
                }
            }
        }
    }

    // Confirmation dialog
    if (dialogVisible) {
        AlertDialog(
            onDismissRequest = { dialogVisible = false },
            confirmButton = {
                Button(onClick = {
                    dialogVisible = false
                    onBanUnbanConfirmed(!isBanned)
                }) {
                    Text(text = if (!isBanned) "Ban" else "Unban")
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogVisible = false }) {
                    Text("Cancel")
                }
            },
            title = {
                Text(text = if (!isBanned) "Ban User?" else "Unban User?")
            },
            text = {
                Text(
                    text = if (!isBanned)
                        "Are you sure you want to ban ${user.displayName}?"
                    else
                        "Unban ${user.displayName} and allow them access again?"
                )
            }
        )
    }
}



@Composable
fun AvatarImage(
    avatarUrl: String,
    modifier: Modifier = Modifier,
) {
    val painter = if (avatarUrl.isNotEmpty()) {
        rememberAsyncImagePainter(model = avatarUrl)
    } else {
        painterResource(R.drawable.img_avatar_default)
    }

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
            .offset(y = (-8).dp)
            .size(56.dp)
            .border(2.dp, Color.White, CircleShape)
            .clip(CircleShape)
    )
}
