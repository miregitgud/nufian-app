package com.example.nufianapp.presentation.screens.profile.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.nufianapp.data.model.Certificate
import com.example.nufianapp.data.repository.CertificateRepository
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
class CertificateViewModel @Inject constructor(
    private val certificateRepository: CertificateRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

    private val _pagingFlow: MutableStateFlow<PagingData<Certificate>> =
        MutableStateFlow(PagingData.empty())
    val pagingFlow: StateFlow<PagingData<Certificate>> = _pagingFlow

    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow

    private fun getCertificateWithPreview(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            certificateRepository.getCertificatesByUserId(userId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    pagingData.map { certificate ->
                        GlideImageLoader.preloadImage(
                            applicationContext,
                            certificate.certificateImageUrl
                        )
                    }
                    _pagingFlow.value = pagingData
                }
        }
    }

    private fun getCertificateWithoutPreview(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            certificateRepository.getCertificatesByUserId(userId)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _pagingFlow.value = pagingData
                }
        }
    }

    fun getPagingFlow(isProfile: Boolean, userId: String?) {
        viewModelScope.launch {
            _pagingFlow.value = PagingData.empty() // Clear existing data
            val currentUserId = userRepository.currentUser?.uid ?: return@launch
            if (isProfile) {
                getCertificateWithPreview(currentUserId)
            } else {
                userId?.let {
                    getCertificateWithoutPreview(it)
                }
            }
        }
    }

    fun deleteCertificate(certificateId: String) {
        viewModelScope.launch {
            val response = certificateRepository.deleteCertificate(certificateId)
            if (response is Response.Success) {
                onSnackBarShown("Certificate deleted successfully")
                getPagingFlow(true, null) // Refresh the certificate list
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