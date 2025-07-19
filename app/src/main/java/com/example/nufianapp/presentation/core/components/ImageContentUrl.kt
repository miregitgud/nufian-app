package com.example.nufianapp.presentation.core.components

import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.nufianapp.R

@Composable
fun ImageContentUrl(
    modifier: Modifier = Modifier,
    contentUrl: String?,
    defaultDrawableResId: Int = R.drawable.grey_rectangle
) {
    val context = LocalContext.current
    AndroidView(
        factory = { contextImage ->
            ImageView(contextImage).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        modifier = modifier,
        update = { imageView ->
            val options = RequestOptions()
                .placeholder(defaultDrawableResId)
                .error(defaultDrawableResId)

            if (contentUrl.isNullOrEmpty()) {
                imageView.setImageResource(defaultDrawableResId)
            } else {
                Glide.with(context)
                    .load(contentUrl)
                    .apply(options)
                    .into(imageView)
            }
        }
    )
}