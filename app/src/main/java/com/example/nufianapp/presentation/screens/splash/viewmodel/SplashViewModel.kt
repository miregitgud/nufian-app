package com.example.nufianapp.presentation.screens.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nufianapp.domain.repository.AuthRepository
import com.example.nufianapp.domain.repository.UserRepository
import com.example.nufianapp.data.store.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val firstTimeRepository: DataStoreRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(false) // Assuming false means signed out
    val authState: StateFlow<Boolean> get() = _authState

    val isEmailVerified get() = userRepository.currentUser?.isEmailVerified ?: false


    private val _isOnboardingComplete = MutableStateFlow(false)
    val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete

    init {
        viewModelScope.launch {
            firstTimeRepository.readOnBoardingState().collect { completed ->
                _isOnboardingComplete.value = completed
                if (completed) {
                    authRepository.getAuthState(viewModelScope).collect { state ->
                        _authState.value = state
                    }
                }
            }
        }
    }
}

