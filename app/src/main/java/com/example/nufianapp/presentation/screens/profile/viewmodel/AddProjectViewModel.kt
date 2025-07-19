package com.example.nufianapp.presentation.screens.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nufianapp.data.model.Project
import com.example.nufianapp.data.repository.ProjectRepository
import com.example.nufianapp.domain.model.ErrorUtils
import com.example.nufianapp.domain.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    var addProjectResponse by mutableStateOf<Response<Boolean>>(Response.Success(false))
        private set

    fun addProject(project: Project) {
        if (addProjectResponse is Response.Loading) return // Prevent multiple submissions

        viewModelScope.launch {
            if (project.projectName.isEmpty()) {
                _snackBarFlow.emit("Name cannot be empty")
                return@launch
            }
            if (project.description.isEmpty()) {
                _snackBarFlow.emit("Description cannot be empty")
                return@launch
            }
            if (project.linkProject.isEmpty()) {
                _snackBarFlow.emit("Link Project Id cannot be empty")
                return@launch
            }
            if (project.projectImageUri == null) {
                _snackBarFlow.emit("Project Image Uri cannot be null")
                return@launch
            }
            addProjectResponse = Response.Loading
            addProjectResponse = try {
                projectRepository.storeProject(project)
            } catch (e: Exception) {
                Response.Failure(e)
            }
            if (addProjectResponse is Response.Failure) {
                handleError((addProjectResponse as Response.Failure).e)
            } else {
                _snackBarFlow.emit("Forum added successfully")
            }
        }
    }

    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow

    private suspend fun handleError(error: Throwable) {
        val errorMessage = ErrorUtils.getFriendlyErrorMessage(error)
        _snackBarFlow.emit(errorMessage)
    }
}
