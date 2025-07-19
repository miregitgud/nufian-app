package com.example.nufianapp.data.firebase

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.logging.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageHelper @Inject constructor(
    storage: FirebaseStorage
) {
    private val storageRef = storage.reference
    private val logger = Logger.getLogger(StorageHelper::class.java.name)

    suspend fun uploadImage(imagePath: String, imageUri: Uri?): String {
        return imageUri?.let {
            try {
                val imageRef = storageRef.child(imagePath)
                val uploadTask = imageRef.putFile(it)
                uploadTask.await()
                imageRef.downloadUrl.await().toString()
            } catch (e: Exception) {
                logger.severe("Error uploading image: ${e.message}")
                throw ImagesUploadException("Failed to upload image", e)
            }
        } ?: throw IllegalArgumentException("Image Uri cannot be null")
    }

    suspend fun deleteImageByUrl(url: String) {
        val ref = Firebase.storage.getReferenceFromUrl(url)
        ref.delete().await()
    }

    suspend fun uploadImages(imagePaths: List<String>, imageUris: List<Uri>): List<String> {
        require(imagePaths.size == imageUris.size) { "Image paths and URIs lists must have the same size" }

        val downloadUrls = mutableListOf<String>()

        try {
            imagePaths.forEachIndexed { index, imagePath ->
                val imageUri = imageUris[index]
                val imageRef = storageRef.child(imagePath)
                val uploadTask = imageRef.putFile(imageUri)
                uploadTask.await()

                val downloadUrl = imageRef.downloadUrl.await().toString()
                downloadUrls.add(downloadUrl)
            }
        } catch (e: Exception) {
            logger.severe("Error uploading images: ${e.message}")
            throw ImagesUploadException("Failed to upload images", e)
        }

        return downloadUrls
    }
}

class ImagesUploadException(message: String, cause: Throwable? = null) : Exception(message, cause)
