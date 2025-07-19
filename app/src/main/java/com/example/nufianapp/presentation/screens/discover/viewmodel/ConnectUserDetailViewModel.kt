package com.example.nufianapp.presentation.screens.discover.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nufianapp.data.model.User
import com.example.nufianapp.data.repository.ConnectRepository
import com.example.nufianapp.domain.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectUserDetailViewModel @Inject constructor(
    private val connectRepository: ConnectRepository
) : ViewModel() {

    private val _userByIdData = MutableStateFlow<User?>(null)
    val userByIdData: StateFlow<User?> = _userByIdData

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    // Fetch user data by ID
    fun fetchUserData(userId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            val response = connectRepository.getUserDataById(userId)
            handleUserDataResponse(response)
        }
    }

    // Handle response for fetching user data by ID
    private fun handleUserDataResponse(response: Response<User?>) {
        when (response) {
            is Response.Success -> {
                _loadingState.value = false
                _userByIdData.value = response.data
            }

            is Response.Failure -> {
                _loadingState.value = false
                _errorState.value = response.e.message ?: "Unknown error"
            }

            Response.Loading -> {
                // Handle loading state if needed
            }
        }
    }

}
