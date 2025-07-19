package com.example.nufianapp.data.model

import android.net.Uri
import java.util.Date

data class Project(
    val projectId: String = "",
    val projectOwner: String = "",
    val userId: String = "",
    var projectImageUrl: String = "",
    val projectImageUri: Uri? = null,
    val projectName: String = "",
    val description: String = "",
    val linkProject: String = "",
    val createdAt: Date = Date()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "projectId" to projectId,
            "userId" to userId,
            "projectOwner" to projectOwner,
            "projectImageUrl" to projectImageUrl,
            "projectName" to projectName,
            "description" to description,
            "linkProject" to linkProject,
            "createdAt" to createdAt
        )
    }
}
