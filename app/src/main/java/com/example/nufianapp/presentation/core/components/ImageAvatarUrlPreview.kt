package com.example.nufianapp.presentation.core.components

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.nufianapp.R
import com.example.nufianapp.data.model.User
import com.example.nufianapp.presentation.core.GlideImageLoader
import com.example.nufianapp.ui.theme.Blue
import com.example.nufianapp.ui.theme.Charcoal
import com.example.nufianapp.ui.theme.Graphite
import com.example.nufianapp.ui.theme.Orange
import com.example.nufianapp.ui.theme.Red

@Composable
fun ImageAvatarUrlPreview(
    avatarUrl: String?,
    isAdmin: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current

        val defaultAvatar =
            if (isAdmin) R.drawable.img_avatar_company else R.drawable.img_avatar_default

        AndroidView(
            modifier = modifier
                .size(48.dp)
                .background(Graphite, CircleShape)
                .shadow(4.dp, CircleShape)
                .clickable {
                    onClick?.invoke()
                },
            factory = { applicationContext ->
                ImageView(applicationContext).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    GlideImageLoader.loadImage(
                        context = applicationContext,
                        url = avatarUrl,
                        imageView = this
                    )
                }
            },
            update = { imageView ->
                if (avatarUrl.isNullOrEmpty()) {
                    imageView.setImageResource(defaultAvatar)
                } else {
                    GlideImageLoader.loadImage(
                        context = context,
                        url = avatarUrl,
                        imageView = imageView
                    )
                }
            }
        )
    }
}