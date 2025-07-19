package com.example.nufianapp.data.repository

import androidx.paging.PagingData
import com.example.nufianapp.data.firebase.FireStoreHelper
import com.example.nufianapp.data.firebase.StorageHelper
import com.example.nufianapp.data.model.Project
import com.example.nufianapp.domain.model.ErrorUtils
import com.example.nufianapp.domain.model.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val fireStoreHelper: FireStoreHelper,
    private val storageHelper: StorageHelper,
    userRepository: UserRepository
) : ProjectRepository {

    private val currentUser = userRepository.currentUser?.uid ?: throw Exception(
        ErrorUtils.getFriendlyErrorMessage(Exception("User not logged in"))
    )

    override fun getProjectsByUserId(userId: String): Flow<PagingData<Project>> =
        fireStoreHelper.getProjectByUserId(userId)
            .catch { e ->
                emit(PagingData.empty())
                ErrorUtils.getFriendlyErrorMessage(e)
            }

    override suspend fun storeProject(
        project: Project,
    ): Response<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            val updatedProject = project.projectImageUri?.let {
                val imagePath = "projects/$currentUser/${System.currentTimeMillis()}.jpg"
                val imageUrl = storageHelper.uploadImage(imagePath, it)
                project.copy(userId = currentUser, projectImageUrl = imageUrl)
            } ?: project.copy(userId = currentUser)

            fireStoreHelper.storeUserProject(currentUser, updatedProject.toMap())
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
        }
    }

    override suspend fun updateProject(project: Project): Response<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                fireStoreHelper.updateUserProject(currentUser, project)
                Response.Success(Unit)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }

    override suspend fun deleteProject(projectId: String): Response<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                fireStoreHelper.deleteUserProject(currentUser, projectId)
                Response.Success(Unit)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }

    override suspend fun getProjectByUserAndProjectId(userId: String, projectId: String): Response<Project> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val project = fireStoreHelper.getProjectByUserAndProjectId(userId, projectId)
                Response.Success(project)
            } catch (e: Exception) {
                Response.Failure(Exception(ErrorUtils.getFriendlyErrorMessage(e)))
            }
        }
}
