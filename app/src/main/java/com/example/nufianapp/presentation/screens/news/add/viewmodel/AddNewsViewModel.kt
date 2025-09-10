package com.example.nufianapp.presentation.screens.news.add.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nufianapp.data.model.News
import com.example.nufianapp.domain.repository.NewsRepository
import com.example.nufianapp.domain.repository.NotificationRepository
import com.example.nufianapp.domain.model.ErrorUtils
import com.example.nufianapp.domain.model.Response
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
class AddNewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    var addNewsResponse by mutableStateOf<Response<Boolean>>(Response.Success(false))
        private set

    private val _snackBarFlow = MutableSharedFlow<String>()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow.asSharedFlow()

    fun addNews(news: News) {
        if (addNewsResponse is Response.Loading) return // Prevent multiple submissions

        viewModelScope.launch {
            if (news.subject.isEmpty()) {
                _snackBarFlow.emit("Subject cannot be empty")
                return@launch
            }
            if (news.content.isEmpty()) {
                _snackBarFlow.emit("Content cannot be empty")
                return@launch
            }
            addNewsResponse = Response.Loading
            addNewsResponse = try {
                newsRepository.storeNewsData(news)
            } catch (e: Exception) {
                Response.Failure(e)
            }
            if (addNewsResponse is Response.Failure) {
                handleError((addNewsResponse as Response.Failure).e)
            } else {
                _snackBarFlow.emit("News added successfully")
                sendNotificationToAllUsers(news.subject)
            }
        }
    }

    private fun sendNotificationToAllUsers(newsTitle: String) {
        viewModelScope.launch {
            val notificationTitle = "New News Posted"
            val notificationType = "news"

            val result = notificationRepository.sendNotificationToAllUsers(
                notificationTitle, newsTitle, notificationType
            )

            if (result.isSuccess) {
                _snackBarFlow.emit("Notification sent to all users")
            } else {
                _snackBarFlow.emit("Failed to send notification to all users")
            }
        }
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
