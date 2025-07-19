package com.example.nufianapp.presentation.screens.profile.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.nufianapp.data.model.Project
import com.example.nufianapp.data.repository.ProjectRepository
import com.example.nufianapp.data.repository.UserRepository
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.GlideImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

    private val _pagingFlow: MutableStateFlow<PagingData<Project>> =
        MutableStateFlow(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<Project>> = _pagingFlow

    private val _project = MutableStateFlow<Project?>(null)
    val project: StateFlow<Project?> get() = _project

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow

    private fun getProjectWithPreview() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = userRepository.currentUser?.uid ?: return@launch
            projectRepository.getProjectsByUserId(userId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    pagingData.map { project ->
                        GlideImageLoader.preloadImage(
                            applicationContext,
                            project.projectImageUrl
                        )
                    }
                    _pagingFlow.value = pagingData
                }
        }
    }

    private fun getProjectWithoutPreview(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            projectRepository.getProjectsByUserId(userId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    pagingData.map { project ->
                        GlideImageLoader.preloadImage(
                            applicationContext,
                            project.projectImageUrl
                        )
                    }
                    _pagingFlow.value = pagingData
                }
        }
    }

    fun getPagingFlow(isProfile: Boolean, userId: String?) {
        if (isProfile) {
            getProjectWithPreview()
        } else {
            if (userId != null) {
                getProjectWithoutPreview(userId)
            }
        }
    }

    fun fetchProject(userId: String, projectId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = projectRepository.getProjectByUserAndProjectId(userId, projectId)) {
                is Response.Success -> _project.value = result.data
                is Response.Failure -> _error.value = "hello"
                Response.Loading -> TODO()
            }
            _isLoading.value = false
        }
    }


    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            val response = projectRepository.deleteProject(projectId)
            if (response is Response.Success) {
                onSnackBarShown("Project deleted successfully")
                getProjectWithPreview() // Refresh the project list
            } else if (response is Response.Failure) {
                onSnackBarShown(response.e.toString())
            }
        }
    }

    fun onSnackBarShown(message: String) {
        viewModelScope.launch {
            _snackBarFlow.emit(message)
        }
    }
}