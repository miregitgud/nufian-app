package com.example.nufianapp.presentation.screens.notification.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.nufianapp.data.model.Notification
import com.example.nufianapp.domain.repository.NotificationRepository
import com.example.nufianapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
class NotificationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _pagingFlow: MutableStateFlow<PagingData<Notification>> =
        MutableStateFlow(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<Notification>> = _pagingFlow


    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow

    init {
        getNotificationByUserId()
    }

    private fun getCurrentUserId(): String? {
        return userRepository.currentUser?.uid
    }

    private fun getNotificationByUserId() {
        viewModelScope.launch(Dispatchers.IO) {
            getCurrentUserId()?.let {
                notificationRepository.getNotificationByUserId(it)
                    .distinctUntilChanged()
                    .cachedIn(viewModelScope)
                    .collectLatest { pagingData ->
                        _pagingFlow.value = pagingData
                    }
            }
        }
    }

    fun onSnackBarShown(message: String) {
        viewModelScope.launch {
            _snackBarFlow.emit(message)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = getCurrentUserId()
            if (userId != null) {
                try {
                    notificationRepository.clearAllNotificationsForUser(userId)
                    _snackBarFlow.emit("All notifications cleared.")
                    // Refresh notification list
                    getNotificationByUserId()
                } catch (e: Exception) {
                    _snackBarFlow.emit("Failed to clear notifications.")
                }
            }
        }
    }

}

