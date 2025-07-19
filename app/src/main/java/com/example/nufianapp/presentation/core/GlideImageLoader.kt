package com.example.nufianapp.presentation.core

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.nufianapp.R

object GlideImageLoader {

    private val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(R.drawable.img_cache_error) // Error image placeholder if loading fails

    fun loadImage(context: Context, url: String?, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .apply(requestOptions)
            .into(imageView)
    }

    fun preloadImage(context: Context, url: String?) {
        Glide.with(context)
            .load(url)
            .apply(requestOptions)
            .preload()
    }

    // Preload a list of image URLs
    fun preloadImages(context: Context, urls: List<String?>) {
        for (url in urls) {
            if (!url.isNullOrBlank()) {
                Glide.with(context)
                    .load(url)
                    .apply(requestOptions)
                    .preload()
            }
        }
    }

    // Additional function to clear an ImageView to avoid potential memory leaks
    fun clearImage(context: Context, imageView: ImageView) {
        Glide.with(context).clear(imageView)
    }
}