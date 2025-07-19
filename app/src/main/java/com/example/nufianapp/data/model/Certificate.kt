package com.example.nufianapp.data.model

import android.net.Uri
import java.util.Date

data class Certificate(
    val certificateId: String = "",
    val userId: String = "",
    val name: String = "",
    val organization: String = "",
    val startDate: String = "",
    val endDate: String = "",
    var certificateImageUrl: String = "",
    val certificateImageUri: Uri? = null, // Specify nullable type explicitly
    val credentialId: String = "",
    val createdAt: Date = Date()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "certificateId" to certificateId,
            "userId" to userId,
            "name" to name,
            "organization" to organization,
            "startDate" to startDate,
            "endDate" to endDate,
            "certificateImageUrl" to certificateImageUrl,
            "credentialId" to credentialId,
            "createdAt" to createdAt
        )
    }
}
