package com.example.nufianapp.domain.repository

import androidx.paging.PagingData
import com.example.nufianapp.data.model.Project
import com.example.nufianapp.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    suspend fun storeProject(project: Project): AddForumResponse
    fun getProjectsByUserId(userId: String): Flow<PagingData<Project>>
    suspend fun updateProject(project: Project): Response<Unit>
    suspend fun deleteProject(projectId: String): Response<Unit>
    suspend fun getProjectByUserAndProjectId(userId: String, projectId: String): Response<Project>
}