package com.example.nufianapp.presentation.screens.discover.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.nufianapp.data.model.User
import com.example.nufianapp.domain.repository.ConnectRepository
import com.example.nufianapp.domain.repository.UserRepository
import com.example.nufianapp.domain.model.Response
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
class ConnectViewModel @Inject constructor(
    private val connectRepository: ConnectRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _pagingFlow: MutableStateFlow<PagingData<User>> =
        MutableStateFlow(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<User>> = _pagingFlow

    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow

    private val _isAdmin: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    init {
        getUser()
        checkUserType()
    }

    private fun getUser() {
        viewModelScope.launch(Dispatchers.IO) {
            connectRepository.getUser()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _pagingFlow.value = pagingData
                }
        }
    }

    private fun checkUserType() {
        viewModelScope.launch {
            val userId = userRepository.currentUser?.uid
            if (userId != null) {
                val response = userRepository.getUserDataById(userId)
                _isAdmin.value = response is Response.Success && response.data?.userType == "admin"
            } else {
                _isAdmin.value = false
            }
        }
    }

    fun onSnackBarShown(message: String) {
        viewModelScope.launch {
            _snackBarFlow.emit(message)
        }
    }
}
