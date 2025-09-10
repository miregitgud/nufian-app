package com.example.nufianapp.presentation.screens.home.add.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nufianapp.data.model.Forum
import com.example.nufianapp.data.model.ForumCategory
import com.example.nufianapp.domain.repository.ForumRepository
import com.example.nufianapp.domain.repository.UserRepository
import com.example.nufianapp.domain.model.ErrorUtils
import com.example.nufianapp.domain.model.Response
import com.example.nufianapp.presentation.core.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddForumViewModel @Inject constructor(
    private val forumRepository: ForumRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var addForumResponse by mutableStateOf<Response<Boolean>>(Response.Success(false))
        private set

    private var _categories: List<ForumCategory> = Constants.FORUM_CATEGORY
    val categories: List<ForumCategory>
        get() = if (userType == "admin") _categories else _categories.filter { it.name != "Infinite Learning" }

    private val _selectedForumCategory = MutableStateFlow<ForumCategory?>(null)
    val selectedForumCategory: StateFlow<ForumCategory?> = _selectedForumCategory.asStateFlow()

    // New variables to hold user data
    var userType by mutableStateOf("")
    var avatarUrl by mutableStateOf<String?>(null)
    var userId by mutableStateOf("")

    private val _snackBarFlow = MutableSharedFlow<String>()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow.asSharedFlow()

    init {
        // Retrieve user data when the ViewModel is initialized
        viewModelScope.launch {
            when (val userData = userRepository.getUserData()) {
                is Response.Success -> {
                    userType = userData.data.userType
                    avatarUrl = userData.data.avatarUrl
                    userId = userData.data.uid
                }

                is Response.Failure -> {
                    handleError(userData.e)
                }

                Response.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }

    fun addForum(forum: Forum) {
        if (addForumResponse is Response.Loading) return // Prevent multiple submissions

        viewModelScope.launch {
            if (forum.subject.isEmpty()) {
                _snackBarFlow.emit("Subject cannot be empty")
                return@launch
            }
            if (forum.content.isEmpty()) {
                _snackBarFlow.emit("Content cannot be empty")
                return@launch
            }
            addForumResponse = Response.Loading
            addForumResponse = try {
                forumRepository.storeForumData(forum)
                Response.Success(true)
            } catch (e: Exception) {
                Response.Failure(e)
            }
            if (addForumResponse is Response.Failure) {
                handleError((addForumResponse as Response.Failure).e)
            } else {
                _snackBarFlow.emit("Forum added successfully")
            }
        }
    }

    fun selectForumCategory(category: ForumCategory) {
        _selectedForumCategory.value = category
    }

    private val _selectedContentImageUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedContentImageUris: StateFlow<List<Uri>> = _selectedContentImageUris.asStateFlow()

    private var capturedImageUri: Uri? by mutableStateOf(null)

    fun updateCapturedImageUri(uri: Uri?) {
        capturedImageUri = uri
    }

    fun handleTakePictureResult(success: Boolean) {
        viewModelScope.launch {
            if (success) {
                capturedImageUri?.let { uri ->
                    if (_selectedContentImageUris.value.size < 5) {
                        _selectedContentImageUris.value += uri
                    } else {
                        _snackBarFlow.emit("You can't add more than 5 images")
                    }
                }
            } else {
                _snackBarFlow.emit("Failed to take picture")
            }
        }
    }

    fun handleGetContentImagesResult(uris: List<Uri>) {
        viewModelScope.launch {
            if (_selectedContentImageUris.value.size + uris.size <= 5) {
                _selectedContentImageUris.value += uris
            } else {
                val remainingSlots = 5 - _selectedContentImageUris.value.size
                if (remainingSlots > 0) {
                    _selectedContentImageUris.value += uris.take(remainingSlots)
                }
                _snackBarFlow.emit("You can't add more than 5 images")
            }
        }
    }

    private suspend fun handleError(error: Throwable) {
        val errorMessage = ErrorUtils.getFriendlyErrorMessage(error)
        _snackBarFlow.emit(errorMessage)
    }
}
