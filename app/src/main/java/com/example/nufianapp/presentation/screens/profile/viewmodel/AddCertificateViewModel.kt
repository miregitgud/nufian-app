package com.example.nufianapp.presentation.screens.profile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nufianapp.data.model.Certificate
import com.example.nufianapp.domain.repository.CertificateRepository
import com.example.nufianapp.domain.model.ErrorUtils
import com.example.nufianapp.domain.model.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCertificateViewModel @Inject constructor(
    private val certificateRepository: CertificateRepository
) : ViewModel() {

    var addCertificateResponse by mutableStateOf<Response<Boolean>>(Response.Success(false))
        private set

    private val _snackBarFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow

    fun addCertificate(certificate: Certificate) {
        if (addCertificateResponse is Response.Loading) return // Prevent multiple submissions

        viewModelScope.launch {
            when {
                certificate.name.isEmpty() -> _snackBarFlow.emit("Subject cannot be empty")
                certificate.organization.isEmpty() -> _snackBarFlow.emit("Content cannot be empty")
                certificate.credentialId.isEmpty() -> _snackBarFlow.emit("Credential Id cannot be empty")
                certificate.certificateImageUri == null -> _snackBarFlow.emit("Certificate Image Uri cannot be null")
                else -> {
                    addCertificateResponse = Response.Loading
                    val response = certificateRepository.storeCertificate(certificate)
                    addCertificateResponse = response
                    when (response) {
                        is Response.Success -> _snackBarFlow.emit("Certificate added successfully")
                        is Response.Failure -> handleError(response.e)
                        Response.Loading -> TODO()
                    }
                }
            }
        }
    }

    private suspend fun handleError(error: Throwable) {
        val errorMessage = ErrorUtils.getFriendlyErrorMessage(error)
        _snackBarFlow.emit(errorMessage)
    }
}
