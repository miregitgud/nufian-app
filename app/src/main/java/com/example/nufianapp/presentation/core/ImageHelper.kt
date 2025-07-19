package com.example.nufianapp.presentation.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageHelper(private val context: Context) {

    suspend fun cropCenter(imageUri: String?): Bitmap? {
        return withContext(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUri)
                .build()

            val result = (loader.execute(request) as? SuccessResult)?.drawable as? BitmapDrawable
            val bitmap = result?.bitmap
            bitmap?.let { centerCrop(it) }
        }
    }

    private fun centerCrop(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val newWidth = if (width > height) height else width
        val newHeight = if (width > height) height else width

        val x = (width - newWidth) / 2
        val y = (height - newHeight) / 2

        return Bitmap.createBitmap(src, x, y, newWidth, newHeight)
    }
}