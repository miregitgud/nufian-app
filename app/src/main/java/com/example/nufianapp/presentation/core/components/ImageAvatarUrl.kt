package com.example.nufianapp.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.nufianapp.R

@Composable
fun ImageAvatarUrl(
    isNotification: String = "news",
    modifier: Modifier = Modifier
) {
    val darkColors = listOf(
        Color(0xFF002AC4), // Lightened Midnight Blue
        Color(0xFF2CA500),
        Color(0xFF64504C), // Lightened Magenta
        Color(0xFFFF5722), // Lightened Gold
        Color(0xFF7125FF)
    )
    val lightColors = listOf(
        Color(0xFF00FFFF),
        Color(0xFF9DFFA1),
        Color(0xFFAD9F9C),
        Color(0xFFFFAB91),
        Color(0xFFC1A0FF)
    )

    // Determine background color and icon based on isNotification value
    val (backgroundColor, iconResource) = when (isNotification) {
        "like" -> darkColors[1] to R.drawable.icon_item_like
        "comment" -> darkColors[2] to R.drawable.icon_item_comment
        "news" -> darkColors[3 ] to R.drawable.ic_menu_news_selected
        else -> darkColors[4] to R.drawable.icon_item_notification
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Load appropriate icon based on notification type
        Icon(
            painter = painterResource(id = iconResource),
            contentDescription = null,
            tint = Color.White,
            modifier = modifier
        )
    }
}
